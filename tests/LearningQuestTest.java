package com.littlemakers.village.nativeapp;
public final class LearningQuestTest {
 static int checks;static void check(boolean b){checks++;if(!b)throw new AssertionError("check "+checks);}
 public static void main(String[]args){for(int level=0;level<24;level++)for(int tier=0;tier<3;tier++){
  LearningQuest q=new LearningQuest(0,level,tier);int correct=q.answer();int hits=0;for(int n:q.choices())if(n==correct)hits++;check(hits==1);check(correct>=0&&correct<=20);check(!q.choose(-1));check(!q.won);check(!q.choose(-1));check(q.tier==Math.max(0,tier-1));check(q.choose(q.answer()));check(!q.choose(q.answer()));check(q.rewardKey().equals("guardian-"+level));
  q=new LearningQuest(1,level,tier);check(!q.balance());for(int n=0;n<q.missing();n++)q.adjust(1);check(q.balance());check(q.known()+q.weight==q.total());check(!q.balance());
  q=new LearningQuest(2,level,tier);check(!q.step('L')||q.heading==3);check(!q.step('F'));q.rewind();for(char c:"FFFRFFF".toCharArray())check(q.step(c));check(q.finishRoute());check(!q.step('F'));check(q.rewardKey().equals("robot-"+level));
 }
 LearningQuest q=new LearningQuest(0,0,0);q.streak=1;check(q.choose(q.answer()));check(q.tier==1);q=new LearningQuest(2,0,0);for(int i=0;i<32;i++)check(q.append('F'));check(!q.append('R'));q.undo();check(q.append('R'));check(!q.append('Z'));q=new LearningQuest(1,0,0);for(int i=0;i<40;i++)q.adjust(-1);check(q.weight==0);for(int i=0;i<40;i++)q.adjust(1);check(q.weight==12);
 System.out.println("PASS "+checks+" original quest checks: all 72 stages, adaptive tiers, unique choices, balance solutions, safe robot routes, bounds and replay guards");}
}
