package com.littlemakers.village.nativeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;
import java.io.IOException;
import java.io.InputStream;

/** Interactive opening scene, not a timed loading gate. No network or account setup. */
public final class OpeningActivity extends Activity {
 private OpeningArt artwork;private SoundEngine sound;private boolean entering;
 @Override public void onCreate(Bundle saved){setTheme(R.style.AppTheme);super.onCreate(saved);if(android.os.Build.VERSION.SDK_INT>=31)Api31Splash.install(this);sound=new SoundEngine(this);ThemeManager theme=new ThemeManager(this);
  FrameLayout root=new FrameLayout(this);root.setBackgroundColor(0xff245d73);artwork=new OpeningArt(this);root.addView(artwork,new FrameLayout.LayoutParams(-1,-1));
  LinearLayout content=new LinearLayout(this);content.setOrientation(1);content.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);content.setPadding(theme.dp(28),theme.dp(24),theme.dp(28),theme.dp(24));
  TextView name=label("Little Makers",38,true);content.addView(name);TextView sub=label("A little world. Big imagination.",18,false);sub.setPadding(0,theme.dp(8),0,theme.dp(20));content.addView(sub);
  Button play=new Button(this);play.setAllCaps(false);play.setText("Let’s play  ›");play.setTextSize(22);play.setTypeface(theme.font(true));play.setTextColor(0xff26364d);play.setMinHeight(theme.dp(72));android.graphics.drawable.GradientDrawable fill=new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0xffffe9a2,0xfff1c765});fill.setCornerRadius(theme.dp(28));play.setBackground(fill);play.setElevation(theme.dp(5));play.setOnClickListener(v->{if(entering)return;entering=true;sound.note(1);v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);startActivity(new Intent(this,CreativeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP));finish();});content.addView(play,new LinearLayout.LayoutParams(-1,theme.dp(76)));
  TextView trust=label("Offline play · No ads · Made for curious minds",13,false);trust.setPadding(0,theme.dp(16),0,theme.dp(12));content.addView(trust);
  Button privacy=new Button(this);privacy.setAllCaps(false);privacy.setText("Privacy & grown-up information");privacy.setTextColor(Color.WHITE);privacy.setTextSize(14);privacy.setMinHeight(theme.dp(64));privacy.setBackgroundColor(Color.TRANSPARENT);privacy.setOnClickListener(v->startActivity(new Intent(this,MainActivity.class).putExtra("destination","privacy")));content.addView(privacy,new LinearLayout.LayoutParams(-1,theme.dp(64)));
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(content,new ScrollView.LayoutParams(-1,-1));FrameLayout.LayoutParams panel=new FrameLayout.LayoutParams(-1,-1);if(getResources().getDisplayMetrics().widthPixels>getResources().getDisplayMetrics().heightPixels){panel.width=Math.round(getResources().getDisplayMetrics().widthPixels*.58f);panel.gravity=Gravity.END;panel.setMargins(theme.dp(12),theme.dp(16),theme.dp(12),theme.dp(16));android.graphics.drawable.GradientDrawable glass=new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0x88415467,0xcc20374b});glass.setCornerRadius(theme.dp(28));scroll.setBackground(glass);}root.addView(scroll,panel);root.setOnApplyWindowInsetsListener((v,i)->{content.setPadding(theme.dp(28)+i.getSystemWindowInsetLeft(),theme.dp(24)+i.getSystemWindowInsetTop(),theme.dp(28)+i.getSystemWindowInsetRight(),theme.dp(16)+i.getSystemWindowInsetBottom());return i;});setContentView(root);root.requestApplyInsets();getWindow().getDecorView().setSystemUiVisibility(0);getWindow().setStatusBarColor(0xff245d73);getWindow().setNavigationBarColor(0xff20374b);
 }
 private static final class Api31Splash {static void install(OpeningActivity a){a.getSplashScreen().setOnExitAnimationListener(view->{boolean reduced=a.getSharedPreferences("family",0).getBoolean("reduce_motion",false)||android.provider.Settings.Global.getFloat(a.getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)==0;if(reduced)view.remove();else view.animate().alpha(0).setDuration(160).withEndAction(view::remove).start();});}}
 private TextView label(String value,int size,boolean bold){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(Color.WHITE);t.setGravity(Gravity.CENTER);t.setTypeface(new ThemeManager(this).font(bold));return t;}
 @Override protected void onResume(){super.onResume();entering=false;if(artwork!=null)artwork.invalidate();}
 @Override protected void onPause(){sound.stop();super.onPause();}
 @Override protected void onDestroy(){artwork.dispose();sound.stop();super.onDestroy();}
 private static final class OpeningArt extends View {
  private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);private Bitmap forest;private final Drawable mascot;private final ThemeManager theme;private final RectF scene=new RectF();
  OpeningArt(android.content.Context context){super(context);theme=new ThemeManager(context);mascot=context.getDrawable(R.drawable.brand_mascot);try(InputStream in=context.getAssets().open("webart/forest_backdrop.webp")){forest=BitmapFactory.decodeStream(in);}catch(IOException ignored){}setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
  boolean moving(){return !getContext().getSharedPreferences("family",0).getBoolean("reduce_motion",false)&&android.provider.Settings.Global.getFloat(getContext().getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)>0;}
  protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight(),time=moving()?SystemClock.uptimeMillis()/1000f:0;float slow=(float)Math.sin(time*.17f)*.015f;
   if(forest!=null){float scale=Math.max(w/forest.getWidth(),h/forest.getHeight())*(1.035f+slow);float iw=forest.getWidth()*scale,ih=forest.getHeight()*scale;scene.set((w-iw)/2,(h-ih)/2,(w+iw)/2,(h+ih)/2);c.drawBitmap(forest,null,scene,paint);}else c.drawColor(0xff478b8b);
   paint.setShader(new LinearGradient(0,0,0,h,new int[]{0x16375368,0x202d4b57,0xe720374b,0xff20374b},new float[]{0,.35f,.7f,1},Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,paint);paint.setShader(null);
   boolean wide=w>h;float size=wide?Math.min(w*.35f,h*.64f):Math.min(w*.64f,Math.max(theme.dp(140),(h-theme.dp(360))*.85f));float centerY=wide?h*.45f:Math.max(theme.dp(90),(h-theme.dp(370))*.48f);float centerX=wide?w*.23f:w/2;float bob=moving()?(float)Math.sin(time*1.7f)*theme.dp(5):0;
   paint.setColor(0x2efff2c3);c.drawCircle(centerX,centerY,size*.52f,paint);c.save();c.translate(centerX,centerY+bob);if(moving())c.rotate((float)Math.sin(time*.9f)*3);mascot.setBounds((int)(-size/2),(int)(-size/2),(int)(size/2),(int)(size/2));mascot.draw(c);c.restore();
   for(int i=0;i<9;i++){float x=w*(.12f+(i%4)*.24f),y=h*(.13f+(i%3)*.12f)+(moving()?(float)Math.sin(time*.7f+i)*theme.dp(9):0);paint.setColor(0x99fff4c4);c.drawCircle(x,y,theme.dp(i%2+2),paint);}
  }
  @Override public void onWindowFocusChanged(boolean focus){super.onWindowFocusChanged(focus);removeCallbacks(frame);if(focus){invalidate();if(moving())post(frame);}}
  @Override protected void onAttachedToWindow(){super.onAttachedToWindow();if(hasWindowFocus())post(frame);}
  private final Runnable frame=new Runnable(){public void run(){if(isAttachedToWindow()&&moving()&&hasWindowFocus()){invalidate();postDelayed(this,33);}}};
  @Override protected void onDetachedFromWindow(){removeCallbacks(frame);super.onDetachedFromWindow();}
  void dispose(){removeCallbacks(frame);if(forest!=null){forest.recycle();forest=null;}}
 }
}
