package com.littlemakers.village.nativeapp;
public final class BackdropCropTest {
 public static void main(String[]args){float[]c=new float[4];int checked=0;for(float image:new float[]{.6f,1,1.5f,2})for(float view:new float[]{.5f,.75f,1,1.7f,2.4f})for(float pan:new float[]{-9,-.02f,0,.02f,9}){BackdropCrop.cover(image,view,pan,c);if(c[0]<=0||c[1]<=0||c[0]>1||c[1]>1||c[2]<0||c[3]<0||c[2]+c[0]>1.00001f||c[3]+c[1]>1.00001f)throw new AssertionError("Image edge sampled");if(Math.abs(c[0]*image/c[1]-view)>.0001f)throw new AssertionError("Stretched image");checked++;}System.out.println("PASS: "+checked+" portrait/landscape/pan combinations preserve aspect and image bounds.");}
}
