package com.littlemakers.village.nativeapp;
import android.media.*;
import android.content.Context;
import java.util.*;

/** Original synthesized PCM; no downloaded music, SDK, network voice or ad service. */
public final class SoundEngine {
 private final Context context;private final Set<AudioTrack> playing=new HashSet<>();
 SoundEngine(Context context){this.context=context;}
 public void setEnabled(boolean enabled){context.getSharedPreferences("family",0).edit().putBoolean("sound",enabled).apply();if(!enabled)stop();}
 public boolean enabled(){return context.getSharedPreferences("family",0).getBoolean("sound",true);}
 public void tap(){play(new double[]{440},.09);}
 public void note(int index){play(new double[]{262,330,392}[Math.floorMod(index,3)]);}
 private void play(double frequency){play(new double[]{frequency},.28);}
 public void win(){stop();play(new double[]{523.25,659.25,783.99,1046.50},.19);}
 public void tune(int[] notes){double[] freq=new double[notes.length];for(int i=0;i<notes.length;i++)freq[i]=new double[]{262,330,392}[notes[i]%3];play(freq,.32);}
 private void play(double[]frequencies,double duration){
  if(!enabled())return;synchronized(playing){if(playing.size()>=4)stop();}try {
   short[] pcm=AudioSynth.samples(frequencies,duration);
   final AudioTrack track=new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).setAudioFormat(new AudioFormat.Builder().setSampleRate(22050).setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build()).setBufferSizeInBytes(pcm.length*2).setTransferMode(AudioTrack.MODE_STATIC).build();
   synchronized(playing){playing.add(track);}track.write(pcm,0,pcm.length);track.setNotificationMarkerPosition(pcm.length);track.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener(){public void onMarkerReached(AudioTrack t){release(t);}public void onPeriodicNotification(AudioTrack t){}});track.play();
  }catch(RuntimeException unsupportedAudio){stop();}
 }
 private void release(AudioTrack track){synchronized(playing){if(playing.remove(track)){try{track.stop();}catch(IllegalStateException ignored){}track.release();}}}
 public void stop(){synchronized(playing){for(AudioTrack t:new ArrayList<>(playing))release(t);}}
}
