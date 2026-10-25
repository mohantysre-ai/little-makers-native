package com.littlemakers.village.nativeapp;
/** Sample-coverage tracing, independent of the platform's font outlines. */
final class TraceTrack {
 private final float[]points;private final boolean[]covered;
 TraceTrack(float[]xy){if(xy==null||xy.length<4||xy.length%2!=0)throw new IllegalArgumentException();points=xy.clone();for(float v:points)if(!Float.isFinite(v))throw new IllegalArgumentException();covered=new boolean[xy.length/2];}
 void stroke(float x0,float y0,float x1,float y1,float radius){if(!Float.isFinite(x0)||!Float.isFinite(y0)||!Float.isFinite(x1)||!Float.isFinite(y1)||!Float.isFinite(radius)||radius<=0)return;float dx=x1-x0,dy=y1-y0,length=dx*dx+dy*dy;if(!Float.isFinite(length))return;for(int i=0;i<covered.length;i++){float x=points[2*i],y=points[2*i+1],t=length==0?0:Math.max(0,Math.min(1,((x-x0)*dx+(y-y0)*dy)/length));if(Math.hypot(x-x0-t*dx,y-y0-t*dy)<=radius)covered[i]=true;}}
 int samples(){return covered.length;}float x(int i){return points[i*2];}float y(int i){return points[i*2+1];}boolean covered(int i){return covered[i];}
 int percent(){int n=0;for(boolean b:covered)if(b)n++;return n*100/covered.length;}
 boolean complete(){return percent()>=72;}
 String save(){StringBuilder b=new StringBuilder();for(boolean v:covered)b.append(v?'1':'0');return b.toString();}
 boolean restore(String s){if(s.length()!=covered.length||!s.matches("[01]+"))return false;for(int i=0;i<covered.length;i++)covered[i]=s.charAt(i)=='1';return true;}
}
