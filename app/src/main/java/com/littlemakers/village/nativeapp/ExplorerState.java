package com.littlemakers.village.nativeapp;
/** Bounded, deterministic progress for original scripted reading and friendship activities. */
final class ExplorerState {
 final int mode,level;int phase,first=-1,second=-1;String word="";boolean traced,won;
 ExplorerState(int mode,int level){if(mode<0||mode>5||level<0||level>=ExplorerCatalog.count(mode))throw new IllegalArgumentException();this.mode=mode;this.level=level;}
 boolean answer(int option){if(won||option<0||option>2)return false;if(mode==0){if(option!=ExplorerCatalog.LESSONS[level].answer)return false;won=true;return true;}if(mode==1&&level<26){if(!traced||option!=level%3)return false;won=true;return true;}if(mode==3&&option<2){if(phase==0){first=option;phase=1;return false;}second=option;phase=2;won=true;return true;}return false;}
 String[] letterChoices(){String[]v=new String[3];v[level%3]=ExplorerCatalog.WORDS[level];v[(level+1)%3]=ExplorerCatalog.WORDS[(level+7)%26];v[(level+2)%3]=ExplorerCatalog.WORDS[(level+15)%26];return v;}
 String targetWord(){return level>=26?ExplorerCatalog.BUILD_WORDS[level-26]:"";}
 boolean addLetter(char c){if(mode!=1||level<26||won||c<'A'||c>'Z'||word.length()>=targetWord().length())return false;word+=c;return true;}
 void undo(){if(!won&&word.length()>0)word=word.substring(0,word.length()-1);}
 boolean checkWord(){if(won||!word.equals(targetWord())||targetWord().isEmpty())return false;won=true;return true;}
 String save(){return phase+","+first+","+second+","+(traced?1:0)+","+(won?1:0)+","+word;}
 boolean restore(String saved){try{String[]p=saved.split(",",-1);if(p.length!=6)return false;int ph=Integer.parseInt(p[0]),a=Integer.parseInt(p[1]),b=Integer.parseInt(p[2]),t=Integer.parseInt(p[3]),w=Integer.parseInt(p[4]);if(ph<0||ph>2||a< -1||a>1||b< -1||b>1||t<0||t>1||w<0||w>1||!p[5].matches("[A-Z]{0,3}"))return false;if(mode==3&&((ph==0&&(a!=-1||b!=-1))||(ph==1&&(a<0||b!=-1))||(ph==2&&(a<0||b<0))||(w==1)!=(ph==2)))return false;if(mode==1&&level>=26&&p[5].length()>targetWord().length())return false;phase=ph;first=a;second=b;traced=t==1;won=w==1;word=p[5];return true;}catch(RuntimeException e){return false;}}
 String rewardKey(){return ExplorerCatalog.prefix(mode)+"-"+level;}
}
