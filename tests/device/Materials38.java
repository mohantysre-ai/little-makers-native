package com.littlemakers.village.rendercheck;
import android.view.*;import android.opengl.*;import java.lang.reflect.*;import java.util.concurrent.*;import java.util.concurrent.atomic.*;
/** Runtime texture capability check. A shader fallback is useful, but is not a material acceptance pass. */
final class Materials38 {
 static void require(Object surface)throws Exception{Field f=surface.getClass().getDeclaredField("materials");f.setAccessible(true);Object material=f.get(surface);if(material==null)throw new AssertionError("Material object missing");Method available=material.getClass().getDeclaredMethod("available");available.setAccessible(true);if(!(Boolean)available.invoke(material))throw new AssertionError("Material atlas not uploaded: "+surface.getClass().getSimpleName());AtomicInteger error=new AtomicInteger(-1);CountDownLatch latch=new CountDownLatch(1);((GLSurfaceView)surface).queueEvent(()->{error.set(GLES20.glGetError());latch.countDown();});if(!latch.await(30,TimeUnit.SECONDS)||error.get()!=0)throw new AssertionError("GL error "+error.get());}
 static void visit(View view)throws Exception{if(view instanceof GLSurfaceView)require(view);if(view instanceof ViewGroup)for(int i=0;i<((ViewGroup)view).getChildCount();i++)visit(((ViewGroup)view).getChildAt(i));}
}
