package com.littlemakers.village.nativeapp;
import java.util.*;
/** Original offline puzzle rules. Stable stages and bounded local adaptation; no timers. */
final class LearningQuest {
 static final int STAGES=24; static final String[] NAMES={"Number Guardians","Balance Workshop","Robot Routes"};
 final int mode,stage; int tier,streak,mistakes,weight,x,y,heading; boolean won; final StringBuilder commands=new StringBuilder();
 LearningQuest(int mode,int stage,int tier){if(mode<0||mode>2||stage<0||stage>=STAGES)throw new IllegalArgumentException();this.mode=mode;this.stage=stage;this.tier=Math.max(0,Math.min(2,tier));}
 int a(){return tier==0?1+stage%5:2+stage%7;}
 int b(){return tier==0?0:1+(stage*3)%Math.max(2,tier==1?4:a());}
 boolean subtract(){return tier==2&&stage%2==1;}
 int answer(){return subtract()?a()-b():a()+b();}
 int[] choices(){int n=answer();int[]v={n,n+1,Math.max(0,n-1)};if(v[2]==v[0])v[2]=n+2;int shift=stage%3;return new int[]{v[shift],v[(shift+1)%3],v[(shift+2)%3]};}
 String question(){return tier==0?"How many glowing seeds?":a()+(subtract()?" − ":" + ")+b()+" = ?";}
 boolean choose(int value){if(won)return false;if(value!=answer()){streak=0;if(++mistakes>=2&&tier>0){tier--;mistakes=0;}return false;}won=true;mistakes=0;if(++streak>=2){tier=Math.min(2,tier+1);streak=0;}return true;}
 int known(){return 1+stage%4;} int missing(){return 1+(stage*5)%6;} int total(){return known()+missing();}
 void adjust(int delta){if(!won)weight=Math.max(0,Math.min(12,weight+Math.max(-1,Math.min(1,delta))));}
 boolean balance(){if(won)return false;if(weight==missing()){won=true;return true;}mistakes++;return false;}
 boolean blocked(int xx,int yy){if(xx<0||xx>3||yy<0||yy>3)return true;if(yy==0||xx==3||(xx==0&&yy==0))return false;return ((stage*73+19)>>(yy*3+xx)%10&1)!=0;}
 boolean append(char c){if(won||commands.length()>=32||"FLR".indexOf(c)<0)return false;commands.append(c);return true;}
 void undo(){if(!won&&commands.length()>0)commands.deleteCharAt(commands.length()-1);}
 void rewind(){x=y=heading=0;}
 // Heading 0=east, 1=south, 2=west, 3=north. Returns false for a blocked step.
 boolean step(char c){if(won)return false;if(c=='L'){heading=(heading+3)%4;return true;}if(c=='R'){heading=(heading+1)%4;return true;}if(c!='F')return false;int xx=x+new int[]{1,0,-1,0}[heading],yy=y+new int[]{0,1,0,-1}[heading];if(blocked(xx,yy))return false;x=xx;y=yy;return true;}
 boolean finishRoute(){if(x==3&&y==3){won=true;return true;}return false;}
 String rewardKey(){return new String[]{"guardian","balance","robot"}[mode]+"-"+stage;}
}
