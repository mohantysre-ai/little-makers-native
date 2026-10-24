package com.littlemakers.village.nativeapp;

/** Render-thread owned matching state; independent of Android for deterministic verification. */
final class ShapePlayState {
 static final float HOME_Y=-2.25f;
 final float[] x=new float[3],y=new float[3],age=new float[3];
 final boolean[] sorted=new boolean[3];
 int selected=-1;
 static float homeX(int i){return (i-1)*2.5f;}
 ShapePlayState(){reset();}
 void reset(){selected=-1;for(int i=0;i<3;i++){x[i]=homeX(i);y[i]=1.8f;sorted[i]=false;age[i]=1;}}
 void pick(float px,float py){selected=-1;for(int i=0;i<3;i++)if(!sorted[i]&&Math.hypot(px-x[i],py-y[i])<.95){selected=i;break;}}
 void drag(float px,float py){if(selected>=0){x[selected]=Math.max(-3.3f,Math.min(3.3f,px));y[selected]=Math.max(-3.4f,Math.min(3.5f,py));}}
 int release(){int i=selected;selected=-1;if(i>=0&&Math.hypot(x[i]-homeX(i),y[i]-HOME_Y)<.95&&match(i))return i;return -1;}
 void cancel(){selected=-1;}
 boolean match(int i){if(i<0||i>=3||sorted[i])return false;sorted[i]=true;age[i]=0;return true;}
 boolean complete(){return sorted[0]&&sorted[1]&&sorted[2];}
 void advance(float dt,boolean reduced){dt=Math.max(0,Math.min(.05f,dt));float blend=reduced?1:1-(float)Math.exp(-dt*12);for(int i=0;i<3;i++){age[i]+=dt;if(i!=selected){x[i]+=(homeX(i)-x[i])*blend;y[i]+=((sorted[i]?HOME_Y:1.8f)-y[i])*blend;}}}
}
