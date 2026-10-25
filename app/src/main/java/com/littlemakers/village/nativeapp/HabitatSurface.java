package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.opengl.*;
import android.view.*;
import java.nio.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
/** A locally built habitat: placed objects remain world geometry and fish swim around the display. */
final class HabitatSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
 interface Listener {void selected(int slot);void unavailable();}
 private final Listener listener;private final boolean reduced,dark,pond=true;private final HabitatState state;
 private Mesh sphere,box,cone,cylinder;private final float[]projection=new float[16],view=new float[16],model=new float[16],vp=new float[16],mvp=new float[16],normalMatrix=new float[9];
 private int program,posAttr,normalAttr,mvpUniform,modelUniform,colorUniform,normalUniform,materialUniform,timeUniform,width=1,height=1;private long lastFrame;private float time;volatile long renderedFrames;
 HabitatSurface(Context c,HabitatState state,boolean dark,boolean reduced,Listener listener){super(c);this.state=state;this.dark=dark;this.reduced=reduced;this.listener=listener;setEGLContextClientVersion(2);setEGLConfigChooser(8,8,8,0,16,0);setPreserveEGLContextOnPause(true);setRenderer(this);setRenderMode(reduced?RENDERMODE_WHEN_DIRTY:RENDERMODE_CONTINUOUSLY);setContentDescription("Your three-dimensional aquarium. Tap a building spot, then choose a decoration. The numbered buttons provide the same selection.");}
 static float slotX(int i){return(i%3-1)*2.45f;}static float slotY(int i){return 1.5f-(i/3)*1.8f;}
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
  Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);if(angle!=0)Matrix.rotateM(model,0,angle,0,0,1);Matrix.scaleM(model,0,sx,sy,sz);Matrix.multiplyMM(mvp,0,vp,0,model,0);
  for(int column=0;column<3;column++){float scale=column==0?sx:column==1?sy:sz;for(int row=0;row<3;row++)normalMatrix[column*3+row]=model[column*4+row]/(scale*scale);}GLES20.glUniformMatrix3fv(normalUniform,1,false,normalMatrix,0);GLES20.glUniformMatrix4fv(mvpUniform,1,false,mvp,0);GLES20.glUniformMatrix4fv(modelUniform,1,false,model,0);GLES20.glUniform3f(colorUniform,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f);
  GLES20.glUniform1f(materialUniform,pond&&(color==0x204355||color==0x7ec6cd)?1:!pond&&(color==0x374b58||color==0xd7b58b)?2:0);GLES20.glUniform1f(timeUniform,time);mesh.data.position(0);GLES20.glVertexAttribPointer(posAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);mesh.data.position(3);GLES20.glVertexAttribPointer(normalAttr,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mesh.count);
 }
 private void ball(float x,float y,float z,float r,int color){draw(sphere,x,y,z,r,r,r,color,0);}

 @Override public void onDrawFrame(GL10 gl){long now=android.os.SystemClock.uptimeMillis();float dt=lastFrame==0?0:Math.min(.05f,(now-lastFrame)/1000f);lastFrame=now;if(!reduced)time+=dt;GLES20.glClearColor(dark?.08f:.45f,dark?.18f:.73f,dark?.23f:.79f,1);GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);if(program==0)return;GLES20.glUseProgram(program);GLES20.glEnableVertexAttribArray(posAttr);GLES20.glEnableVertexAttribArray(normalAttr);Matrix.setLookAtM(view,0,0,0,14,0,0,0,0,1,0);Matrix.multiplyMM(vp,0,projection,0,view,0);
  draw(sphere,0,0,-1.3f,6,8,.4f,dark?0x204657:0x7fc6cc,0);draw(sphere,0,-3.8f,-.65f,5,1.4f,.1f,dark?0x65756b:0xe1d1a9,0);
  synchronized(state){for(int column=0;column<3;column++)draw(sphere,(column-1)*2.45f,-1.6f,-.85f,1.03f,3.35f,.48f,dark?0x426d75:0x71a5a0,column*7-7);for(int i=0;i<9;i++){float x=slotX(i),y=slotY(i);draw(sphere,x,y-.5f,-.22f,.7f,.15f,.2f,0x9eb8a7,0);if(i==state.selected){for(int j=0;j<16;j++){float a=j*.3927f;ball(x+(float)Math.cos(a)*.65f,y+(float)Math.sin(a)*.65f,.55f,.035f,0xf7dda0);}}int type=state.slots[i];if(type>=0)decoration(type,x,y);}}
  for(int i=0;i<3;i++){float phase=time*.35f+i*2.1f,x=(float)Math.sin(phase)*2.8f,y=2.5f+(float)Math.cos(phase)*.25f;draw(sphere,x,y,.15f,.43f,.23f,.22f,new int[]{0xf1bc74,0xd9a4bb,0x9cc9a0}[i],0);draw(cone,x-.52f,y,.15f,.24f,.3f,.14f,0xe4c898,-90);ball(x+.25f,y+.07f,.34f,.045f,0x354a5a);draw(sphere,x-.05f,y-.19f+(reduced?0:(float)Math.sin(time*7+i)*.05f),.32f,.17f,.08f,.07f,0xf7dda9,15);}
  for(int i=0;i<7;i++){float y=-3.5f+(time*.45f+i*1.1f)%7.5f;ball(-3.6f+(float)Math.sin(y)*.08f,y,.05f,.04f,0xd1e9dd);}renderedFrames++;
 }
 private void decoration(int type,float x,float y){switch(type){case 0:case 4:for(int i=0;i<3;i++)draw(sphere,x+(i-1)*.19f,y+.15f,-.03f,.11f,.5f,.10f,type==0?0x75ad8a:0xd4a0b8,(i-1)*18+(reduced?0:(float)Math.sin(time+i)*3));break;
 case 1:draw(sphere,x,y,-.02f,.53f,.32f,.28f,0xa1aaa1,15);draw(sphere,x+.3f,y-.14f,.02f,.25f,.17f,.19f,0xbcc0ae,-10);break;
 case 2:draw(box,x-.44f,y,.02f,.12f,.5f,.16f,0xc5b195,0);draw(box,x+.44f,y,.02f,.12f,.5f,.16f,0xc5b195,0);draw(sphere,x,y+.5f,.02f,.58f,.16f,.18f,0xd6c4a5,0);break;
 case 3:for(int i=0;i<5;i++)draw(sphere,x+(i-2)*.13f,y,.03f,.17f,.38f,.2f,i%2==0?0xf0cfb5:0xdfb49f,(i-2)*-13);break;
 case 5:draw(box,x,y,.03f,.48f,.28f,.22f,0xb4926c,0);draw(sphere,x,y+.27f,.03f,.49f,.13f,.23f,0xd2ab72,0);ball(x,y+.01f,.28f,.065f,0xf2da92);break;}}
 @Override public boolean onTouchEvent(MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN){getParent().requestDisallowInterceptTouchEvent(true);return true;}if(e.getActionMasked()==MotionEvent.ACTION_UP){float x=(e.getX()/getWidth()-.5f)*8,y=(.5f-e.getY()/getHeight())*8*getHeight()/getWidth();int best=-1;float distance=.85f;for(int i=0;i<9;i++){float d=(float)Math.hypot(x-slotX(i),y-slotY(i));if(d<distance){best=i;distance=d;}}if(best>=0){state.select(best);listener.selected(best);requestRender();}performClick();getParent().requestDisallowInterceptTouchEvent(false);return true;}if(e.getActionMasked()==MotionEvent.ACTION_CANCEL)getParent().requestDisallowInterceptTouchEvent(false);return true;}
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
