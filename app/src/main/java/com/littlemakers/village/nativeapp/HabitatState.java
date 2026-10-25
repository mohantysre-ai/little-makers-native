package com.littlemakers.village.nativeapp;
/** Nine persistent build slots. Starter decorations are free; extras unlock at visible star totals. */
final class HabitatState {
 static final String[] NAMES={"Water plant","Smooth rock","Garden arch","Shell shelter","Soft coral","Treasure nook"};
 static final int[] STARS={0,0,0,2,4,6};
 final int[]slots={-1,-1,-1,-1,-1,-1,-1,-1,-1};int selected;
 synchronized boolean place(int type,int stars){if(type<0||type>=NAMES.length||selected<0||selected>=9||stars<STARS[type])return false;slots[selected]=type;return true;}
 synchronized void select(int slot){if(slot>=0&&slot<9)selected=slot;}
 synchronized void clear(){slots[selected]=-1;}
 synchronized void reset(){java.util.Arrays.fill(slots,-1);selected=0;}
 synchronized int count(){int n=0;for(int s:slots)if(s>=0)n++;return n;}
 synchronized String save(){StringBuilder b=new StringBuilder();for(int s:slots)b.append(s).append(',');return b.toString();}
 synchronized void restore(String value){String[]parts=value.split(",");if(parts.length!=9)return;int[]copy=new int[9];try{for(int i=0;i<9;i++){copy[i]=Integer.parseInt(parts[i]);if(copy[i]<-1||copy[i]>=NAMES.length)return;}System.arraycopy(copy,0,slots,0,9);}catch(NumberFormatException ignored){}}
}
