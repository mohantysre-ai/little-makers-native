package com.littlemakers.village.nativeapp;

/** Child-controlled progression. Camera time never completes a learning action. */
final class StoryTheaterState {
 final int steps,choices;int step,ending=-1;boolean begun,placed;
 StoryTheaterState(int steps,int choices){if(steps<1||steps>64||choices<1||choices>16)throw new IllegalArgumentException("Invalid story structure");this.steps=steps;this.choices=choices;}
 void restore(int step,int ending,boolean begun,boolean placed){
  this.step=Math.max(0,Math.min(steps,step));this.begun=begun;this.placed=this.step<steps&&placed;
  this.ending=this.step==steps&&ending>=0&&ending<choices?ending:-1;
  if(this.step>0||this.placed||this.ending>=0)this.begun=true;
 }
 boolean help(){if(!begun||step>=steps||placed)return false;placed=true;return true;}
 boolean next(){if(!placed||step>=steps)return false;step++;placed=false;return true;}
 boolean choose(int answer){if(!begun||step!=steps||answer<0||answer>=choices)return false;ending=answer;return true;}
 boolean finished(){return step==steps&&ending>=0;}
 void restart(){step=0;ending=-1;begun=false;placed=false;}
}
