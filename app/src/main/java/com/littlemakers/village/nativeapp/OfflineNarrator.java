package com.littlemakers.village.nativeapp;
import android.content.Context;import android.os.*;import android.speech.tts.*;import java.util.*;
/** Optional system speech, restricted to an installed voice which does not require a network. */
final class OfflineNarrator {
 private TextToSpeech engine;private final Context context;private final Runnable unavailable;private boolean ready,closed;private String pending;
 OfflineNarrator(Context c,Runnable unavailable){context=c;this.unavailable=unavailable;}
 void speak(String text){if(closed)return;pending=text;if(engine==null){engine=new TextToSpeech(context,status->new Handler(Looper.getMainLooper()).post(()->{if(closed)return;if(status!=TextToSpeech.SUCCESS){unavailable.run();return;}Set<Voice>voices=engine.getVoices();if(voices!=null)for(Voice v:voices)if(!v.isNetworkConnectionRequired()&&v.getLocale().getLanguage().equals("en")){ready=engine.setVoice(v)==TextToSpeech.SUCCESS;break;}if(ready&&pending!=null)engine.speak(pending,TextToSpeech.QUEUE_FLUSH,null,"makers-script");else unavailable.run();}));}else if(ready)engine.speak(text,TextToSpeech.QUEUE_FLUSH,null,"makers-script");else unavailable.run();}
 void stop(){pending=null;if(engine!=null)engine.stop();}void close(){closed=true;pending=null;if(engine!=null){engine.shutdown();engine=null;}}
}
