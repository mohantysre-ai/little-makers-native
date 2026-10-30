package com.littlemakers.village.nativeapp;

/** Bounded perspective camera; preserves the whole interactive board at every aspect ratio. */
final class StoryLens {
 static float distance(int width,int height,float worldHeight){
  float aspect=Math.max(1,width)/(float)Math.max(1,height);
  float half=Math.max(worldHeight*.5f,320/aspect);
  return half/(float)Math.tan(Math.toRadians(19))*1.04f+100;
 }
 static float orbit(float seconds,boolean reduced){return reduced?0:(float)Math.sin(seconds*.16f)*32;}
 static float lift(float seconds,boolean reduced){return reduced?0:(float)Math.sin(seconds*.12f)*16;}
 static float focus(float value,float center){return center+Math.max(-28,Math.min(28,(value-center)*.10f));}
 private StoryLens(){}
}
