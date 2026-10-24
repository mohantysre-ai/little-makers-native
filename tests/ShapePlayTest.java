package com.littlemakers.village.nativeapp;
public final class ShapePlayTest {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[]args){ShapePlayState s=new ShapePlayState();
  s.pick(0,1.8f);s.drag(-2.5f,-2.25f);check(s.release()==-1);check(!s.complete());
  s.advance(.05f,true);check(Math.abs(s.x[1])<.0001f&&Math.abs(s.y[1]-1.8f)<.0001f);
  for(int i=0;i<3;i++){s.pick(ShapePlayState.homeX(i),1.8f);check(s.selected==i);s.drag(ShapePlayState.homeX(i),ShapePlayState.HOME_Y);check(s.release()==i);check(!s.match(i));}
  check(s.complete());s.reset();check(!s.complete());s.pick(-2.5f,1.8f);s.drag(99,99);check(s.x[0]<=3.3f&&s.y[0]<=3.5f);s.cancel();s.advance(100,false);check(s.selected==-1&&Float.isFinite(s.x[0]));
  for(int i=0;i<100;i++)s.advance(.05f,false);check(Math.abs(s.x[0]+2.5f)<.001);System.out.println("3D shape drag, mismatch, snap, reset and repeat-reward guards passed");
 }
}
