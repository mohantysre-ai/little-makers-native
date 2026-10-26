package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;

public final class ThemeManager {
 private final Context context;public final int text,background,surface,soft,border,accent,mint,rose,gold;public final boolean dark;
 public ThemeManager(Context context){this.context=context;String mode=context.getSharedPreferences("family",0).getString("theme","system");dark=mode.equals("dark")||(mode.equals("system")&&(context.getResources().getConfiguration().uiMode&Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES);text=dark?Color.WHITE:0xff202941;background=dark?0xff171b2d:0xfff6f4ff;surface=dark?0xff262d43:Color.WHITE;soft=dark?0xff343954:0xffebe7fc;border=dark?0xff647092:0xffc5c2df;accent=dark?0xff574491:0xffe4ddfa;mint=dark?0xff167c6d:0xffbcece1;rose=dark?0xffaa345e:0xfff7c6d8;gold=dark?0xff876000:0xfff4e09f;}
 public int dp(float value){return Math.round(value*context.getResources().getDisplayMetrics().density);}
 private static final Typeface heading=Typeface.create("sans-serif",Typeface.BOLD),body=Typeface.create("sans-serif",Typeface.NORMAL);
 public Typeface font(boolean display){return display?heading:body;}
 public GradientDrawable panel(boolean primary){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{primary?accent:surface,primary?soft:soft});d.setCornerRadius(dp(22));d.setStroke(dp(2),border);return d;}
 public static void bindPress(android.view.View v){v.setOnTouchListener((target,event)->{int action=event.getActionMasked();if(action!=android.view.MotionEvent.ACTION_DOWN&&action!=android.view.MotionEvent.ACTION_UP&&action!=android.view.MotionEvent.ACTION_CANCEL)return false;boolean reduced=target.getContext().getSharedPreferences("family",0).getBoolean("reduce_motion",false)||android.provider.Settings.Global.getFloat(target.getContext().getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)==0;if(reduced)return false;if(action==android.view.MotionEvent.ACTION_DOWN){target.animate().cancel();target.animate().scaleX(.94f).scaleY(.94f).setDuration(70).start();}else if(action==android.view.MotionEvent.ACTION_UP||action==android.view.MotionEvent.ACTION_CANCEL){target.animate().cancel();target.animate().scaleX(1).scaleY(1).setDuration(190).setInterpolator(new android.view.animation.OvershootInterpolator(.7f)).start();}return false;});}
 public int learningColor(int index){return new int[]{mint,rose,gold}[Math.floorMod(index,3)];}
 public static double contrast(int foreground,int bg){double a=luminance(foreground),b=luminance(bg);return(Math.max(a,b)+.05)/(Math.min(a,b)+.05);}
 private static double luminance(int c){double[]v={Color.red(c)/255.,Color.green(c)/255.,Color.blue(c)/255.};for(int i=0;i<3;i++)v[i]=v[i]<=.04045?v[i]/12.92:Math.pow((v[i]+.055)/1.055,2.4);return v[0]*.2126+v[1]*.7152+v[2]*.0722;}
}
