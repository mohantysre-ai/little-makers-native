package com.littlemakers.village.nativeapp;
/** Small deterministic branching film. Completion requires watching and answering, not random tapping. */
final class CinematicState {
 static final class Scene {
  final String clip,title,caption,question;final String[]choices;final int[]next;final int correct;
  Scene(String clip,String title,String caption,String question,String[]choices,int[]next,int correct){this.clip=clip;this.title=title;this.caption=caption;this.question=question;this.choices=choices.clone();this.next=next.clone();this.correct=correct;}
 }
 static final Scene[]SCENES={
  new Scene("moon_arrival","Your Moon expedition","You are the mission guide. Fly above the Moon and choose what to explore.","Where shall we look next?",new String[]{"Explore round craters","Explore light and shadows"},new int[]{1,2},-1),
  new Scene("moon_crater","A bowl in the ground","These big hollows are craters. Look for the curved rim around the hollow.","What shape is the crater rim?",new String[]{"Roughly round","A pointed triangle"},new int[]{3,3},0),
  new Scene("moon_sunlight","The bright side","Sunlight makes these rocky slopes bright. The other slopes are in shadow.","What lights the Moon?",new String[]{"The Sun","The Moon makes its own light"},new int[]{3,3},0),
  new Scene("moon_departure","Your discovery is saved","The Moon has craters, rocky slopes and shadows. It reflects sunlight. You guided an expedition!","",new String[0],new int[0],-1)
 };
 int node;boolean watched,solved,finished;int positionMs;
 Scene scene(){return SCENES[node];}
 void completeClip(){watched=true;positionMs=0;if(node==3&&solved)finished=true;}
 boolean choose(int choice){Scene s=scene();if(!watched||finished||choice<0||choice>=s.next.length)return false;if(s.correct>=0&&choice!=s.correct)return false;if(s.correct>=0)solved=true;node=s.next[choice];watched=false;positionMs=0;return true;}
 boolean canReward(){return finished&&node==3&&watched&&solved;}
 String save(){return "1,"+node+","+(watched?1:0)+","+(solved?1:0)+","+(finished?1:0)+","+Math.max(0,Math.min(8000,positionMs));}
 static CinematicState restore(String encoded){CinematicState s=new CinematicState();if(encoded==null||encoded.isEmpty())return s;try{String[]p=encoded.split(",",-1);if(p.length!=6||!p[0].equals("1"))return s;int node=Integer.parseInt(p[1]),watch=Integer.parseInt(p[2]),solved=Integer.parseInt(p[3]),done=Integer.parseInt(p[4]),pos=Integer.parseInt(p[5]);if(node<0||node>=SCENES.length||watch<0||watch>1||solved<0||solved>1||done<0||done>1||pos<0||pos>8000)return s;if((node<3&&solved!=0)||(node==3&&solved!=1)||(done==1&&(node!=3||watch!=1)))return s;s.node=node;s.watched=watch==1;s.solved=solved==1;s.finished=done==1;s.positionMs=s.watched?0:pos;return s;}catch(NumberFormatException invalid){return s;}}
}
