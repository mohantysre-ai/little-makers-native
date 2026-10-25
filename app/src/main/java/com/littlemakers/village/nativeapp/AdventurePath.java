package com.littlemakers.village.nativeapp;
import java.util.*;
/** Bounded local grid routing. Diagonal moves cannot cut through the landmark collision volumes. */
final class AdventurePath {
 static final int NX=45,NZ=37;static final float STEP=.25f;
 static float x(int n){return(n%NX)*STEP-5.5f;}static float z(int n){return(n/NX)*STEP-4.5f;}
 static boolean clear(AdventureState s,float ax,float az,float bx,float bz){int steps=Math.max(1,(int)(Math.hypot(bx-ax,bz-az)/.08f)+1);for(int i=0;i<=steps;i++){float t=(float)i/steps;if(!s.walkable(ax+(bx-ax)*t,az+(bz-az)*t))return false;}return true;}
 static float[][] find(AdventureState s,float ax,float az,float bx,float bz){
  if(clear(s,ax,az,bx,bz))return new float[][]{{bx,bz}};
  int count=NX*NZ,start=-1,goal=-1;float startDistance=Float.MAX_VALUE,goalDistance=Float.MAX_VALUE;boolean[]valid=new boolean[count];
  for(int i=0;i<count;i++){valid[i]=s.walkable(x(i),z(i));if(!valid[i])continue;float a=(float)Math.hypot(x(i)-ax,z(i)-az),b=(float)Math.hypot(x(i)-bx,z(i)-bz);if(a<startDistance&&clear(s,ax,az,x(i),z(i))){startDistance=a;start=i;}if(b<goalDistance){goalDistance=b;goal=i;}}
  if(start<0||goal<0)return new float[0][];int[]parent=new int[count],queue=new int[count];Arrays.fill(parent,-2);parent[start]=-1;int head=0,tail=0;queue[tail++]=start;
  while(head<tail&&parent[goal]==-2){int at=queue[head++],col=at%NX,row=at/NX;for(int dx=-1;dx<=1;dx++)for(int dz=-1;dz<=1;dz++){if(dx==0&&dz==0)continue;int c=col+dx,r=row+dz;if(c<0||c>=NX||r<0||r>=NZ)continue;int next=r*NX+c;if(!valid[next]||parent[next]!=-2)continue;if(dx!=0&&dz!=0&&(!valid[row*NX+c]||!valid[r*NX+col]))continue;parent[next]=at;queue[tail++]=next;}}
  if(parent[goal]==-2)return new float[0][];ArrayList<float[]>route=new ArrayList<>();for(int at=goal;at>=0;at=parent[at])route.add(new float[]{x(at),z(at)});Collections.reverse(route);if(clear(s,x(goal),z(goal),bx,bz))route.add(new float[]{bx,bz});return route.toArray(new float[0][]);
 }
}
