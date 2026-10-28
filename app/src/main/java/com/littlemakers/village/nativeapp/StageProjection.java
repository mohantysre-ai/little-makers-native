package com.littlemakers.village.nativeapp;
/** Keep model dimensions uniform in screen pixels while retaining normalized scene positions. */
final class StageProjection {
 static float modelYScale(int width,int height,float sceneHeight){if(width<=0||height<=0||!Float.isFinite(sceneHeight)||sceneHeight<=0)throw new IllegalArgumentException("Invalid viewport");return width*sceneHeight/(640f*height);}
 static float modelFit(int width,int height,float sceneHeight){return Math.min(1,1.25f/modelYScale(width,height,sceneHeight));}
}
