package com.littlemakers.village.nativeapp;
import android.content.Context;import android.graphics.*;import android.view.*;import android.widget.GridLayout;
/** Six independent accessible toys; one small Canvas per target, never another GL context. */
final class AnimatedToyDock extends GridLayout {
 interface Listener{void chosen(int index);}private boolean running=true;private final boolean reduced;
 private static final String[]NAMES={"Clay","Paint","Shapes","Stories","Texture","Adventures"};
 private static final int[]COLORS={0xffe7a878,0xffef96bb,0xff92bbe9,0xffb2a0dd,0xff83c9a4,0xffedc56e};
 AnimatedToyDock(Context c,ThemeManager theme,boolean reduced,Listener listener){super(c);this.reduced=reduced;setColumnCount(3);setRowCount(2);setPadding(theme.dp(4),0,theme.dp(4),0);for(int i=0;i<NAMES.length;i++){final int item=i;Toy toy=new Toy(c,theme,i);toy.setContentDescription(NAMES[i]+", open activity");toy.setFocusable(true);toy.setOnClickListener(v->{v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);listener.chosen(item);});GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(i/3,1f),GridLayout.spec(i%3,1f));lp.width=0;lp.height=0;lp.setGravity(Gravity.FILL);addView(toy,lp);}}
 void stop(){running=false;for(int i=0;i<getChildCount();i++)getChildAt(i).invalidate();}
 @Override protected void onDetachedFromWindow(){stop();super.onDetachedFromWindow();}
 private final class Toy extends View{
  final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);final int[]dots={0xfff7d85e,0xff64bda9,0xff73a6e0,0xffb084c6};final Path path=new Path();final ThemeManager theme;final int kind;long pressAt;boolean pressed;
  Toy(Context c,ThemeManager theme,int kind){super(c);this.theme=theme;this.kind=kind;paint.setTypeface(theme.font(true));setMinimumHeight(theme.dp(64));}
  @Override public boolean onTouchEvent(android.view.MotionEvent e){if(e.getActionMasked()==MotionEvent.ACTION_DOWN){pressed=true;pressAt=android.os.SystemClock.uptimeMillis();invalidate();}if(e.getActionMasked()==MotionEvent.ACTION_CANCEL||e.getActionMasked()==MotionEvent.ACTION_UP){pressed=false;invalidate();}return super.onTouchEvent(e);}
  void color(int c){paint.setColor(c);paint.setStyle(Paint.Style.FILL);}void oval(Canvas c,float l,float t,float r,float b,int col){color(col);c.drawOval(l,t,r,b,paint);}
  @Override protected void onDraw(Canvas c){super.onDraw(c);float width=getWidth(),height=getHeight(),unit=Math.min(width/100f,(height-theme.dp(23))/94f);float time=reduced?0:android.os.SystemClock.uptimeMillis()/1000f;float bob=reduced?0:(float)Math.sin(time*2.2f+kind)*3;float response=pressed?.90f:1;
   c.save();c.translate(width/2,(height-theme.dp(23))/2);c.scale(unit*response,unit*response);oval(c,-36,29,36,39,theme.dark?0xff111827:0x2260747e);c.translate(0,bob);c.rotate(reduced?0:(float)Math.sin(time*1.4f+kind)*3);
   int col=COLORS[kind];
   if(kind==0){oval(c,-36,-2,36,33,0xffaa654b);oval(c,-34,-7,34,25,col);oval(c,-24,-29,24,12,col);oval(c,-13,-35,14,-12,0xffffdbab);}
   if(kind==1){oval(c,-37,-31,37,30,col);oval(c,7,-10,25,8,theme.background);for(int j=0;j<4;j++)oval(c,-28+j*12,-25+(j%2)*9,-17+j*12,-14+(j%2)*9,dots[j]);color(0xff996947);c.save();c.rotate(32);c.drawRoundRect(14,-36,23,35,4,4,paint);color(0xff527eaa);c.drawRoundRect(12,-42,25,-21,4,4,paint);c.restore();}
   if(kind==2){color(col);c.drawRoundRect(-32,-5,3,30,5,5,paint);oval(c,0,-32,34,2,0xffe6b657);path.reset();path.moveTo(-38,-11);path.lineTo(-10,-39);path.lineTo(10,-11);path.close();color(0xffb2a0dd);c.drawPath(path,paint);}
   if(kind==3){color(0xff8d71b1);c.drawRoundRect(-36,-31,36,31,6,6,paint);color(0xfffff0cb);c.drawRoundRect(-30,-26,30,24,3,3,paint);color(col);c.drawRect(-2,-26,2,25,paint);color(0xffdfb054);path.reset();for(int j=0;j<10;j++){double a=-Math.PI/2+j*Math.PI/5;float r=j%2==0?13:6;float x=15+(float)Math.cos(a)*r,y=-4+(float)Math.sin(a)*r;if(j==0)path.moveTo(x,y);else path.lineTo(x,y);}path.close();c.drawPath(path,paint);}
   if(kind==4){color(0xff7eaa86);c.drawRoundRect(-4,-4,4,31,3,3,paint);oval(c,-30,1,0,17,col);oval(c,0,-7,29,10,col);for(int j=0;j<5;j++){double a=j*Math.PI*2/5;float x=(float)Math.cos(a)*14,y=-20+(float)Math.sin(a)*14;oval(c,x-11,y-11,x+11,y+11,0xffedb3ca);}oval(c,-9,-29,9,-11,0xfff1d274);}
   if(kind==5){color(col);c.drawRoundRect(-28,-30,28,28,18,18,paint);oval(c,-20,-12,-7,3,Color.WHITE);oval(c,7,-12,20,3,Color.WHITE);oval(c,-16,-9,-10,-1,0xff253549);oval(c,11,-9,17,-1,0xff253549);paint.setColor(0xff785f3c);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3);c.drawArc(-10,2,10,16,0,180,false,paint);paint.setStyle(Paint.Style.FILL);}
   // A restrained glossy highlight moves with each object, never a flashing full-screen effect.
   oval(c,-20,-27,-6,-20,0x88ffffff);c.restore();color(theme.text);paint.setTextAlign(Paint.Align.CENTER);paint.setTextSize(theme.dp(11)*getResources().getConfiguration().fontScale);c.drawText(NAMES[kind],width/2,height-theme.dp(7),paint);
   if(running&&!reduced&&isShown()&&hasWindowFocus())postInvalidateDelayed(33);
  }
 }
}
