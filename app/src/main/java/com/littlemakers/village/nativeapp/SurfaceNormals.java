package com.littlemakers.village.nativeapp;
import java.util.Arrays;
/** Area-weighted smooth normals, recalculated from the actual sculpted grid. */
final class SurfaceNormals {
 static void calculate(float[][]p,int rows,int cols,float[][]out){for(float[]n:out)Arrays.fill(n,0);for(int r=0;r<rows-1;r++)for(int c=0;c<cols;c++){int a=r*cols+c,b=(r+1)*cols+c,d=r*cols+(c+1)%cols,e=(r+1)*cols+(c+1)%cols;face(p,out,a,e,b);face(p,out,a,d,e);}for(int row:new int[]{0,rows-1}){float x=0,y=0,z=0;for(int c=0;c<cols;c++){float[]n=out[row*cols+c];x+=n[0];y+=n[1];z+=n[2];}for(int c=0;c<cols;c++){float[]n=out[row*cols+c];n[0]=x;n[1]=y;n[2]=z;}}for(int i=0;i<out.length;i++){float[]n=out[i];float len=(float)Math.sqrt(n[0]*n[0]+n[1]*n[1]+n[2]*n[2]);if(len<1e-8f){n[0]=0;n[1]=i/cols<rows/2?1:-1;n[2]=0;}else for(int k=0;k<3;k++)n[k]/=len;}}
 private static void face(float[][]p,float[][]n,int a,int b,int c){float ux=p[b][0]-p[a][0],uy=p[b][1]-p[a][1],uz=p[b][2]-p[a][2],vx=p[c][0]-p[a][0],vy=p[c][1]-p[a][1],vz=p[c][2]-p[a][2];float x=uy*vz-uz*vy,y=uz*vx-ux*vz,z=ux*vy-uy*vx;add(n[a],x,y,z);add(n[b],x,y,z);add(n[c],x,y,z);}
 private static void add(float[]n,float x,float y,float z){n[0]+=x;n[1]+=y;n[2]+=z;}
}
