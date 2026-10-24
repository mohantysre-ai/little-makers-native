package com.littlemakers.village.nativeapp;
/** Original bounded, enveloped mono PCM synthesis. */
public final class AudioSynth {
 public static short[] samples(double[] frequencies,double duration){int rate=22050,length=(int)(rate*duration);short[]data=new short[length*frequencies.length];for(int n=0;n<frequencies.length;n++)for(int i=0;i<length;i++){double t=i/(double)rate,envelope=Math.min(1,t/.02)*Math.min(1,(duration-t)/.055);data[n*length+i]=(short)(Math.sin(2*Math.PI*frequencies[n]*t)*envelope*4500);}return data;}
}
