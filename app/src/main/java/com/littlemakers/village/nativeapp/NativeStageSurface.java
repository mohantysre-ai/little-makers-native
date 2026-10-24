package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.*;
import android.provider.Settings;
import android.view.View;
import java.nio.*;
import java.util.*;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** Shared native mesh renderer for every legacy mechanic, story diorama and physical art board. */
final class NativeStageSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
 private final boolean reduced,dark;private final Runnable unavailable;
 private volatile SceneFrame frame;private Bitmap pendingImage,retainedImage;
 private ForestBackdrop backdrop;private Mesh sphere,box,cone,cylinder;private int program,pos,normal,mvpU,colorU,normalU,worldU,timeU,materialU;
 private int imageProgram,imageTexture,imagePos,imageUv,imageMvp;private FloatBuffer imageQuad;
 private final float[]projection=new float[16],view=new float[16],vp=new float[16],root=new float[16],local=new float[16],model=new float[16],mvp=new float[16],normals=new float[9];
 volatile long renderedFrames;private float winAt=-1;private boolean previousWin;private volatile float reactX=-1000,reactY=-1000;private volatile boolean reaction;private float reactionAt=-10;
 void react(float x,float y){reactX=x;reactY=y;reaction=true;requestRender();}
 private long last;private float time;private int width=1,height=1;private boolean stopped;
 NativeStageSurface(Context c,boolean dark,Runnable unavailable){super(c);this.dark=dark;this.unavailable=unavailable;reduced=c.getSharedPreferences("family",0).getBoolean("reduce_motion",false)||Settings.Global.getFloat(c.getContentResolver(),Settings.Global.ANIMATOR_DURATION_SCALE,1)==0;setEGLContextClientVersion(2);setEGLConfigChooser(8,8,8,0,16,0);setPreserveEGLContextOnPause(true);setRenderer(this);setRenderMode(reduced?RENDERMODE_WHEN_DIRTY:RENDERMODE_CONTINUOUSLY);setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);}
 boolean reducedMotion(){return reduced;}
 void submit(SceneFrame f){frame=f;requestRender();}
 synchronized void image(Bitmap b){if(pendingImage!=null)pendingImage.recycle();pendingImage=b;requestRender();}
 private synchronized Bitmap takeImage(){Bitmap b=pendingImage;pendingImage=null;return b;}
 private int shader(int kind,String code){int s=GLES20.glCreateShader(kind);GLES20.glShaderSource(s,code);GLES20.glCompileShader(s);int[]ok={0};GLES20.glGetShaderiv(s,GLES20.GL_COMPILE_STATUS,ok,0);if(ok[0]==0)throw new IllegalStateException("Stage shader: "+GLES20.glGetShaderInfoLog(s));return s;}
 private int program(String v,String f){int p=GLES20.glCreateProgram(),a=shader(GLES20.GL_VERTEX_SHADER,v),b=shader(GLES20.GL_FRAGMENT_SHADER,f);GLES20.glAttachShader(p,a);GLES20.glAttachShader(p,b);GLES20.glLinkProgram(p);GLES20.glDeleteShader(a);GLES20.glDeleteShader(b);int[]ok={0};GLES20.glGetProgramiv(p,GLES20.GL_LINK_STATUS,ok,0);if(ok[0]==0)throw new IllegalStateException("Stage linking");return p;}
 @Override public void onSurfaceCreated(GL10 unused,EGLConfig config){try{
  program=program("uniform mat4 uMvp;uniform mat4 uModel;uniform mat3 uNormal;attribute vec3 aPosition;attribute vec3 aNormal;varying vec3 vN;varying vec3 vP;void main(){vN=normalize(uNormal*aNormal);vP=(uModel*vec4(aPosition,1.)).xyz;gl_Position=uMvp*vec4(aPosition,1.);}","precision mediump float;uniform vec3 uColor;uniform float uTime;uniform float uMaterial;varying vec3 vN;varying vec3 vP;void main(){vec3 n=normalize(vN),l=normalize(vec3(-.45,-.7,1.));float diffuse=max(dot(n,l),0.);float gloss=pow(max(dot(n,normalize(l+vec3(0.,0.,1.))),0.),32.);float grain=uMaterial>.5?.97+.03*sin(vP.y*.8+sin(vP.x*.04)*2.):1.;vec3 lit=uColor*grain*(.67+.33*diffuse)+vec3(.19)*gloss;gl_FragColor=vec4(lit,1.);}");
  pos=GLES20.glGetAttribLocation(program,"aPosition");normal=GLES20.glGetAttribLocation(program,"aNormal");mvpU=GLES20.glGetUniformLocation(program,"uMvp");worldU=GLES20.glGetUniformLocation(program,"uModel");colorU=GLES20.glGetUniformLocation(program,"uColor");normalU=GLES20.glGetUniformLocation(program,"uNormal");timeU=GLES20.glGetUniformLocation(program,"uTime");materialU=GLES20.glGetUniformLocation(program,"uMaterial");
  backdrop=null;sphere=Mesh.sphere();box=Mesh.box();cone=Mesh.round(true);cylinder=Mesh.round(false);
  imageProgram=program("uniform mat4 uMvp;attribute vec3 aPosition;attribute vec2 aUv;varying vec2 vUv;void main(){vUv=aUv;gl_Position=uMvp*vec4(aPosition,1.);}","precision mediump float;uniform sampler2D uImage;varying vec2 vUv;void main(){gl_FragColor=texture2D(uImage,vUv);}");imagePos=GLES20.glGetAttribLocation(imageProgram,"aPosition");imageUv=GLES20.glGetAttribLocation(imageProgram,"aUv");imageMvp=GLES20.glGetUniformLocation(imageProgram,"uMvp");int[]id={0};GLES20.glGenTextures(1,id,0);imageTexture=id[0];GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,imageTexture);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);if(retainedImage!=null)GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,retainedImage,0);imageQuad=ByteBuffer.allocateDirect(6*5*4).order(ByteOrder.nativeOrder()).asFloatBuffer();GLES20.glEnable(GLES20.GL_DEPTH_TEST);last=0;
 }catch(RuntimeException failed){program=0;post(unavailable);}}
 @Override public void onSurfaceChanged(GL10 gl,int w,int h){width=w;height=h;GLES20.glViewport(0,0,w,h);}
 private void identity(){Matrix.setIdentityM(root,0);}
 private void origin(float x,float y,float z,float size,float yaw){identity();Matrix.translateM(root,0,x,y,z);Matrix.rotateM(root,0,yaw,0,1,0);Matrix.scaleM(root,0,size,size,size);}
 private void draw(Mesh mesh,float x,float y,float z,float sx,float sy,float sz,int color,float angle){
  Matrix.setIdentityM(local,0);Matrix.translateM(local,0,x,y,z);if(angle!=0)Matrix.rotateM(local,0,angle,0,0,1);Matrix.scaleM(local,0,Math.max(.001f,sx),Math.max(.001f,sy),Math.max(.001f,sz));Matrix.multiplyMM(model,0,root,0,local,0);Matrix.multiplyMM(mvp,0,vp,0,model,0);
  for(int col=0;col<3;col++){float length2=0;for(int row=0;row<3;row++)length2+=model[col*4+row]*model[col*4+row];for(int row=0;row<3;row++)normals[col*3+row]=model[col*4+row]/length2;}
  GLES20.glUniformMatrix4fv(mvpU,1,false,mvp,0);GLES20.glUniformMatrix4fv(worldU,1,false,model,0);GLES20.glUniformMatrix3fv(normalU,1,false,normals,0);GLES20.glUniform3f(colorU,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f);GLES20.glUniform1f(materialU,mesh==box&&(color==0xc5a47e||color==0xf2ead8)?.7f:0);mesh.data.position(0);GLES20.glVertexAttribPointer(pos,3,GLES20.GL_FLOAT,false,24,mesh.data);mesh.data.position(3);GLES20.glVertexAttribPointer(normal,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mesh.count);
 }
 private void ball(float x,float y,float z,float r,int color){draw(sphere,x,y,z,r,r,r,color,0);}
 private void pill(float x,float y,float z,float w,float h,float depth,int color){draw(box,x,y,z,Math.max(1,w/2-8),Math.max(1,h/2-8),depth,color,0);for(int a=-1;a<=1;a+=2)for(int b=-1;b<=1;b+=2)draw(sphere,x+a*(w/2-12),y+b*(h/2-12),z,12,12,depth,color,0);draw(box,x,y,z,w/2,Math.max(1,h/2-12),depth,color,0);draw(box,x,y,z,Math.max(1,w/2-12),h/2,depth,color,0);}
 @Override public void onDrawFrame(GL10 gl){
  SceneFrame f=frame;int bg=f==null?(dark?0xff171b2d:0xfff6f4ff):f.background;GLES20.glClearColor(((bg>>16)&255)/255f,((bg>>8)&255)/255f,(bg&255)/255f,1);GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);if(program==0||f==null)return;
  long now=android.os.SystemClock.uptimeMillis();float dt=last==0?0:Math.min(.05f,Math.max(0,(now-last)/1000f));last=now;if(!reduced)time+=dt;
  Matrix.orthoM(projection,0,0,640,f.height,0,.1f,4000);Matrix.setLookAtM(view,0,0,0,1500,0,0,0,0,1,0);Matrix.multiplyMM(vp,0,projection,0,view,0);if(f.environment.equals("welcome")){if(backdrop==null){backdrop=new ForestBackdrop(getContext());backdrop.create();}backdrop.draw(width,height,reduced?0:(float)Math.sin(time*.2f)*12,dark);}GLES20.glUseProgram(program);GLES20.glEnableVertexAttribArray(pos);GLES20.glEnableVertexAttribArray(normal);GLES20.glUniform1f(timeU,time);identity();if(reaction){reactionAt=time;reaction=false;}if(f.won&&!previousWin)winAt=time;previousWin=f.won;
  if(f.environment.equals("forest")||f.environment.equals("ocean")||f.environment.equals("night")||f.environment.equals("bridge"))environment(f.environment,f.won,f.branch);
  else if(f.environment.equals("game")){pill(320,500,-34,636,990,15,dark?0x243247:0xf2ead8);origin(606,43,25,23,10);prop(StoryModels.BRUSH,0x91bbaa,false);origin(23,545+(reduced?0:(float)Math.sin(time)*7),30,18,12);prop(StoryModels.BUTTERFLY,0xd6afd2,false);}
  else if(f.environment.equals("studio")){pill(320,420,-30,638,838,20,dark?0x39465b:0xc5a47e);pill(320,420,0,610,810,8,0xfffbef);}
  int index=0;for(SceneFrame.Node n:f.nodes){float bob=n.animate&&!reduced?(float)Math.sin(time*1.8f+index)*2:0,yaw=n.animate?12+(!reduced?(float)Math.sin(time+index)*5:0):0;index++;identity();
   if(n.type==SceneFrame.PANEL){pill(n.x+3,n.y+5,-12,n.w,n.h,6,dark?0x141c29:0xc4bbaa);float lift=!reduced&&time-reactionAt<.35f&&Math.hypot(n.x-reactX,n.y-reactY)<Math.max(n.w,n.h)*.55f?(float)Math.sin((time-reactionAt)/.35f*Math.PI)*7:0;pill(n.x,n.y,lift,n.w,n.h,8,n.color);continue;}
   if(n.type==8){for(int a:new int[]{-1,1})for(int b:new int[]{-1,1}){draw(box,n.x+a*(n.w/2-10),n.y+b*n.h/2,n.z,10,3,4,n.color,0);draw(box,n.x+a*n.w/2,n.y+b*(n.h/2-10),n.z,3,10,4,n.color,0);}continue;}
   if(n.type==7){draw(sphere,n.x,n.y+n.h/2-8,12,n.w/2,10,20,dark?0x526c84:0xc3dce3,0);for(int side:new int[]{-1,1})draw(box,n.x+side*(n.w/2-5),n.y,34,4,n.h/2,5,dark?0x85a3b9:0xe3f0f0,0);draw(sphere,n.x,n.y-n.h/2,40,n.w/2,7,10,0xc0dce4,0);continue;}
   if(n.type==6){origin(n.x,n.y,n.z,1,0);draw(box,0,0,0,n.w,n.h,6,n.color,n.model/100f);continue;}
   if(n.type==SceneFrame.MODEL){origin(n.x,n.y+bob,n.z,n.w,yaw);prop(n.model,n.color,f.won);continue;}
   if(n.type==SceneFrame.CHARACTER){origin(n.x,n.y+bob,n.z,n.w,8);companion(n.color,f.won);accessory(n.model);continue;}
   draw(sphere,n.x+4,n.y+7,12,n.w,n.h*.8f,1,dark?0x182134:0xc0b9b0,0);origin(n.x,n.y+bob,n.z,1,yaw);draw(n.type==SceneFrame.SPHERE?sphere:n.type==SceneFrame.CONE?cone:box,0,0,0,n.w,n.h,Math.max(8,n.w*.48f),n.color,n.type==SceneFrame.CONE?180:0);
  }
  if(f.environment.equals("studio")){origin(620,72,85,32,10);prop(StoryModels.BRUSH,0x81b9a4,false);origin(23,735+(reduced?0:(float)Math.sin(time)*5),75,23,12);prop(StoryModels.BUTTERFLY,0xd6afd2,false);drawing(f.height);}
  if(f.won&&!reduced&&time-winAt<1.6f){identity();for(int i=0;i<12;i++){float a=time*.6f+i*.5236f;ball(320+(float)Math.cos(a)*200,300+(float)Math.sin(a)*120,100,4,0xf4cf78);}}
  renderedFrames++;
 }
 private void drawing(float h){Bitmap image=takeImage();if(image!=null){GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,imageTexture);GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,image,0);if(retainedImage!=null)retainedImage.recycle();retainedImage=image;}if(imageTexture==0)return;float[]v={20,20,40,0,0,620,20,40,1,0,620,h-20,40,1,1,20,20,40,0,0,620,h-20,40,1,1,20,h-20,40,0,1};imageQuad.position(0);imageQuad.put(v).position(0);GLES20.glUseProgram(imageProgram);GLES20.glEnableVertexAttribArray(imagePos);GLES20.glEnableVertexAttribArray(imageUv);GLES20.glUniformMatrix4fv(imageMvp,1,false,vp,0);GLES20.glActiveTexture(GLES20.GL_TEXTURE0);GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,imageTexture);GLES20.glUniform1i(GLES20.glGetUniformLocation(imageProgram,"uImage"),0);imageQuad.position(0);GLES20.glVertexAttribPointer(imagePos,3,GLES20.GL_FLOAT,false,20,imageQuad);imageQuad.position(3);GLES20.glVertexAttribPointer(imageUv,2,GLES20.GL_FLOAT,false,20,imageQuad);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);}
 private void companion(int color,boolean happy){draw(sphere,0,-.1f,0,.48f,.55f,.34f,color,0);float blink=!reduced&&time%5>4.85?.008f:.045f;for(int i:new int[]{-1,1}){draw(sphere,i*.17f,-.22f,.31f,.045f,blink,.03f,0x2c3c50,0);ball(i*.29f,-.03f,.28f,.065f,0xe69e9b);draw(sphere,i*.2f,.47f,.04f,.16f,.08f,.18f,color,0);draw(sphere,i*.5f,.1f+(reduced?0:(float)Math.sin(time*2+i)*.03f),0,.14f,.10f,.12f,color,0);}for(int i=0;i<5;i++){float x=(i-2)*.04f;ball(x,.09f-x*x*3,.335f,.018f,0x2c3c50);}}
 private void accessory(int kind){if(kind==1){draw(cylinder,0,-.91f,0,.32f,.055f,.26f,0xd4af79,0);draw(cylinder,0,-1.03f,0,.2f,.12f,.2f,0xd4af79,0);}else if(kind==2){draw(cone,.21f,-.72f,.22f,.1f,.14f,.06f,0xe3a1bc,90);draw(cone,.41f,-.72f,.22f,.1f,.14f,.06f,0xe3a1bc,-90);}else if(kind==3)ball(.17f,-.04f,.27f,.07f,0xf3d07e);}
 private void child(int color,boolean happy){float walk=reduced?0:(float)Math.sin(time*3)*.08f;draw(sphere,0,0,0,.33f,.42f,.25f,color,0);ball(0,-.55f,0,.27f,0xffd5af);float eyes=!reduced&&time%5>4.85?.006f:.025f;draw(sphere,-.1f,-.59f,.25f,.025f,eyes,.02f,0x26384b,0);draw(sphere,.1f,-.59f,.25f,.025f,eyes,.02f,0x26384b,0);draw(sphere,-.19f,.46f,walk,.11f,.2f,.12f,0x435270,0);draw(sphere,.19f,.46f,-walk,.11f,.2f,.12f,0x435270,0);draw(sphere,-.4f,-.04f,walk,.12f,.27f,.12f,0xffd5af,happy?-30:10);draw(sphere,.4f,-.04f,-walk,.12f,.27f,.12f,0xffd5af,happy?30:-10);ball(0,-.46f,.25f,.035f,0xb67f74);}
 private void environment(String mode,boolean happy,int branch){identity();boolean ocean=mode.equals("ocean"),night=mode.equals("night");draw(sphere,320,820,-180,570,410,100,ocean?0x9ac6c7:night?0x52766b:0x9cc18e,0);draw(sphere,180,860,-90,370,250,40,ocean?0xe3d1aa:night?0x647c72:0xc1d99a,0);
  if(mode.equals("bridge"))draw(sphere,350,650,-45,90,230,7,0x79bfcc,0);
  if(ocean){for(int i=0;i<4;i++){origin(55+i*170,665,0,70,0);prop(StoryModels.SEAWEED,i%2==0?0x6aaa91:0xc58ba6,false);}for(int i=0;i<2;i++){origin(140+i*300+(reduced?0:(float)Math.sin(time*.5+i)*30),340+i*80,40,65,10);prop(StoryModels.FISH,i==0?0xf1b96a:0x80bba7,happy);}}
  else {origin(55,420,-45,135,8);prop(StoryModels.PINE,0x6aab8d,false);origin(550,485,-45,120,-8);prop(StoryModels.HOUSE,0xe3b992,false);origin(happy?(branch==0?440:230):165,590,30,95,0);child(0x7fb8d6,happy);origin(happy?(branch==0?510:315):465,620,30,88,0);child(0xd69bb7,happy);origin(530,110,-60,65,0);prop(night?StoryModels.MOON:StoryModels.SUN,0xf1d78d,false);}
  for(int i=0;i<3;i++){origin(100+i*200+(reduced?0:(float)Math.sin(time*.2+i)*12),95+i%2*55,-100,60,0);prop(StoryModels.CLOUD,night?0x8d9cab:0xf7f4e9,false);}
 }
 private void prop(int id,int color,boolean happy){switch(id){
 case StoryModels.CHILD:child(color,happy);break;
 case StoryModels.HOUSE:draw(box,0,.15f,0,.55f,.48f,.38f,color,0);draw(cone,0,-.47f,0,.8f,.43f,.58f,0xc88187,180);draw(box,-.15f,.35f,.4f,.12f,.28f,.035f,0x715f7f,0);draw(box,.25f,.08f,.41f,.13f,.13f,.03f,0xb2deea,0);break;
 case StoryModels.CLOUD:for(int i=0;i<4;i++)draw(sphere,(i-1.5f)*.33f,i%2*-.17f,0,.36f,.24f,.26f,color,0);break;
 case StoryModels.BIKE:for(int s:new int[]{-1,1}){draw(sphere,s*.49f,.28f,0,.31f,.31f,.08f,0x485365,0);draw(sphere,s*.49f,.28f,.08f,.23f,.23f,.03f,0xd1d5cb,0);}draw(box,0,.06f,.1f,.48f,.06f,.06f,color,-20);draw(box,.32f,-.19f,.1f,.05f,.38f,.05f,color,-15);draw(box,-.2f,-.2f,.1f,.19f,.06f,.07f,0x5e5467,0);break;
 case StoryModels.MOON:ball(0,0,0,.57f,0xf4e5b0);for(int i=0;i<3;i++)draw(sphere,-.22f+i*.2f,-.1f+i%2*.27f,.5f,.1f,.09f,.035f,0xd2c596,0);break;
 case StoryModels.FLOWER:case StoryModels.TULIP:draw(cylinder,0,.25f,0,.045f,.48f,.045f,0x629d77,0);for(int i=0;i<6;i++){float a=i*1.0472f;ball((float)Math.cos(a)*.28f,-.28f+(float)Math.sin(a)*.28f,0,.21f,id==StoryModels.TULIP?0xe79aaf:0xf0c56b);}ball(0,-.28f,.15f,.2f,0xa87a57);break;
 case StoryModels.PLANT:draw(cone,0,.4f,0,.38f,.3f,.38f,0xc18c73,0);leaves(0,-.15f,0,color);break;
 case StoryModels.SEEDLING:case StoryModels.SEAWEED:leaves(0,0,0,color);break;
 case StoryModels.CORAL:for(int i=0;i<5;i++)draw(sphere,(i-2)*.22f,.05f-Math.abs(i-2)*.15f,0,.13f,.42f,.15f,0xdca2b0,(i-2)*14);break;
 case StoryModels.FISH:draw(sphere,0,0,0,.53f,.28f,.27f,color,0);draw(cone,-.64f,0,0,.28f,.32f,.15f,0xe7be7c,-90);draw(sphere,-.05f,.22f,.2f,.21f,.11f,.1f,0xf0d7a3,reduced?0:(float)Math.sin(time*7)*14);ball(.3f,-.08f,.25f,.07f,0xfff8eb);ball(.32f,-.08f,.3f,.033f,0x314459);break;
 case StoryModels.BASKET:draw(sphere,0,.18f,0,.5f,.32f,.3f,0xbd976a,0);for(int i=0;i<9;i++){float a=i*.3927f;ball((float)Math.cos(a)*.43f,-.03f-(float)Math.sin(a)*.45f,0,.045f,0xb0855c);}break;
 case StoryModels.BREAD:draw(sphere,0,0,0,.53f,.38f,.35f,0xd6a267,0);for(int i=0;i<3;i++)draw(box,(i-1)*.22f,-.12f,.33f,.03f,.14f,.02f,0xf2d8ac,25);break;
 case StoryModels.TELESCOPE:draw(cylinder,0,-.18f,0,.18f,.5f,.18f,0x8e8ab3,-65);draw(box,0,.37f,0,.05f,.35f,.05f,0x9a886e,-20);draw(box,.2f,.4f,0,.05f,.35f,.05f,0x9a886e,20);break;
 case StoryModels.STAR:case StoryModels.SUN:ball(0,0,0,id==StoryModels.SUN?.35f:.2f,0xf2ce75);for(int i=0;i<(id==StoryModels.SUN?8:5);i++){float a=i*6.28318f/(id==StoryModels.SUN?8:5);draw(cone,(float)Math.sin(a)*.38f,(float)Math.cos(a)*.38f,0,.14f,.23f,.12f,0xf2ce75,-a*57.2958f);}break;
 case StoryModels.BEAR:case StoryModels.RABBIT:draw(sphere,0,.17f,0,.36f,.42f,.25f,color,0);ball(0,-.3f,.03f,.31f,color);for(int i:new int[]{-1,1}){draw(sphere,i*.22f,-.61f,0,.13f,id==StoryModels.RABBIT?.3f:.14f,.1f,color,i*10);ball(i*.11f,-.33f,.31f,.025f,0x364054);ball(i*.24f,.49f,.04f,.13f,color);}ball(0,-.23f,.34f,.055f,0xa77477);break;
 case StoryModels.SHELL:for(int i=0;i<7;i++)draw(sphere,(i-3)*.1f,-.1f,0,.13f,.45f,.23f,i%2==0?0xf0cfb2:0xe1ae9c,(i-3)*-12);break;
 case StoryModels.CRAB:draw(sphere,0,0,0,.38f,.23f,.23f,0xdf967c,0);for(int i:new int[]{-1,1}){for(int j=0;j<3;j++)draw(box,i*.4f,.06f+j*.12f,0,.21f,.035f,.035f,0xdf967c,i*25);ball(i*.55f,-.23f,0,.18f,0xdf967c);ball(i*.12f,-.26f,.12f,.05f,0x314558);}break;
 case StoryModels.TURTLE:draw(sphere,0,0,0,.43f,.32f,.3f,0x83ae86,0);ball(.47f,-.04f,0,.17f,0xa2c497);for(int i:new int[]{-1,1})for(int j:new int[]{-1,1})draw(sphere,i*.3f,j*.26f,-.1f,.18f,.08f,.1f,0xa2c497,i*j*30);ball(.53f,-.08f,.14f,.025f,0x314558);break;
 case StoryModels.CARROT:draw(cone,0,.1f,0,.25f,.6f,.25f,0xe8a15e,0);leaves(0,-.5f,0,0x82ac7b);break;
 case StoryModels.UMBRELLA:draw(sphere,0,-.3f,0,.62f,.25f,.35f,color,0);draw(cylinder,0,.15f,0,.035f,.6f,.035f,0xb49c7b,0);break;
 case StoryModels.BOOK:draw(box,0,0,0,.5f,.62f,.12f,color,0);draw(box,.035f,0,.13f,.43f,.54f,.03f,0xfff4d9,0);for(int i=0;i<4;i++)draw(box,.03f,-.3f+i*.17f,.17f,.3f,.012f,.005f,0xb6b6b1,0);break;
 case StoryModels.BOAT:draw(sphere,0,.3f,0,.6f,.18f,.3f,0xb9936d,0);draw(cylinder,0,-.1f,0,.035f,.56f,.035f,0x947b68,0);draw(cone,.22f,-.18f,0,.36f,.43f,.05f,0xf7e9c7,180);break;
 case StoryModels.APPLE:case StoryModels.STRAWBERRY:draw(sphere,0,.08f,0,.4f,id==StoryModels.APPLE?.4f:.5f,.32f,0xe5928a,0);draw(cylinder,0,-.4f,0,.04f,.14f,.04f,0x967d60,-20);draw(sphere,.17f,-.38f,0,.21f,.07f,.1f,0x82ac7b,-20);if(id==StoryModels.STRAWBERRY)for(int i=0;i<5;i++)ball((i%2-.5f)*.24f,-.1f+i*.08f,.32f,.023f,0xf3d79b);break;
 case StoryModels.KITE:draw(box,0,-.1f,0,.32f,.32f,.06f,color,45);for(int i=0;i<6;i++)ball((float)Math.sin(i)*.07f,.38f+i*.13f,0,.03f,0xdfc79a);break;
 case StoryModels.TENT:draw(cone,0,0,0,.65f,.58f,.4f,color,180);draw(cone,0,.17f,.38f,.24f,.35f,.04f,0x5a6370,180);break;
 case StoryModels.BUTTERFLY:for(int i:new int[]{-1,1})draw(sphere,i*(reduced?.3f:.2f+Math.abs((float)Math.sin(time*6))*.15f),0,0,.3f,.44f,.08f,i<0?0xe1a9c5:0x8abbd3,i*20);draw(sphere,0,0,.1f,.045f,.4f,.05f,0x75647c,0);break;
 case StoryModels.TREE:case StoryModels.PINE:draw(cylinder,0,.25f,0,.1f,.65f,.1f,0xab8769,0);if(id==StoryModels.PINE){for(int i=0;i<3;i++)draw(cone,0,-.1f-i*.3f,0,.55f-i*.1f,.4f,.45f-i*.1f,color,180);}else{ball(0,-.4f,0,.53f,color);ball(-.25f,-.24f,0,.4f,0x9bc795);}break;
 case StoryModels.PALETTE:draw(sphere,0,0,0,.6f,.4f,.1f,0xd7b88e,0);for(int i=0;i<4;i++)ball(-.3f+i*.2f,-.08f,.1f,.09f,new int[]{0xdd93a2,0x85bdb2,0xe7c47c,0xa698cc}[i]);break;
 case StoryModels.BRUSH:draw(cylinder,0,.1f,0,.06f,.52f,.06f,0xad91c5,-20);draw(cone,.2f,-.51f,0,.14f,.23f,.12f,color,160);break;
 case StoryModels.PLANK:draw(box,0,0,0,.7f,.13f,.18f,0xc3a277,0);for(int i=0;i<3;i++)ball(-.45f+i*.45f,0,.19f,.025f,0x9a8367);break;
 default:throw new IllegalArgumentException("No mesh model "+id);
 }}
 private void leaves(float x,float y,float z,int color){draw(cylinder,x,y+.25f,z,.035f,.4f,.035f,0x75a185,0);for(int i:new int[]{-1,1})draw(sphere,x+i*.18f,y-.08f,z,.24f,.11f,.1f,color,i*-30);}
 void pauseStage(){if(!stopped){stopped=true;onPause();}}
 void resumeStage(){if(stopped){stopped=false;last=0;onResume();}requestRender();}
 @Override protected void onDetachedFromWindow(){super.onDetachedFromWindow();Bitmap b=takeImage();if(b!=null)b.recycle();if(retainedImage!=null){retainedImage.recycle();retainedImage=null;}}
 private static final class Mesh {
  final FloatBuffer data;final int count;Mesh(float[]v){data=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();data.put(v).position(0);count=v.length/6;}
  static void vertex(float[]v,int[]offset,float x,float y,float z,float nx,float ny,float nz){int i=offset[0];v[i++]=x;v[i++]=y;v[i++]=z;v[i++]=nx;v[i++]=ny;v[i++]=nz;offset[0]=i;}
  static Mesh sphere(){float[]v=new float[10*16*6*6];int[]o={0};for(int a=0;a<10;a++)for(int b=0;b<16;b++)for(int corner:new int[]{0,1,2,0,2,3}){double lat=Math.PI*(a+(corner==1||corner==2?1:0))/10,lon=2*Math.PI*(b+(corner>=2?1:0))/16;float x=(float)(Math.sin(lat)*Math.cos(lon)),y=(float)Math.cos(lat),z=(float)(Math.sin(lat)*Math.sin(lon));vertex(v,o,x,y,z,x,y,z);}return new Mesh(v);}
  static Mesh box(){float[]v=new float[36*6];int[]o={0};for(int axis=0;axis<3;axis++)for(int sign:new int[]{-1,1})for(int c:new int[]{0,1,2,0,2,3}){float[]p={0,0,0},n={0,0,0};p[axis]=n[axis]=sign;p[(axis+1)%3]=c==0||c==3?-1:1;p[(axis+2)%3]=c<2?-1:1;vertex(v,o,p[0],p[1],p[2],n[0],n[1],n[2]);}return new Mesh(v);}
  static Mesh round(boolean tip){float[]v=new float[24*12*6];int[]o={0};for(int i=0;i<24;i++){double a=i*Math.PI/12,b=(i+1)*Math.PI/12;float ax=(float)Math.cos(a),az=(float)Math.sin(a),bx=(float)Math.cos(b),bz=(float)Math.sin(b),top=tip?0:1;float[][]p={{ax,-1,az},{bx,-1,bz},{bx*top,1,bz*top},{ax,-1,az},{bx*top,1,bz*top},{ax*top,1,az*top},{0,-1,0},{bx,-1,bz},{ax,-1,az},{0,1,0},{ax*top,1,az*top},{bx*top,1,bz*top}};for(int j=0;j<12;j++){float[]q=p[j];float ny=j>=9?1:j>=6?-1:tip?.5f:0;vertex(v,o,q[0],q[1],q[2],j>=6?0:q[0],ny,j>=6?0:q[2]);}}return new Mesh(v);}
 }
}
