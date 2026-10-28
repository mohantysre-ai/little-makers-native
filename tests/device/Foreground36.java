package com.littlemakers.village.rendercheck;
import android.app.Instrumentation;import android.os.SystemClock;import android.view.accessibility.AccessibilityNodeInfo;
/** Reject screenshots/assertions hidden by OS dialogs or another application. */
final class Foreground36 {
 static void require(Instrumentation test,android.app.Activity activity){String expected=test.getTargetContext().getPackageName(),observed="none";long end=SystemClock.uptimeMillis()+15000;while(SystemClock.uptimeMillis()<end){AccessibilityNodeInfo root=test.getUiAutomation().getRootInActiveWindow();if(root!=null){observed=String.valueOf(root.getPackageName());root.recycle();if(expected.equals(observed)&&activity.hasWindowFocus())return;}SystemClock.sleep(100);}throw new AssertionError("App is obscured; active window package="+observed);}
}
