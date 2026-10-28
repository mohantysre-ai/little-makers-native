package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.opengl.*;
import android.view.MotionEvent;
import java.nio.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** Solid mesh gameplay, not a video or a bitmap animation. All GL work stays on the render thread. */
final class PlaySceneSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
 interface Listener {void matched(int shape);void completed();void unavailable();}
 private final Listener listener;private final boolean reduced,dark,pond;
 private final ShapePlayState state=new ShapePlayState();
 private Mesh sphere,box,cone,cylinder;
 private final float[] projection=new float[16],view=new float[16],model=new float[16],vp=new float[16],mvp=new float[16],normalMatrix=new float[9];
 private static final int[] COLORS={0x62c8b5,0xf3a0ae,0xf2c35c};
 private int program,posAttr,normalAttr,mvpUniform,modelUniform,colorUniform,normalUniform,materialUniform,timeUniform,width=1,height=1;
 private long lastFrame;private float time,fishX,fishY,targetX,targetY,heading;
 PlaySceneSurface(Context c,boolean dark,boolean reduced,boolean pond,Listener listener){
  super(c);this.dark=dark;this.reduced=reduced;this.pond=pond;this.listener=listener;
  setEGLContextClientVersion(2);setEGLConfigChooser(8,8,8,0,16,0);setPreserveEGLContextOnPause(true);setRenderer(this);
  setRenderMode(reduced?RENDERMODE_WHEN_DIRTY:RENDERMODE_CONTINUOUSLY);
  setContentDescription(pond?"Three-dimensional fish pond. Touch the water to guide the fish.":"Three-dimensional shape workshop. Drag each solid shape into its matching tray. Matching buttons below offer the same activity.");
 }
 boolean reducedMotion(){return reduced;}
 void matchAccessible(int i){queueEvent(()->{if(state.match(i)){post(()->listener.matched(i));if(state.complete())post(listener::completed);}requestRender();});}
 void resetGame(){queueEvent(()->{state.reset();requestRender();});}
 private int shader(int type,String code){int shader=GLES20.glCreateShader(type);GLES20.glShaderSource(shader,code);GLES20.glCompileShader(shader);int[]ok={0};GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,ok,0);if(ok[0]==0)throw new IllegalStateException("Village shader failed");return shader;}
 @Override public void onSurfaceCreated(GL10 gl,EGLConfig config){
  try{
   String vs="uniform mat4 uMvp;uniform mat4 uModel;uniform mat3 uNormal;attribute vec3 aPosition;attribute vec3 aNormal;varying vec3 vNormal;varying vec3 vPosition;void main(){vec4 world=uModel*vec4(aPosition,1.);vPosition=world.xyz;vNormal=normalize(uNormal*aNormal);gl_Position=uMvp*vec4(aPosition,1.);}";
   String fs="precision mediump float;uniform vec3 uColor;uniform float uMaterial;uniform float uTime;varying vec3 vNormal;varying vec3 vPosition;void main(){vec3 n=normalize(vNormal);vec3 light=normalize(vec3(-.6,1.,.7));float diffuse=max(dot(n,light),0.);float shine=pow(max(dot(n,normalize(light+vec3(0.,.6,1.))),0.),32.);float grain=1.;if(uMaterial>1.5){grain=.93+.07*sin(vPosition.y*73.+sin(vPosition.x*3.)*2.);}else if(uMaterial>.5){grain=.92+.08*sin(vPosition.x*5.+uTime*.5)*sin(vPosition.y*7.-uTime*.35);}vec3 c=uColor*grain*(.5+.5*diffuse)+vec3(.28)*shine;gl_FragColor=vec4(c,1.);}";
   program=GLES20.glCreateProgram();int vertex=shader(GLES20.GL_VERTEX_SHADER,vs),fragment=shader(GLES20.GL_FRAGMENT_SHADER,fs);GLES20.glAttachShader(program,vertex);GLES20.glAttachShader(program,fragment);GLES20.glLinkProgram(program);GLES20.glDeleteShader(vertex);GLES20.glDeleteShader(fragment);int[]linked={0};GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linked,0);if(linked[0]==0)throw new IllegalStateException("Village program failed");
   posAttr=GLES20.glGetAttribLocation(program,"aPosition");normalAttr=GLES20.glGetAttribLocation(program,"aNormal");mvpUniform=GLES20.glGetUniformLocation(program,"uMvp");modelUniform=GLES20.glGetUniformLocation(program,"uModel");colorUniform=GLES20.glGetUniformLocation(program,"uColor");normalUniform=GLES20.glGetUniformLocation(program,"uNormal");
   materialUniform=GLES20.glGetUniformLocation(program,"uMaterial");timeUniform=GLES20.glGetUniformLocation(program,"uTime");sphere=Mesh.sphere();box=Mesh.box();cone=Mesh.round(true);cylinder=Mesh.round(false);GLES20.glEnable(GLES20.GL_DEPTH_TEST);lastFrame=0;
  }catch(RuntimeException unsupported){program=0;post(listener::unavailable);}
 }
 @Override public void onSurfaceChanged(GL10 gl,int w,int h){width=w;height=h;GLES20.glViewport(0,0,w,h);Matrix.orthoM(projection,0,-4,4,-4f*h/Math.max(1,w),4f*h/Math.max(1,w),.1f,40);}
 private void draw(Mesh mesh,float x,float y,float z,float sx,float sy,float sz,int color,float angle){
  Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);if(angle!=0)Matrix.rotateM(model,0,angle,0,pond?0:1,pond?1:0);Matrix.scaleM(model,0,sx,sy,sz);Matrix.multiplyMM(mvp,0,vp,0,model,0);
  for(int column=0;column<3;column++){float scale=column==0?sx:column==1?sy:sz;for(int row=0;row<3;row++)normalMatrix[column*3+row]=model[column*4+row]/(scale*scale);}GLES20.glUniformMatrix3fv(normalUniform,1,false,normalMatrix,0);GLES20.glUniformMatrix4fv(mvpUniform,1,false,mvp,0);GLES20.glUniformMatrix4fv(modelUniform,1,false,model,0);GLES20.glUniform3f(colorUniform,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f);
  GLES20.glUniform1f(materialUniform,pond&&(color==0x204355||color==0x83aeb2)?1:!pond&&(color==0x374b58||color==0xd7b58b)?2:0);GLES20.glUniform1f(timeUniform,time);mesh.data.position(0);GLES20.glVertexAttribPointer(posAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);mesh.data.position(3);GLES20.glVertexAttribPointer(normalAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mesh.count);
 }
 private void ball(float x,float y,float z,float r,int color){draw(sphere,x,y,z,r,r,r,color,0);}

 @Override public void onDrawFrame(GL10 gl){
  long now=android.os.SystemClock.uptimeMillis();float dt=Math.min(.05f,lastFrame==0?0:(now-lastFrame)/1000f);lastFrame=now;
  if(!reduced)time+=dt;state.advance(dt,reduced);
  GLES20.glClearColor(dark?.07f:.73f,dark?.12f:.85f,dark?.18f:.88f,1);GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);if(program==0)return;
  GLES20.glUseProgram(program);GLES20.glEnableVertexAttribArray(posAttr);GLES20.glEnableVertexAttribArray(normalAttr);
  Matrix.setLookAtM(view,0,0,0,14,0,0,0,0,1,0);Matrix.multiplyMM(vp,0,projection,0,view,0);
  if(pond){pond(dt);return;}
  // A beveled wooden workbench, raised tray rims, recessed matching forms and contact shadows.
  draw(box,0,-.3f,-.9f,3.95f,4.5f,.28f,dark?0x374b58:0xd7b58b,0);
  for(int plank=0;plank<8;plank++)draw(box,0,-4.25f+plank*1.12f,-.6f,3.9f,.012f,.015f,dark?0x334551:0xbc966e,0);
  for(int i=0;i<3;i++){
   float x=ShapePlayState.homeX(i),y=ShapePlayState.HOME_Y;
   draw(sphere,x,y,-.31f,1.12f,1.15f,.18f,dark?0x596778:0xf2e5cc,0);
   piece(i,x,y,-.08f,.74f,0,dark?0x263541:0x8c7969);
   float py=state.y[i]+(!reduced&&!state.sorted[i]&&state.selected!=i?(float)Math.sin(time*1.7f+i)*.06f:0);
   float lift=state.selected==i?.58f:0;
   draw(sphere,state.x[i]+.09f,py-.15f,-.37f,.73f,.68f,.025f,dark?0x38434b:0xb69a77,0);
   piece(i,state.x[i],py,.25f+lift,.66f,state.sorted[i]?0:(!reduced?(float)Math.sin(time+i)*8:0),COLORS[i]);
   if(!reduced&&state.sorted[i]&&state.age[i]<.75f)for(int j=0;j<8;j++){double a=j*Math.PI/4;float r=.7f+state.age[i];ball(x+(float)Math.cos(a)*r,y+(float)Math.sin(a)*r,.8f,.045f,0xffe7a0);}
  }
 }
 private void piece(int i,float x,float y,float z,float size,float angle,int color){
  if(i==0)draw(sphere,x,y,z,size,size,.34f,color,angle);
  else if(i==1)draw(cone,x,y,z,size,size,.38f,color,angle);
  else draw(box,x,y,z,size*.82f,size*.82f,.30f,color,angle+12);
 }
 private void pond(float dt){
  draw(sphere,0,0,-1.2f,5.5f,7,.4f,dark?0x204355:0x83aeb2,0);
  draw(sphere,0,-4.1f,-.65f,4.7f,1.3f,.12f,dark?0x55615c:0xe0cf9e,0);
  for(int i=0;i<11;i++){float x=-3.6f+i*.72f,y=-3.6f+(float)Math.sin(i*2)*.25f;draw(sphere,x,y,-.4f,.28f,.18f,.18f,i%2==0?0xb3b5a0:0x929c98,i*19);}
  for(int i=0;i<7;i++){float x=-3.6f+i*1.2f;for(int leaf=0;leaf<3;leaf++)draw(sphere,x+(leaf-1)*.13f+(reduced?0:(float)Math.sin(time*1.3f+i)*.07f),-3.2f+leaf*.23f,-.25f,.09f,.5f,.09f,0x4a9f86,0);}
  float dx=targetX-fishX,dy=targetY-fishY;float blend=reduced?1:1-(float)Math.exp(-dt*2.5f);fishX+=dx*blend;fishY+=dy*blend;
  if(Math.hypot(dx,dy)>.04)heading=dx<0?-1:1;if(heading==0)heading=1;
  float wag=reduced?0:(float)Math.sin(time*8)*.12f;
  // Streamlined body, counter-shaded belly, forked moving tail and small lateral eye.
  draw(sphere,fishX,fishY,.2f,.83f,.29f,.19f,0x9d9e6e,0);
  draw(sphere,fishX+heading*.06f,fishY-.09f,.23f,.65f,.17f,.17f,0xd8d6bc,0);
  draw(sphere,fishX+heading*.57f,fishY+.01f,.2f,.25f,.23f,.18f,0x9d9e6e,0);
  for(int side=-1;side<=1;side+=2)draw(cone,fishX-heading*.91f,fishY+side*.13f,.19f,.26f,.19f,.035f,0x8a8e64,90+side*30+wag*60);
  draw(cone,fishX-heading*.12f,fishY+.30f,.2f,.33f,.17f,.025f,0x878a61,180);
  draw(sphere,fishX-heading*.1f,fishY-.26f,.2f,.27f,.06f,.035f,0xb0aa80,10);
  draw(sphere,fishX+heading*.28f,fishY-.10f,.39f,.2f,.04f,.025f,0xa7a276,heading*(20+wag*30));
  draw(sphere,fishX+heading*.4f,fishY,.38f,.018f,.16f,.012f,0x6b7662,heading*12);
  ball(fishX+heading*.65f,fishY+.07f,.38f,.036f,0x202c2b);ball(fishX+heading*.66f,fishY+.08f,.41f,.009f,0xf1eddf);
  draw(sphere,fishX+heading*.8f,fishY-.025f,.24f,.035f,.014f,.09f,0x636e5d,0);
  for(int i=0;i<5;i++){float y=-2.8f+(time*.45f+i*1.3f)%6.5f;ball(-2.7f+(float)Math.sin(y*2)*.15f,y,.25f,.045f+i%2*.025f,0xb9e6e1);}
  for(int ring=0;ring<2;ring++){float radius=reduced?.24f:.15f+(time*.5f+ring*.5f)%1;for(int j=0;j<24;j++){double a=j*Math.PI/12;ball(targetX+(float)Math.cos(a)*radius,targetY+(float)Math.sin(a)*radius,.7f,.025f,0xc5efdf);}}
 }
 @Override public boolean onTouchEvent(MotionEvent e){
  float x=(e.getX()/Math.max(1,getWidth())-.5f)*8,y=(.5f-e.getY()/Math.max(1,getHeight()))*8*getHeight()/Math.max(1,getWidth());int action=e.getActionMasked();
  if(action==MotionEvent.ACTION_DOWN)getParent().requestDisallowInterceptTouchEvent(true);
  queueEvent(()->{if(pond){targetX=Math.max(-3.1f,Math.min(3.1f,x));targetY=Math.max(-3.2f,Math.min(3.2f,y));}else if(action==MotionEvent.ACTION_DOWN)state.pick(x,y);else if(action==MotionEvent.ACTION_MOVE)state.drag(x,y);else if(action==MotionEvent.ACTION_UP){int matched=state.release();if(matched>=0){post(()->listener.matched(matched));if(state.complete())post(listener::completed);}}else if(action==MotionEvent.ACTION_CANCEL)state.cancel();requestRender();});
  if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){getParent().requestDisallowInterceptTouchEvent(false);if(action==MotionEvent.ACTION_UP)performClick();}return true;
 }
 @Override public boolean performClick(){super.performClick();return true;}
 @Override public void onPause(){queueEvent(()->lastFrame=0);super.onPause();}
 private static final class Mesh {
  final FloatBuffer data;final int count;Mesh(float[]v){data=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();data.put(v).position(0);count=v.length/6;}
  static void vertex(float[]v,int[]offset,float x,float y,float z,float nx,float ny,float nz){int i=offset[0];v[i++]=x;v[i++]=y;v[i++]=z;v[i++]=nx;v[i++]=ny;v[i++]=nz;offset[0]=i;}
  static Mesh sphere(){float[]v=new float[10*16*6*6];int[]o={0};for(int a=0;a<10;a++)for(int b=0;b<16;b++)for(int corner:new int[]{0,1,2,0,2,3}){double lat=Math.PI*(a+(corner==1||corner==2?1:0))/10,lon=2*Math.PI*(b+(corner>=2?1:0))/16;float x=(float)(Math.sin(lat)*Math.cos(lon)),y=(float)Math.cos(lat),z=(float)(Math.sin(lat)*Math.sin(lon));vertex(v,o,x,y,z,x,y,z);}return new Mesh(v);}
  static Mesh box(){float[]v=new float[36*6];int[]o={0};for(int axis=0;axis<3;axis++)for(int sign:new int[]{-1,1})for(int c:new int[]{0,1,2,0,2,3}){float[]p={0,0,0},n={0,0,0};p[axis]=n[axis]=sign;p[(axis+1)%3]=c==0||c==3?-1:1;p[(axis+2)%3]=c<2?-1:1;vertex(v,o,p[0],p[1],p[2],n[0],n[1],n[2]);}return new Mesh(v);}
  static Mesh round(boolean tip){float[]v=new float[24*12*6];int[]o={0};for(int i=0;i<24;i++){double a=i*Math.PI/12,b=(i+1)*Math.PI/12;float ax=(float)Math.cos(a),az=(float)Math.sin(a),bx=(float)Math.cos(b),bz=(float)Math.sin(b),top=tip?0:1;float[][]p={{ax,-1,az},{bx,-1,bz},{bx*top,1,bz*top},{ax,-1,az},{bx*top,1,bz*top},{ax*top,1,az*top},{0,-1,0},{bx,-1,bz},{ax,-1,az},{0,1,0},{ax*top,1,az*top},{bx*top,1,bz*top}};for(int j=0;j<12;j++){float[]q=p[j];float ny=j>=9?1:j>=6?-1:tip?.5f:0;vertex(v,o,q[0],q[1],q[2],j>=6?0:q[0],ny,j>=6?0:q[2]);}}return new Mesh(v);}
 }
}
