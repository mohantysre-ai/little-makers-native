package com.littlemakers.village.nativeapp;
import java.util.*;
/** Seven original extruded polygon pieces with orientation-aware drag and snap. */
final class TangramState {
 static final int N=7;static final int[] KIND={9,9,9,9,9,6,10};
 static final float[] SIZE={43,43,33,24,24,27,34};
 static final String[] NAMES={"Large triangle 1","Large triangle 2","Medium triangle","Small triangle 1","Small triangle 2","Square","Parallelogram"};
 final int level;final float[] x=new float[N],y=new float[N];final int[] rotation=new int[N],placed=new int[N];int selected;
 TangramState(int level){if(level<0||level>=24)throw new IllegalArgumentException();this.level=level;Arrays.fill(placed,-1);for(int i=0;i<N;i++){x[i]=50+i*89;y[i]=355;}}
 float tx(int i){float[][]p={{170,290,410,145,485,280,385},{165,270,380,470,485,210,330},{170,290,410,160,490,280,390},{160,280,405,145,480,270,390}};return p[level%4][i]+(level/4%2==0?0:10);}
 float ty(int i){float[][]p={{95,95,100,215,210,220,220},{120,105,95,100,225,225,225},{100,100,100,220,220,220,220},{105,90,110,225,215,225,215}};return p[level%4][i];}
 int targetRotation(int i){return Math.floorMod(new int[]{0,2,4,6,0,1,0}[i]+level/4,8);}
 void rotate(){if(placed[selected]<0)rotation[selected]=(rotation[selected]+1)%8;}
 boolean compatible(int piece,int target){return piece>=0&&piece<N&&target>=0&&target<N&&KIND[piece]==KIND[target]&&SIZE[piece]==SIZE[target];}
 boolean place(int piece,int target){if(!compatible(piece,target)||placed[piece]>=0)return false;for(int p:placed)if(p==target)return false;if(rotation[piece]!=targetRotation(target))return false;placed[piece]=target;x[piece]=tx(target);y[piece]=ty(target);return true;}
 boolean drop(int piece,float xx,float yy){if(piece<0||piece>=N||placed[piece]>=0||!Float.isFinite(xx)||!Float.isFinite(yy))return false;x[piece]=Math.max(30,Math.min(610,xx));y[piece]=Math.max(45,Math.min(385,yy));for(int t=0;t<N;t++)if(Math.hypot(xx-tx(t),yy-ty(t))<34&&place(piece,t))return true;x[piece]=50+piece*89;y[piece]=355;return false;}
 int count(){int n=0;for(int p:placed)if(p>=0)n++;return n;}boolean won(){return count()==N;}
 String save(){StringBuilder b=new StringBuilder();for(int i=0;i<N;i++){if(i>0)b.append(',');b.append(rotation[i]).append(':').append(placed[i]);}return b.toString();}
 boolean restore(String value){try{String[]p=value.split(",",-1);if(p.length!=N)return false;int[]r=new int[N],v=new int[N];boolean[]used=new boolean[N];for(int i=0;i<N;i++){String[]pair=p[i].split(":",-1);if(pair.length!=2)return false;r[i]=Integer.parseInt(pair[0]);v[i]=Integer.parseInt(pair[1]);if(r[i]<0||r[i]>7||v[i]< -1||v[i]>=N)return false;if(v[i]>=0){if(used[v[i]]||!compatible(i,v[i])||r[i]!=targetRotation(v[i]))return false;used[v[i]]=true;}}for(int i=0;i<N;i++){rotation[i]=r[i];placed[i]=v[i];x[i]=v[i]<0?50+i*89:tx(v[i]);y[i]=v[i]<0?355:ty(v[i]);}return true;}catch(RuntimeException e){return false;}}
}
