package com.littlemakers.village.nativeapp;
/** Reusable center-cover UV transform; never samples outside the bundled image. */
final class BackdropCrop {
 static void cover(float imageAspect,float viewAspect,float pan,float[]out){if(!(imageAspect>0)||!(viewAspect>0)||out.length<4)throw new IllegalArgumentException("Invalid viewport");float sx=Math.min(1,viewAspect/imageAspect),sy=Math.min(1,imageAspect/viewAspect);out[0]=sx;out[1]=sy;out[2]=Math.max(0,Math.min(1-sx,(1-sx)*.5f+pan));out[3]=(1-sy)*.5f;}
 private BackdropCrop(){}
}
