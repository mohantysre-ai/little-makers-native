package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.opengl.*;
import android.view.*;
import java.nio.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/** Guided third-person exploration: real world-space movement, follow camera and ground-ray picking. */
final class AdventureSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
 interface Listener {void changed(int event);void unavailable();}
 final AdventureState state;private final Listener listener;private final boolean dark,reduced;private final int skin;private final String accessory;
 private Mesh sphere,box,cone,cylinder;private ForestBackdrop forest;
 private final float[]projection=new float[16],view=new float[16],vp=new float[16],model=new float[16],mvp=new float[16],normalMatrix=new float[9],inverse=new float[16],near=new float[4],far=new float[4];
 private int program,posAttr,normalAttr,mvpUniform,modelUniform,colorUniform,normalUniform,width=1,height=1;
 private long lastFrame;private float downX,downY,lastX,cameraX,cameraZ=4;private boolean dragged,stick;volatile boolean joystickMode,overview;volatile long renderedFrames;
 AdventureSurface(Context c,AdventureState state,boolean dark,boolean reduced,int skin,String accessory,Listener listener){super(c);this.state=state;this.dark=dark;this.reduced=reduced;this.skin=skin;this.accessory=accessory;this.listener=listener;cameraX=state.x;cameraZ=state.z;setEGLContextClientVersion(2);setEGLConfigChooser(8,8,8,0,16,0);setPreserveEGLContextOnPause(true);setRenderer(this);setRenderMode(RENDERMODE_CONTINUOUSLY);setContentDescription("Wonder Isles. Tap ground to walk. Drag horizontally to look around. Optional joystick at bottom left. Clue and action buttons below provide guided controls.");}
 void action(){queueEvent(()->{int event=state.interact();post(()->listener.changed(event));});}
 void clue(){state.nextClue();requestRender();}
 void recenter(){state.turn(-state.yaw);requestRender();}
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

 private void tree(float x,float z,float h,float sway){draw(cylinder,x,.3f,z,.13f,h,.13f,0x9b7057,0);ball(x+sway,h+.2f,z,.65f,0x76b696);ball(x-.35f+sway,h-.05f,z+.15f,.48f,0x9fd09b);}
 @Override public void onDrawFrame(GL10 gl){
  long now=android.os.SystemClock.uptimeMillis();float dt=lastFrame==0?0:Math.min(.05f,(now-lastFrame)/1000f);lastFrame=now;state.advance(dt,reduced);
  GLES20.glClearColor(dark?.09f:.78f,dark?.12f:.9f,dark?.22f:.96f,1);GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);if(program==0)return;
  synchronized(state){float blend=reduced?1:1-(float)Math.exp(-dt*5);cameraX+=(state.x-cameraX)*blend;cameraZ+=(state.z-cameraZ)*blend;
   if(forest!=null)forest.draw(width,height,state.yaw,dark);GLES20.glUseProgram(program);GLES20.glEnableVertexAttribArray(posAttr);GLES20.glEnableVertexAttribArray(normalAttr);
   float t=state.seconds;double a=Math.toRadians(state.yaw);synchronized(vp){if(overview)Matrix.setLookAtM(view,0,0,10,10,0,0,0,0,1,0);else Matrix.setLookAtM(view,0,cameraX+(float)Math.sin(a)*6,4.8f,cameraZ+(float)Math.cos(a)*6,cameraX,.45f,cameraZ-1,0,1,0);Matrix.multiplyMM(vp,0,projection,0,view,0);}
   draw(sphere,0,-1,0,6.5f,.55f,5,0x78c6d4,0);draw(sphere,0,-.7f,0,6,.6f,4.5f,0xb0cf8d,0);draw(sphere,0,-.82f,0,5.8f,.4f,4.35f,0x7ca984,0);
   for(int i=0;i<5;i++){float x=AdventureState.PLACES[i][0],z=AdventureState.PLACES[i][2];draw(sphere,x,-.12f,z,1.05f,.05f,.9f,0x8db48c,0);
    if(i==0){draw(cone,x,.8f,z,1.1f,1.15f,1.1f,0xd39b86,0);draw(cone,x,1.6f,z,.5f,.45f,.5f,0xffd9b6,0);}
    if(i==1){draw(box,x,1.05f,z,.7f,.78f,.09f,0xd7b17a,-10);draw(box,x,1.05f,z+.1f,.59f,.64f,.025f,0xfff8e8,-10);ball(x-.22f,1.2f,z+.18f,.21f,0xe6a0b0);ball(x+.24f,.9f,z+.18f,.24f,0x7bbbd0);}
    if(i==2){draw(cylinder,x,.4f,z,.8f,.5f,.8f,0xa5ccde,0);draw(sphere,x,.95f,z,.85f,.6f,.85f,0x9c8bbf,0);draw(box,x,1.5f,z,.27f,.27f,.27f,0xf1cd7f,reduced?12:t*14);}
    if(i==3){tree(x,z,1.65f,reduced?0:(float)Math.sin(t)*.05f);draw(box,x,1.1f,z+.35f,.6f,.4f,.4f,0xffebc0,0);draw(cone,x,1.75f,z+.35f,.85f,.43f,.65f,0xd693a7,45);}
    if(i==4){draw(box,x,.05f,z,.7f,.1f,.6f,0xcaa77e,0);for(int j=0;j<3;j++){draw(cylinder,x-.4f+j*.4f,.35f,z,.035f,.25f,.035f,0x6e9d76,0);ball(x-.4f+j*.4f,.65f,z,.16f,j==1?0xf2cd80:0xe0a2b2);}}
   }
   // The stream remains visible until three individually counted bridge segments connect it.
   draw(sphere,0,-.06f,-3.5f,3.25f,.04f,.55f,0x79bdce,0);for(int i=0;i<state.bridge;i++)draw(box,0,.01f,-3.15f-i*.31f,.53f,.055f,.14f,0xcead7d,0);
   for(int i=0;i<3;i++)if((state.collected&(1<<i))==0){float x=AdventureState.SEEDS[i][0],z=AdventureState.SEEDS[i][1],bob=reduced?0:(float)Math.sin(t*2+i)*.07f;draw(sphere,x,.25f+bob,z,.2f,.27f,.16f,0xf0cc77,t*8);draw(sphere,x+.12f,.5f+bob,z,.16f,.045f,.08f,0x8bc69c,20);}
   float turtleZ=state.bridge>=3?-4.05f+state.rescue*1.4f:-4.05f,turtleX=state.bridge>=3&&state.rescue>.75f?-.8f*(state.rescue-.75f)/.25f:0;draw(sphere,turtleX,.14f,turtleZ,.34f,.18f,.4f,0x88b493,0);ball(turtleX,.21f,turtleZ+.4f,.13f,0xb1ce9a);ball(turtleX-.05f,.26f,turtleZ+.51f,.018f,0x35495b);ball(turtleX+.05f,.26f,turtleZ+.51f,.018f,0x35495b);
   tree(-4.5f,.1f,1.3f,reduced?0:(float)Math.sin(t*.8)*.05f);tree(4.5f,-.2f,1.5f,reduced?0:(float)Math.sin(t*.9)*.05f);
   // Equipped companion; walk phase is driven by travelled distance, not a decorative slide.
   float bob=reduced?0:state.moving?(float)Math.sin(state.walkPhase)*.035f:(float)Math.sin(t*1.5)*.025f;
   draw(sphere,state.x,.42f+bob,state.z,.31f,.38f,.27f,skin,state.heading);double angle=Math.toRadians(state.heading);float fx=(float)Math.sin(angle),fz=(float)Math.cos(angle),rx=fz,rz=-fx;
   for(int side:new int[]{-1,1}){float ex=state.x+fx*.25f+rx*side*.105f,ez=state.z+fz*.25f+rz*side*.105f;ball(ex,.53f+bob,ez,.035f,0x2e3d50);float step=reduced?0:state.moving?(float)Math.sin(state.walkPhase+side)*.09f:0;draw(sphere,state.x+rx*side*.13f+fx*step,.07f,state.z+rz*side*.13f+fz*step,.12f,.065f,.16f,skin,state.heading);draw(sphere,state.x+rx*side*.35f,.4f+bob,state.z+rz*side*.35f,.12f,.075f,.1f,skin,state.heading);}
   if(accessory.equals("hat")){draw(cylinder,state.x,.84f+bob,state.z,.3f,.035f,.27f,0xd1ab77,0);draw(cylinder,state.x,.92f+bob,state.z,.18f,.1f,.17f,0xd1ab77,0);}else if(accessory.equals("bow")){ball(state.x+rx*.24f,.73f+bob,state.z+rz*.24f,.09f,0xdc9cb6);}else if(accessory.equals("star"))ball(state.x+fx*.28f,.37f+bob,state.z+fz*.28f,.06f,0xf3d280);
   if(state.moving)draw(sphere,state.targetX,.02f,state.targetZ,.18f,.018f,.18f,0xffebaa,0);
  }renderedFrames++;
 }
 private void ground(float sx,float sy){float nx=2*sx/Math.max(1,getWidth())-1,ny=1-2*sy/Math.max(1,getHeight());synchronized(vp){if(!Matrix.invertM(inverse,0,vp,0))return;Matrix.multiplyMV(near,0,inverse,0,new float[]{nx,ny,-1,1},0);Matrix.multiplyMV(far,0,inverse,0,new float[]{nx,ny,1,1},0);}
  for(int i=0;i<3;i++){near[i]/=near[3];far[i]/=far[3];}float dy=far[1]-near[1];if(Math.abs(dy)<.001)return;float distance=-near[1]/dy;if(distance<0)return;state.target(near[0]+(far[0]-near[0])*distance,near[2]+(far[2]-near[2])*distance);}
 @Override public boolean onTouchEvent(MotionEvent e){int action=e.getActionMasked();if(action==MotionEvent.ACTION_DOWN){downX=lastX=e.getX();downY=e.getY();dragged=false;stick=joystickMode&&downX<getWidth()*.4f&&downY>getHeight()*.55f;getParent().requestDisallowInterceptTouchEvent(true);return true;}
  if(action==MotionEvent.ACTION_MOVE){if(stick){float radius=getResources().getDisplayMetrics().density*48;state.joystick((e.getX()-downX)/radius,(e.getY()-downY)/radius);}else if(Math.hypot(e.getX()-downX,e.getY()-downY)>getResources().getDisplayMetrics().density*12){dragged=true;state.turn((e.getX()-lastX)*.18f);}lastX=e.getX();return true;}
  if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){getParent().requestDisallowInterceptTouchEvent(false);if(stick)state.joystick(0,0);else if(!dragged&&action==MotionEvent.ACTION_UP)ground(e.getX(),e.getY());if(action==MotionEvent.ACTION_UP)performClick();return true;}return true;}
 @Override public boolean performClick(){super.performClick();return true;}
 @Override public void onPause(){state.joystick(0,0);queueEvent(()->lastFrame=0);super.onPause();}
 private static final class Mesh {
  final FloatBuffer data;final int count;Mesh(float[]v){data=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();data.put(v).position(0);count=v.length/6;}
  static void vertex(float[]v,int[]offset,float x,float y,float z,float nx,float ny,float nz){int i=offset[0];v[i++]=x;v[i++]=y;v[i++]=z;v[i++]=nx;v[i++]=ny;v[i++]=nz;offset[0]=i;}
  static Mesh sphere(){float[]v=new float[10*16*6*6];int[]o={0};for(int a=0;a<10;a++)for(int b=0;b<16;b++)for(int corner:new int[]{0,1,2,0,2,3}){double lat=Math.PI*(a+(corner==1||corner==2?1:0))/10,lon=2*Math.PI*(b+(corner>=2?1:0))/16;float x=(float)(Math.sin(lat)*Math.cos(lon)),y=(float)Math.cos(lat),z=(float)(Math.sin(lat)*Math.sin(lon));vertex(v,o,x,y,z,x,y,z);}return new Mesh(v);}
  static Mesh box(){float[]v=new float[36*6];int[]o={0};for(int axis=0;axis<3;axis++)for(int sign:new int[]{-1,1})for(int c:new int[]{0,1,2,0,2,3}){float[]p={0,0,0},n={0,0,0};p[axis]=n[axis]=sign;p[(axis+1)%3]=c==0||c==3?-1:1;p[(axis+2)%3]=c<2?-1:1;vertex(v,o,p[0],p[1],p[2],n[0],n[1],n[2]);}return new Mesh(v);}
  static Mesh round(boolean tip){float[]v=new float[24*12*6];int[]o={0};for(int i=0;i<24;i++){double a=i*Math.PI/12,b=(i+1)*Math.PI/12;float ax=(float)Math.cos(a),az=(float)Math.sin(a),bx=(float)Math.cos(b),bz=(float)Math.sin(b),top=tip?0:1;float[][]p={{ax,-1,az},{bx,-1,bz},{bx*top,1,bz*top},{ax,-1,az},{bx*top,1,bz*top},{ax*top,1,az*top},{0,-1,0},{bx,-1,bz},{ax,-1,az},{0,1,0},{ax*top,1,az*top},{bx*top,1,bz*top}};for(int j=0;j<12;j++){float[]q=p[j];float ny=j>=9?1:j>=6?-1:tip?.5f:0;vertex(v,o,q[0],q[1],q[2],j>=6?0:q[0],ny,j>=6?0:q[2]);}}return new Mesh(v);}
 }
}
