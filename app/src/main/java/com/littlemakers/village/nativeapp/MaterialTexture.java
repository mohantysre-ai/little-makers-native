package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.graphics.*;
import android.opengl.*;
import java.io.*;
/** Bundled original photographic material atlas; GLES2 only, no runtime network or library. */
final class MaterialTexture {
 private final Context context;
 private int texture;
 MaterialTexture(Context context){this.context=context;}
 void create(int program){
  Bitmap bitmap=null;
  try(InputStream in=context.getAssets().open("materials/physical_atlas.webp")){
   BitmapFactory.Options options=new BitmapFactory.Options();options.inScaled=false;
   bitmap=BitmapFactory.decodeStream(in,null,options);
   if(bitmap==null)throw new IOException("Material atlas could not be decoded");
   int[]limit={0};GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE,limit,0);
   if(limit[0]<1)throw new IOException("No texture capability");
   if(bitmap.getWidth()>limit[0]){Bitmap scaled=Bitmap.createScaledBitmap(bitmap,limit[0],limit[0],true);bitmap.recycle();bitmap=scaled;}
   int[]ids={0};GLES20.glGenTextures(1,ids,0);texture=ids[0];
   GLES20.glActiveTexture(GLES20.GL_TEXTURE1);GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
   GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
   GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
   GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
   GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
   GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);
  }catch(IOException|RuntimeException unavailable){texture=0;}
  finally{if(bitmap!=null)bitmap.recycle();GLES20.glActiveTexture(GLES20.GL_TEXTURE0);}
  GLES20.glUseProgram(program);GLES20.glUniform1i(GLES20.glGetUniformLocation(program,"uAtlas"),1);
  GLES20.glUniform1f(GLES20.glGetUniformLocation(program,"uUseAtlas"),texture==0?0:1);
 }
 void bind(){GLES20.glActiveTexture(GLES20.GL_TEXTURE1);GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);GLES20.glActiveTexture(GLES20.GL_TEXTURE0);}
 boolean available(){return texture!=0;}
 static int kind(int argb){int color=argb&0xffffff;switch(color){
 case 0xc5a47e:case 0xf2ead8:case 0xd7b58b:case 0xcaa77e:case 0xcead7d:case 0xd0a879:case 0x9b7057:case 0xd7b17a:case 0xc5b195:case 0xb4926c:case 0xc3a277:case 0xc5a47f:return 2;
 case 0x929c98:case 0xb3b5a0:case 0xa1aaa1:case 0xbcc0ae:case 0x969d98:return 4;
 case 0xe1d1a9:case 0xe0cf9e:case 0xe3d1aa:case 0x65756b:case 0x55615c:return 5;
 default:return 0;}}
 static String fragment(boolean stage){String normal=stage?"vN":"vNormal",position=stage?"vP":"vPosition";
 return "precision mediump float;uniform sampler2D uAtlas;uniform float uUseAtlas;uniform vec3 uColor;uniform float uTime;uniform float uMaterial;varying vec3 "+normal+";varying vec3 "+position+";void main(){vec3 p="+position+(stage?"*.022;":";")+"vec3 n=normalize("+normal+");vec3 l=normalize(vec3(-.45,"+(stage?"-.7":".7")+",1.));float detail=1.;float spec=.025;if(uMaterial>1.5&&uUseAtlas>.5){vec3 an=abs(n);vec2 uv=an.z>.55?p.xy:(an.y>.55?p.xz:p.yz);float tile=uMaterial<2.5?0.:(uMaterial<3.5?2.:(uMaterial<4.5?1.:3.));vec2 offset=vec2(mod(tile,2.),floor(tile/2.))*.5;vec3 tex=texture2D(uAtlas,offset+(.012+fract(uv*1.1)*.976)*.5).rgb;float lum=dot(tex,vec3(.2126,.7152,.0722));detail=.72+.55*lum;}else if(uMaterial>.5&&uMaterial<1.5){float ripple=sin(p.x*3.4+uTime*.6)*cos(p.y*4.1-uTime*.4);n=normalize(n+vec3(ripple*.045,0.,0.));detail=.94+.06*ripple;spec=.08;}float diffuse=max(dot(n,l),0.);float shine=pow(max(dot(n,normalize(l+vec3(0.,0.,1.))),0.),24.);vec3 c=uColor*detail*(.46+.48*diffuse)+vec3(spec)*shine;gl_FragColor=vec4(clamp(c,0.,1.),1.);}";}
}
