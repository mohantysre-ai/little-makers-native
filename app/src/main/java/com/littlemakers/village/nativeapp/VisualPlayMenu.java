package com.littlemakers.village.nativeapp;
import android.content.*;import android.app.*;import android.graphics.*;import android.view.*;import android.widget.*;import java.util.*;

/** One live mesh scene beneath nine large, native, screen-reader-labelled hit targets. */
final class VisualPlayMenu extends FrameLayout {
 interface Listener{void chosen(int index);}
 private NativeStageSurface surface;private ToyFallback fallback;private final ThemeManager theme;private final int[]icons;private final String[]labels;private final boolean reduced;private boolean paused;private final int rows;
 private static final int[]COLORS={0xf0cf83,0x92c9bc,0x9bbde2,0xe5b9c9,0xb4addb,0x91c6cf,0xedba90,0xb4cc97,0xdab8df};
 VisualPlayMenu(Context c,ThemeManager theme,String[]labels,int[]icons,Listener listener){super(c);this.theme=theme;this.reduced=c.getSharedPreferences("family",0).getBoolean("reduce_motion",false)||android.provider.Settings.Global.getFloat(c.getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)==0;this.labels=labels.clone();this.icons=icons.clone();this.rows=(labels.length+2)/3;setClipToPadding(false);setMinimumHeight(theme.dp(340));boolean es2=((ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion>=0x20000;
  if(es2){final NativeStageSurface[]holder={null};holder[0]=new NativeStageSurface(c,theme.dark,()->{if(surface==holder[0]){surface.pauseStage();removeView(surface);surface=null;fallback();}});surface=holder[0];addView(surface,new FrameLayout.LayoutParams(-1,-1));surface.submit(scene());}else fallback();
  GridLayout grid=new GridLayout(c);grid.setColumnCount(3);grid.setRowCount(rows);grid.setUseDefaultMargins(false);
  for(int i=0;i<labels.length;i++){final int item=i;Button hit=new Button(c);hit.setText(labels[i]);hit.setAllCaps(false);hit.setTypeface(theme.font(true));hit.setTextSize(14);hit.setTextColor(theme.text);hit.setBackgroundColor(Color.TRANSPARENT);hit.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);hit.setPadding(theme.dp(4),0,theme.dp(4),theme.dp(12));hit.setMinHeight(theme.dp(96));hit.setMinimumWidth(theme.dp(64));hit.setContentDescription(labels[i]+", open activity");hit.setOnTouchListener((v,e)->{if(e.getActionMasked()==android.view.MotionEvent.ACTION_DOWN&&surface!=null)surface.react(cx(item),cy(item));return false;});hit.setOnClickListener(v->{v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);listener.chosen(item);});GridLayout.LayoutParams p=new GridLayout.LayoutParams(GridLayout.spec(i/3,1f),GridLayout.spec(i%3,1f));p.width=0;p.height=0;p.setGravity(Gravity.FILL);grid.addView(hit,p);}
  addView(grid,new FrameLayout.LayoutParams(-1,-1));
 }
 private static float cx(int i){return 112+i%3*208;}private static float cy(int i){return 112+i/3*208;}
 private SceneFrame scene(){ArrayList<SceneFrame.Node>n=new ArrayList<>();for(int i=0;i<labels.length;i++){float x=cx(i),y=cy(i);n.add(SceneFrame.panel(x-96,y-96,x+96,y+96,theme.dark?new int[]{0x594b32,0x28584e,0x344d6b}[i%3]:COLORS[i]));float yy=y-15;int type=icons[i];if(type==100){n.add(new SceneFrame.Node(SceneFrame.CHARACTER,x,yy,47,47,48,0x75c6ac,0,true));n.add(SceneFrame.model(x-48,yy+24,23,StoryModels.FLOWER,0xf3c578,true));}
   else if(type==101){n.add(new SceneFrame.Node(6,x,yy+3,50,4,32,0xd4ad75,0,false));n.add(new SceneFrame.Node(6,x,yy+22,3,24,30,0xd4ad75,0,false));n.add(SceneFrame.shape(x-37,yy-20,15,0,0x86bda6));n.add(SceneFrame.shape(x+37,yy-20,15,0,0xe3b375));}
   else if(type==102){for(int p=0;p<3;p++)n.add(SceneFrame.panel(x-60+p*37,yy+24,x-30+p*37,yy+53,0x8cb4bc));n.add(new SceneFrame.Node(SceneFrame.CHARACTER,x,yy-5,42,42,40,0x8bb7d9,1,true));}
   else if(type==103){n.add(SceneFrame.model(x,yy,48,StoryModels.TELESCOPE,0x87adc5,true));n.add(SceneFrame.model(x+38,yy-35,20,StoryModels.STAR,0xf0cb73,true));}
   else if(type==104){n.add(SceneFrame.model(x-8,yy,52,StoryModels.BOOK,0xa9b7d7,true));n.add(SceneFrame.model(x+38,yy-5,32,StoryModels.BRUSH,0x88b69a,true));}
   else if(type==105){for(int p=0;p<3;p++){n.add(SceneFrame.shape(x-44+p*44,yy+20+(p%2)*15,26,0,COLORS[p]));n.add(SceneFrame.model(x-44+p*44,yy-6+(p%2)*15,20,StoryModels.TREE,0x80ae89,true));}}
   else if(type==106){n.add(new SceneFrame.Node(SceneFrame.CHARACTER,x-28,yy,40,40,40,0x7ac4ac,0,true));n.add(new SceneFrame.Node(SceneFrame.CHARACTER,x+30,yy,40,40,42,0xe3aa94,2,true));}
   else if(type==107){n.add(new SceneFrame.Node(9,x-32,yy,31,31,40,0xe7be77,0,true));n.add(new SceneFrame.Node(6,x+24,yy+8,23,23,40,0x8db6d3,4500,true));n.add(new SceneFrame.Node(10,x+32,yy-30,22,22,45,0xbc9fc5,0,true));}
   else if(type==108){n.add(SceneFrame.model(x-8,yy,48,StoryModels.HOUSE,0xe2b68d,true));n.add(SceneFrame.model(x+42,yy-4,30,StoryModels.TREE,0x7bad8a,true));}
   else n.add(SceneFrame.model(x,yy,52,type,COLORS[i],true));}
  return new SceneFrame(n,theme.background,rows*208+16,"welcome",false);
 }
 private void fallback(){fallback=new ToyFallback(getContext());addView(fallback,0,new FrameLayout.LayoutParams(-1,-1));}
 void pause(){paused=true;if(surface!=null)surface.pauseStage();if(fallback!=null)fallback.invalidate();}void resume(){paused=false;if(surface!=null)surface.resumeStage();if(fallback!=null)fallback.invalidate();}
 boolean reducedMotion(){return reduced;}
 NativeStageSurface surface(){return surface;}
 @Override protected void onMeasure(int w,int h){int width=MeasureSpec.getSize(w);super.onMeasure(w,MeasureSpec.makeMeasureSpec(width*rows/3,MeasureSpec.EXACTLY));}
 private final class ToyFallback extends View{final Paint p=new Paint(3);ToyFallback(Context c){super(c);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}protected void onDraw(Canvas c){c.save();c.scale(getWidth()/640f,getHeight()/(rows*208f+16));boolean reduced=getContext().getSharedPreferences("family",0).getBoolean("reduce_motion",false)||android.provider.Settings.Global.getFloat(getContext().getContentResolver(),android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,1)==0;float t=reduced?0:android.os.SystemClock.uptimeMillis()/650f;for(int i=0;i<labels.length;i++){float x=cx(i),y=cy(i);p.setColor(theme.dark?theme.soft:COLORS[i]);c.drawRoundRect(x-94,y-94,x+94,y+94,28,28,p);float yy=y-15+(float)Math.sin(t+i)*4;Artwork.buddy(c,x,yy,80,COLORS[(i+2)%9],"",p);p.setShader(new RadialGradient(x-12,yy-18,36,new int[]{Color.WHITE,COLORS[i]},null,Shader.TileMode.CLAMP));c.drawCircle(x-48,yy+18,17,p);p.setShader(null);}c.restore();if(!paused&&!reduced&&isShown()&&hasWindowFocus())postInvalidateDelayed(33);}}
}
