package com.littlemakers.village.nativeapp;
import android.graphics.*;

/** Brief, predictable success particles. No blinking, random rewards, or reward mutation. */
final class PlayMotion {
 static void celebration(Canvas c,Paint p,float seconds,float x,float y,int[]colors){
  if(seconds<0||seconds>1.6f)return;float t=seconds/1.6f;
  for(int i=0;i<28;i++){double angle=(i*2.399963)-Math.PI/2;float speed=85+(i%5)*22;float px=x+(float)Math.cos(angle)*speed*t,py=y+(float)Math.sin(angle)*speed*t+95*t*t;p.setColor(colors[i%colors.length]);p.setAlpha(Math.round(255*(1-t)));p.setStyle(Paint.Style.FILL);c.save();c.translate(px,py);c.rotate(i*31+t*180);if(i%3==0)c.drawCircle(0,0,5*(1-t*.5f),p);else c.drawRoundRect(-4,-8,4,8,3,3,p);c.restore();}p.setAlpha(255);
 }
 private PlayMotion(){}
}
