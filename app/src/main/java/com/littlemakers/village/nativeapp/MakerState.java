package com.littlemakers.village.nativeapp;
import java.util.*;
/** Six private world slots; each world has 24 editable, bounded cells. */
final class MakerState {
 static final String[] ITEMS={"Tree","House","Pond","Flowers","Bridge","Friend"};
 static final int[] MODELS={StoryModels.TREE,StoryModels.HOUSE,StoryModels.FISH,StoryModels.FLOWER,StoryModels.PLANK,StoryModels.CHILD};
 static final int[][] GOALS={{2,0,0,1,0,0},{1,1,0,0,0,0},{0,0,1,2,0,0},{0,0,1,0,1,0},{0,1,0,0,0,1},{2,0,0,2,0,0},{1,1,0,0,0,0},{0,0,1,0,1,0},{0,1,0,2,0,1},{3,1,1,0,0,0},{0,2,0,0,0,2},{2,1,1,2,1,1}};
 static final String[] CHALLENGES={"Plant two trees and a flower patch","Give a house a tree","Make a pond with two flower patches","Add a pond and a bridge","Give a friend a house","Grow two trees and two flower patches","Place a tree beside a house","Place a bridge beside a pond","Make a flower garden for a house and a friend","Build a woodland village with three trees, a house and a pond","Make two houses and invite two friends","Make a little world: two trees, a house, a pond, two flowers, a bridge and a friend"};
 final int[] cells=new int[24],turns=new int[24];int selected,brush;
 MakerState(){Arrays.fill(cells,-1);}
 boolean select(int slot){if(slot<0||slot>=24)return false;selected=slot;return true;}
 boolean place(int item){if(item<0||item>=ITEMS.length)return false;cells[selected]=item;turns[selected]=0;brush=item;return true;}
 void clear(){cells[selected]=-1;turns[selected]=0;}void rotate(){if(cells[selected]>=0)turns[selected]=(turns[selected]+1)%4;}
 int count(int item){int n=0;for(int c:cells)if(c==item)n++;return n;}
 boolean adjacent(int a,int b){for(int i=0;i<24;i++)if(cells[i]==a){if(i%6<5&&cells[i+1]==b||i%6>0&&cells[i-1]==b||i<18&&cells[i+6]==b||i>=6&&cells[i-6]==b)return true;}return false;}
 boolean meets(int mission){if(mission<0||mission>=GOALS.length)return false;for(int i=0;i<ITEMS.length;i++)if(count(i)<GOALS[mission][i])return false;return mission==6?adjacent(0,1):mission!=7||adjacent(4,2);}
 String save(){StringBuilder b=new StringBuilder();for(int i=0;i<24;i++){if(i>0)b.append(',');b.append(cells[i]).append(':').append(turns[i]);}return b.toString();}
 boolean restore(String value){try{String[]p=value.split(",",-1);if(p.length!=24)return false;int[]c=new int[24],r=new int[24];for(int i=0;i<24;i++){String[]v=p[i].split(":",-1);if(v.length!=2)return false;c[i]=Integer.parseInt(v[0]);r[i]=Integer.parseInt(v[1]);if(c[i]< -1||c[i]>=6||r[i]<0||r[i]>3)return false;}System.arraycopy(c,0,cells,0,24);System.arraycopy(r,0,turns,0,24);return true;}catch(RuntimeException e){return false;}}
}
