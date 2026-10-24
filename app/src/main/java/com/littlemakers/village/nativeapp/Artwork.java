package com.littlemakers.village.nativeapp;
import android.graphics.*;

/** Original simple geometric characters and symbols drawn with Android Canvas. */
final class Artwork {
 static void shape(Canvas c,float x,float y,float radius,int kind,int color,Paint p){p.setStyle(Paint.Style.FILL);p.setColor(color);if(kind%3==0)c.drawCircle(x,y,radius,p);else if(kind%3==1){Path path=new Path();path.moveTo(x,y-radius);path.lineTo(x+radius,y+radius);path.lineTo(x-radius,y+radius);path.close();c.drawPath(path,p);}else c.drawRoundRect(x-radius,y-radius,x+radius,y+radius,14,14,p);}
 static void buddy(Canvas c,float x,float y,float size,int color,String accessory,Paint p){
  p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(x-size*.48f,y-size*.42f,x+size*.48f,y+size*.42f,size*.25f,size*.25f,p);p.setColor(0xff202941);c.drawCircle(x-size*.17f,y-size*.05f,size*.035f,p);c.drawCircle(x+size*.17f,y-size*.05f,size*.035f,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(size*.035f);c.drawArc(x-size*.13f,y,x+size*.13f,y+size*.15f,0,180,false,p);p.setStyle(Paint.Style.FILL);
  if(accessory.equals("hat")){p.setColor(0xffe5b77e);c.drawRoundRect(x-size*.35f,y-size*.58f,x+size*.35f,y-size*.4f,8,8,p);c.drawRect(x-size*.5f,y-size*.43f,x+size*.5f,y-size*.38f,p);}
  if(accessory.equals("bow")){shape(c,x-size*.25f,y-size*.38f,size*.1f,1,0xffde8faa,p);shape(c,x-size*.05f,y-size*.38f,size*.1f,1,0xffde8faa,p);}
  if(accessory.equals("star")){p.setColor(0xffebc56e);Path s=new Path();for(int i=0;i<10;i++){double angle=i*Math.PI/5-Math.PI/2;float r=size*(i%2==0?.09f:.04f);float sx=x+size*.29f+(float)Math.cos(angle)*r,sy=y+size*.22f+(float)Math.sin(angle)*r;if(i==0)s.moveTo(sx,sy);else s.lineTo(sx,sy);}s.close();c.drawPath(s,p);}
 }
}
