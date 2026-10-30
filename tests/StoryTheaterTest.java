package com.littlemakers.village.nativeapp;
public final class StoryTheaterTest {
 static int checks;static void check(boolean b){checks++;if(!b)throw new AssertionError("Check "+checks);}
 public static void main(String[]args){
  for(int count=1;count<=64;count++)for(int endings=1;endings<=4;endings++){
   StoryTheaterState s=new StoryTheaterState(count,endings);check(!s.help());check(!s.next());check(!s.choose(0));s.begun=true;
   for(int step=0;step<count;step++){check(!s.next());check(!s.choose(0));check(s.help());check(!s.help());StoryTheaterState restored=new StoryTheaterState(count,endings);restored.restore(s.step,s.ending,s.begun,s.placed);check(restored.step==step&&restored.placed&&restored.begun);s=restored;check(s.next());}
   check(!s.finished());check(!s.help());check(!s.choose(-1));check(!s.choose(endings));for(int a=0;a<endings;a++){check(s.choose(a));check(s.finished());}s.restart();check(!s.begun&&!s.placed&&s.step==0&&s.ending==-1);
  }
  StoryTheaterState bad=new StoryTheaterState(3,2);bad.restore(Integer.MAX_VALUE,99,false,true);check(bad.step==3&&bad.ending==-1&&!bad.placed);bad.restore(-4,1,false,true);check(bad.step==0&&bad.ending==-1&&bad.placed&&bad.begun);
  for(int w:new int[]{240,320,480,800,1200})for(int h:new int[]{240,480,800,1400}){
   float d=StoryLens.distance(w,h,840);check(Float.isFinite(d)&&d>0);float half=d*(float)Math.tan(Math.toRadians(19));check(half>420&&half*w/h>320);
   for(int t=0;t<100;t++){check(Math.abs(StoryLens.orbit(t,false))<=32);check(Math.abs(StoryLens.lift(t,false))<=16);check(StoryLens.orbit(t,true)==0&&StoryLens.lift(t,true)==0);}
  }
  check(StoryLens.focus(-10000,320)==292);check(StoryLens.focus(10000,320)==348);
  for(int species=0;species<3;species++){float[]mesh=WildlifeGeometry.build(species);check(mesh.length%30==0&&mesh.length>10000);check(mesh==WildlifeGeometry.build(species));int eyes=0,fur=0;for(int i=0;i<mesh.length;i+=10){for(int j=0;j<10;j++)check(Float.isFinite(mesh[i+j]));double normal=Math.sqrt(mesh[i+3]*mesh[i+3]+mesh[i+4]*mesh[i+4]+mesh[i+5]*mesh[i+5]);check(Math.abs(normal-1)<.00001);check(Math.abs(mesh[i])<1.3&&Math.abs(mesh[i+1])<1.1&&Math.abs(mesh[i+2])<.4);check(mesh[i+6]>=0&&mesh[i+6]<=1&&mesh[i+7]>=0&&mesh[i+7]<=1&&mesh[i+8]>=0&&mesh[i+8]<=1);if(mesh[i+9]==1)eyes++;if(mesh[i+9]==0)fur++;}check(eyes>0&&fur>eyes);}
  System.out.println("PASS "+checks+" story progression, restore, child-control and perspective-fit checks. Not a photorealistic asset test.");
 }
}
