package com.littlemakers.village.rendercheck;
import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.view.*;import android.widget.*;import java.io.*;import java.lang.reflect.*;
/** Real island viewport, live toy controls, navigation and queued movement checks. */
public final class Motion33Check extends Instrumentation {
 Activity a;int captures;File out;
 Object field(Object o,String n)throws Exception{Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);return f.get(o);}
 void ui(Runnable r){java.util.concurrent.atomic.AtomicReference<Throwable>failure=new java.util.concurrent.atomic.AtomicReference<>();runOnMainSync(()->{try{r.run();}catch(Throwable e){failure.set(e);}});waitForIdleSync();if(failure.get()!=null)throw new AssertionError("UI operation failed",failure.get());}
 interface Check{boolean ok()throws Exception;}
 void waitFor(Check c,String why)throws Exception{long end=SystemClock.uptimeMillis()+30000;while(SystemClock.uptimeMillis()<end){if(c.ok())return;SystemClock.sleep(80);}throw new AssertionError(why);}
 void shot(String name)throws Exception{SystemClock.sleep(250);Bitmap b=getUiAutomation().takeScreenshot();if(b==null)throw new AssertionError("screenshot");try(FileOutputStream f=new FileOutputStream(new File(out,name+".png"))){b.compress(Bitmap.CompressFormat.PNG,100,f);}b.recycle();captures++;}
 void invoke(Object o,String n)throws Exception{Method m=o.getClass().getDeclaredMethod(n);m.setAccessible(true);ui(()->{try{m.invoke(o);}catch(Exception e){throw new RuntimeException(e);}});}
 @Override public void onCreate(Bundle b){super.onCreate(b);start();}
 @Override public void onStart(){Bundle result=new Bundle();try{
  out=new File(getTargetContext().getFilesDir(),"motion33-check");out.mkdirs();getTargetContext().getSharedPreferences("adventure",0).edit().clear().commit();getTargetContext().getSharedPreferences("family",0).edit().putString("theme","light").putBoolean("reduce_motion",false).commit();
  a=startActivitySync(new Intent().setClassName("com.littlemakers.village","com.littlemakers.village.nativeapp.AdventureActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));waitForIdleSync();
  waitFor(()->field(a,"island")!=null&&(Long)field(field(a,"island"),"renderedFrames")>2,"island never rendered");View island=(View)field(a,"island");if(island.getHeight()<getTargetContext().getResources().getDisplayMetrics().heightPixels*.35f)throw new AssertionError("island is squeezed off screen");
  ViewGroup dock=(ViewGroup)field(a,"dock");if(dock.getChildCount()!=6)throw new AssertionError("six toys missing");for(int i=0;i<6;i++){View toy=dock.getChildAt(i);if(toy instanceof Button||!toy.isClickable()||toy.getContentDescription()==null||toy.getWidth()<64*getTargetContext().getResources().getDisplayMetrics().density)throw new AssertionError("toy accessibility or size");}
  long frames=(Long)field(island,"renderedFrames");SystemClock.sleep(400);if((Long)field(island,"renderedFrames")<=frames)throw new AssertionError("world motion stalled");shot("immersive-island");
  Object state=field(a,"expedition");float oldX=(Float)field(state,"x"),oldZ=(Float)field(state,"z");invoke(island,"clue");waitFor(()->Math.abs((Float)field(state,"x")-oldX)+Math.abs((Float)field(state,"z")-oldZ)>.1f,"queued clue did not walk");shot("walking-island");
  Object sound=field(a,"sound");Method tap=sound.getClass().getMethod("tap"),stop=sound.getClass().getMethod("stop");long before=SystemClock.uptimeMillis();ui(()->{try{for(int i=0;i<20;i++)tap.invoke(sound);stop.invoke(sound);}catch(Exception e){throw new RuntimeException(e);}});long audioUi=SystemClock.uptimeMillis()-before;
  if(audioUi>2000)throw new AssertionError("audio burst blocks UI: "+audioUi);SystemClock.sleep(200);
  ActivityMonitor monitor=addMonitor("com.littlemakers.village.nativeapp.SchoolTrailActivity",null,false);ui(()->dock.getChildAt(5).performClick());Activity quests=waitForMonitorWithTimeout(monitor,20000);removeMonitor(monitor);if(quests==null)throw new AssertionError("Learn K–10 toy routing");ui(quests::finish);SystemClock.sleep(700);waitForIdleSync();
  getTargetContext().getSharedPreferences("family",0).edit().putString("theme","dark").putBoolean("reduce_motion",true).commit();ui(a::recreate);SystemClock.sleep(1500);
  // Activity recreation is picked up by the monitor below on the next fresh launch.
  a=startActivitySync(new Intent().setClassName("com.littlemakers.village","com.littlemakers.village.nativeapp.AdventureActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));waitForIdleSync();waitFor(()->field(a,"island")!=null&&(Long)field(field(a,"island"),"renderedFrames")>0,"dark island");if(((TextView)field(a,"objective")).getCurrentTextColor()!=Color.WHITE)throw new AssertionError("dark text");Object still=field(a,"island");long idle=(Long)field(still,"renderedFrames");SystemClock.sleep(400);if((Long)field(still,"renderedFrames")>idle+1)throw new AssertionError("reduced world spins while idle");shot("island-dark-reduced");ui(a::finish);
  result.putString("stream","PASS immersive visible world, six accessible animated toy targets, live frames, queued walking, learning-trail routing and dark reduced-motion layout; "+captures+" captures. Audio burst UI elapsed "+audioUi+"ms. No physical-device FPS certification.\n");finish(Activity.RESULT_OK,result);
 }catch(Throwable e){result.putString("stream","FAIL "+android.util.Log.getStackTraceString(e));finish(Activity.RESULT_CANCELED,result);}}
}
