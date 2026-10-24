package com.littlemakers.village.nativeapp;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** Actual mesh-rendered village: geometry, depth buffer, camera, lights and object picking. */
final class VillageSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
 interface Listener {void chosen(String route);void unavailable();}
 private final Listener listener;
 private final boolean reduced,dark;
 private final VillageSceneState state=new VillageSceneState();
 private Mesh sphere,box,cone,cylinder;private ForestBackdrop forest;
 private final float[] projection=new float[16],view=new float[16],model=new float[16],vp=new float[16],mvp=new float[16],normalMatrix=new float[9];
 private final float[] landmarkPoint=new float[4];
 private final float[] screenX=new float[5],screenY=new float[5],point=new float[4];
 private int program,posAttr,normalAttr,mvpUniform,modelUniform,colorUniform,normalUniform,width,height;
 private long lastFrame;private float downX,downY,lastX;private boolean dragged,opening;private final int slop;
 private final Runnable openSelected;
 VillageSurface(Context c,boolean dark,boolean reduced,Listener listener){
  super(c);this.listener=listener;openSelected=()->{opening=false;if(isAttachedToWindow()&&state.selected>=0)this.listener.chosen(VillageSceneState.ROUTES[state.selected]);};this.dark=dark;this.reduced=reduced;slop=ViewConfiguration.get(c).getScaledTouchSlop();
  setEGLContextClientVersion(2);setEGLConfigChooser(8,8,8,0,16,0);setPreserveEGLContextOnPause(true);setRenderer(this);
  setRenderMode(reduced?RENDERMODE_WHEN_DIRTY:RENDERMODE_CONTINUOUSLY);
  setContentDescription("Three-dimensional village. Drag to look around or tap a landmark. The destination buttons below provide the same navigation.");
 }
 boolean reducedMotion(){return reduced;}
 private int shader(int type,String code){int shader=GLES20.glCreateShader(type);GLES20.glShaderSource(shader,code);GLES20.glCompileShader(shader);int[]ok={0};GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,ok,0);if(ok[0]==0)throw new IllegalStateException("Village shader failed");return shader;}
 @Override public void onSurfaceCreated(GL10 gl,EGLConfig config){
  try{
   String vs="uniform mat4 uMvp;uniform mat4 uModel;uniform mat3 uNormal;attribute vec3 aPosition;attribute vec3 aNormal;varying vec3 vNormal;varying vec3 vPosition;void main(){vec4 world=uModel*vec4(aPosition,1.);vPosition=world.xyz;vNormal=normalize(uNormal*aNormal);gl_Position=uMvp*vec4(aPosition,1.);}";
   String fs="precision mediump float;uniform vec3 uColor;varying vec3 vNormal;varying vec3 vPosition;void main(){vec3 n=normalize(vNormal);vec3 light=normalize(vec3(-.6,1.,.7));float diffuse=max(dot(n,light),0.);float shine=pow(max(dot(n,normalize(light+vec3(0.,.6,1.))),0.),32.);vec3 c=uColor*(.65+.35*diffuse)+vec3(.22)*shine;gl_FragColor=vec4(c,1.);}";
   program=GLES20.glCreateProgram();int vertex=shader(GLES20.GL_VERTEX_SHADER,vs),fragment=shader(GLES20.GL_FRAGMENT_SHADER,fs);GLES20.glAttachShader(program,vertex);GLES20.glAttachShader(program,fragment);GLES20.glLinkProgram(program);GLES20.glDeleteShader(vertex);GLES20.glDeleteShader(fragment);int[]linked={0};GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linked,0);if(linked[0]==0)throw new IllegalStateException("Village program failed");
   posAttr=GLES20.glGetAttribLocation(program,"aPosition");normalAttr=GLES20.glGetAttribLocation(program,"aNormal");mvpUniform=GLES20.glGetUniformLocation(program,"uMvp");modelUniform=GLES20.glGetUniformLocation(program,"uModel");colorUniform=GLES20.glGetUniformLocation(program,"uColor");normalUniform=GLES20.glGetUniformLocation(program,"uNormal");
   sphere=Mesh.sphere();box=Mesh.box();cone=Mesh.round(true);cylinder=Mesh.round(false);forest=new ForestBackdrop(getContext());forest.create();GLES20.glEnable(GLES20.GL_DEPTH_TEST);lastFrame=0;
  }catch(RuntimeException unsupported){program=0;post(listener::unavailable);}
 }
 @Override public void onSurfaceChanged(GL10 gl,int w,int h){width=w;height=h;GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(projection,0,43,(float)w/Math.max(1,h),.1f,60);}
 private void draw(Mesh mesh,float x,float y,float z,float sx,float sy,float sz,int color,float angle){
  Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);if(angle!=0)Matrix.rotateM(model,0,angle,0,1,0);Matrix.scaleM(model,0,sx,sy,sz);Matrix.multiplyMM(mvp,0,vp,0,model,0);
  for(int column=0;column<3;column++){float scale=column==0?sx:column==1?sy:sz;for(int row=0;row<3;row++)normalMatrix[column*3+row]=model[column*4+row]/(scale*scale);}GLES20.glUniformMatrix3fv(normalUniform,1,false,normalMatrix,0);GLES20.glUniformMatrix4fv(mvpUniform,1,false,mvp,0);GLES20.glUniformMatrix4fv(modelUniform,1,false,model,0);GLES20.glUniform3f(colorUniform,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f);
  mesh.data.position(0);GLES20.glVertexAttribPointer(posAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);mesh.data.position(3);GLES20.glVertexAttribPointer(normalAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mesh.count);
 }
 private void ball(float x,float y,float z,float r,int color){draw(sphere,x,y,z,r,r,r,color,0);}
 private void tree(float x,float z,float height,float sway){draw(cylinder,x,.3f,z,.13f,height,.13f,0x9b7057,0);ball(x+sway,height+.2f,z,.65f,0x76b696);ball(x-.35f+sway,height-.05f,z+.15f,.48f,0x9fd09b);}
 @Override public void onDrawFrame(GL10 gl){
  long now=android.os.SystemClock.uptimeMillis();synchronized(state){state.advance(lastFrame==0?0:(now-lastFrame)/1000f,reduced);}lastFrame=now;
  GLES20.glClearColor(dark?.09f:.78f,dark?.12f:.9f,dark?.22f:.96f,1);GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);if(program==0)return;
  if(forest!=null)forest.draw(width,height,state.yaw,dark);GLES20.glUseProgram(program);GLES20.glEnableVertexAttribArray(posAttr);GLES20.glEnableVertexAttribArray(normalAttr);float t=state.seconds;double yaw=Math.toRadians(state.yaw);Matrix.setLookAtM(view,0,(float)Math.sin(yaw)*11,9,(float)Math.cos(yaw)*11,0,.1f,0,0,1,0);Matrix.multiplyMM(vp,0,projection,0,view,0);
  draw(sphere,0,-1.0f,0,6.4f,.55f,4.8f,0x74c8db,0);draw(sphere,0,-.7f,0,5.9f,.58f,4.25f,0x9fca8d,0);draw(sphere,0,-.8f,0,5.6f,.35f,4.15f,0x78a984,0);
  for(int i=0;i<10;i++){float x=-3.5f+i*.78f;draw(box,x,-.14f,1.35f,.47f,.03f,.25f,0xf2dca9,12);}
  for(int i=0;i<5;i++){
   float x=VillageSceneState.PLACES[i][0],z=VillageSceneState.PLACES[i][2],b=state.bounce(i,reduced);
   draw(sphere,x,-.16f,z,1.15f,.055f,.85f,0x82af88,0);
   if(i==0){draw(cone,x,.85f+b,z,1.2f,1.2f,1.2f,0xd59a85,0);draw(cone,x,1.65f+b,z,.49f,.48f,.49f,0xffdbc0,0);draw(cylinder,x,2.12f+b,z,.18f,.04f,.18f,0x996d68,0);ball(x+.1f*(float)Math.sin(t),2.45f+b,z,.18f,0xffe0b5);}
   if(i==1){draw(box,x,1.2f+b,z,.78f,.83f,.10f,0xf1cf89,-10);draw(box,x,1.2f+b,z+.12f,.66f,.7f,.025f,0xfff9e8,-10);draw(cylinder,x-.5f,.4f+b,z,.06f,.7f,.06f,0xa57755,0);draw(cylinder,x+.5f,.4f+b,z,.06f,.7f,.06f,0xa57755,0);ball(x-.2f,1.4f+b,z+.2f,.22f,0xec9caf);ball(x+.28f,1.12f+b,z+.2f,.24f,0x78bad6);ball(x-.22f,.95f+b,z+.2f,.18f,0xa7c982);}
   if(i==2){draw(cylinder,x,.5f+b,z,.85f,.6f,.85f,0xa7cfe5,0);draw(sphere,x,1.1f+b,z,.9f,.65f,.9f,0x897abb,0);draw(box,x,1.6f+b,z,.3f,.3f,.3f,0xf2ca78,t*22);draw(sphere,x+.8f,1.6f+b+(float)Math.sin(t)*.12f,z,.22f,.22f,.22f,0xec9caf,0);}
   if(i==3){tree(x,z,1.7f+b,(float)Math.sin(t)*.08f);draw(box,x,1.2f+b,z+.4f,.65f,.48f,.45f,0xffedbd,0);draw(cone,x,1.9f+b,z+.4f,.95f,.45f,.75f,0xd993a4,45);draw(box,x,1.15f+b,z+.87f,.16f,.3f,.025f,0x9d81b1,0);}
   if(i==4){draw(box,x,.05f+b,z,.9f,.16f,.7f,0xd0a879,0);for(int f=0;f<3;f++){float fx=x-.5f+f*.48f;draw(cylinder,fx,.45f+b,z,.045f,.35f,.045f,0x68a26b,0);ball(fx,.85f+b+(float)Math.sin(t+f)*.05f,z,.23f,f==1?0xf2cb71:0xe5a1ad);}}
   landmarkPoint[0]=x;landmarkPoint[1]=1.2f+b;landmarkPoint[2]=z;landmarkPoint[3]=1;Matrix.multiplyMV(point,0,vp,0,landmarkPoint,0);synchronized(screenX){screenX[i]=(point[0]/point[3]*.5f+.5f)*width;screenY[i]=(.5f-point[1]/point[3]*.5f)*height;}
   if(!reduced&&state.selected==i&&state.responseAge<.75f)for(int j=0;j<7;j++){double a=j*Math.PI*2/7;float r=.6f+state.responseAge*1.8f;ball(x+(float)Math.cos(a)*r,1.4f+b+(float)Math.sin(a)*r,z,.065f,0xffedb7);}
  }
  tree(-4.4f,.4f,1.2f,(float)Math.sin(t*.9f)*.07f);tree(4.5f,.1f,1.35f,(float)Math.sin(t*.8f)*.08f);
  for(int i=0;i<3;i++){float x=-4+i*3.8f+(float)Math.sin(t*.12f+i)*.45f;draw(sphere,x,3.5f+i%2*.6f,-3.4f,1,.24f,.4f,0xffffff,0);ball(x-.45f,3.6f+i%2*.6f,-3.4f,.38f,0xffffff);}
  for(int bird=0;bird<2;bird++){float phase=t*.42f+bird*2.4f,bx=(float)Math.sin(phase)*3.7f,by=3.0f+bird*.5f,bz=-2.3f+(float)Math.cos(phase);float flap=reduced?0:(float)Math.sin(t*6+bird)*.13f;draw(sphere,bx,by,bz,.13f,.09f,.17f,0xffd778,0);draw(sphere,bx-.18f,by+flap,bz,.18f,.035f,.1f,0xf3b7c2,-20);draw(sphere,bx+.18f,by+flap,bz,.18f,.035f,.1f,0xf3b7c2,20);}float butterflyX=(float)Math.sin(t*.8f)*.9f,butterflyZ=.3f+(float)Math.cos(t*.8f)*.7f;float wing=reduced?.15f:.05f+Math.abs((float)Math.sin(t*7))*.18f;draw(sphere,butterflyX,1.6f,butterflyZ,.035f,.11f,.035f,0x574b77,0);draw(sphere,butterflyX-wing,1.65f,butterflyZ,wing,.12f,.045f,0xe3a8d2,0);draw(sphere,butterflyX+wing,1.65f,butterflyZ,wing,.12f,.045f,0x91c7df,0);float bob=reduced?0:(float)Math.sin(t*2)*.1f;draw(sphere,0,.28f+bob,2.6f,.46f,.5f,.37f,0xf1c96d,0);float eyeHeight=!reduced&&t%6>5.85f?.012f:.045f;draw(sphere,-.16f,.44f+bob,2.94f,.045f,eyeHeight,.03f,0x30344b,0);draw(sphere,.16f,.44f+bob,2.94f,.045f,eyeHeight,.03f,0x30344b,0);ball(-.29f,.28f+bob,2.9f,.065f,0xe79c9c);ball(.29f,.28f+bob,2.9f,.065f,0xe79c9c);for(int i=0;i<5;i++){float x=(i-2)*.045f;ball(x,.2f+bob+x*x*3,2.96f,.022f,0x30344b);}draw(sphere,-.5f,.3f+bob,2.62f,.16f,.09f,.12f,0xf1c96d,0);draw(sphere,.5f,.3f+bob+(reduced?0:(float)Math.sin(t*3)*.07f),2.62f,.16f,.09f,.12f,0xf1c96d,0);draw(sphere,-.2f,-.12f,2.75f,.17f,.09f,.22f,0xe8b55e,0);draw(sphere,.2f,-.12f,2.75f,.17f,.09f,.22f,0xe8b55e,0);
 }
 @Override public boolean onTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN){downX=lastX=e.getX();downY=e.getY();dragged=false;return true;}if(e.getActionMasked()==MotionEvent.ACTION_MOVE){if(Math.abs(e.getX()-downX)>slop&&Math.abs(e.getX()-downX)>Math.abs(e.getY()-downY)){dragged=true;getParent().requestDisallowInterceptTouchEvent(true);synchronized(state){state.turn((e.getX()-lastX)*.12f);}requestRender();}lastX=e.getX();return true;}if(e.getActionMasked()==MotionEvent.ACTION_UP){getParent().requestDisallowInterceptTouchEvent(false);performClick();if(!dragged&&!opening){int choice=-1;float best=Math.max(getResources().getDisplayMetrics().density*55,width*.12f);synchronized(screenX){for(int i=0;i<5;i++){float d=(float)Math.hypot(e.getX()-screenX[i],e.getY()-screenY[i]);if(d<best){best=d;choice=i;}}}if(choice>=0){synchronized(state){state.select(choice);}opening=true;performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);requestRender();postDelayed(openSelected,reduced?0:420);}}return true;}if(e.getActionMasked()==MotionEvent.ACTION_CANCEL)getParent().requestDisallowInterceptTouchEvent(false);return true;}
 @Override public boolean performClick(){super.performClick();return true;}
 @Override protected void onDetachedFromWindow(){removeCallbacks(openSelected);super.onDetachedFromWindow();}
 private static final class Mesh {
  final FloatBuffer data;final int count;Mesh(float[]v){data=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();data.put(v).position(0);count=v.length/6;}
  static void vertex(float[]v,int[]offset,float x,float y,float z,float nx,float ny,float nz){int i=offset[0];v[i++]=x;v[i++]=y;v[i++]=z;v[i++]=nx;v[i++]=ny;v[i++]=nz;offset[0]=i;}
  static Mesh sphere(){float[]v=new float[10*16*6*6];int[]o={0};for(int a=0;a<10;a++)for(int b=0;b<16;b++)for(int corner:new int[]{0,1,2,0,2,3}){double lat=Math.PI*(a+(corner==1||corner==2?1:0))/10,lon=2*Math.PI*(b+(corner>=2?1:0))/16;float x=(float)(Math.sin(lat)*Math.cos(lon)),y=(float)Math.cos(lat),z=(float)(Math.sin(lat)*Math.sin(lon));vertex(v,o,x,y,z,x,y,z);}return new Mesh(v);}
  static Mesh box(){float[]v=new float[36*6];int[]o={0};for(int axis=0;axis<3;axis++)for(int sign:new int[]{-1,1})for(int c:new int[]{0,1,2,0,2,3}){float[]p={0,0,0},n={0,0,0};p[axis]=n[axis]=sign;p[(axis+1)%3]=c==0||c==3?-1:1;p[(axis+2)%3]=c<2?-1:1;vertex(v,o,p[0],p[1],p[2],n[0],n[1],n[2]);}return new Mesh(v);}
  static Mesh round(boolean tip){float[]v=new float[24*12*6];int[]o={0};for(int i=0;i<24;i++){double a=i*Math.PI/12,b=(i+1)*Math.PI/12;float ax=(float)Math.cos(a),az=(float)Math.sin(a),bx=(float)Math.cos(b),bz=(float)Math.sin(b),top=tip?0:1;float[][]p={{ax,-1,az},{bx,-1,bz},{bx*top,1,bz*top},{ax,-1,az},{bx*top,1,bz*top},{ax*top,1,az*top},{0,-1,0},{bx,-1,bz},{ax,-1,az},{0,1,0},{ax*top,1,az*top},{bx*top,1,bz*top}};for(int j=0;j<12;j++){float[]q=p[j];float ny=j>=9?1:j>=6?-1:tip?.5f:0;vertex(v,o,q[0],q[1],q[2],j>=6?0:q[0],ny,j>=6?0:q[2]);}}return new Mesh(v);}
 }
}
