package com.littlemakers.village.nativeapp;
import java.util.*;
/** Fifty added, deterministic challenges per mechanic. No network content or borrowed assets. */
final class ContentPack {
 static final String[] WORDS={"ANT","APE","BAT","BEE","BUG","COW","FOX","HEN","OWL","PIG","RAT","RAM","SEAL","DUCK","FROG","GOAT","LAMB","LION","MOTH","BEAR","WOLF","CRAB","DEER","HARE","MULE","PONY","SNAIL","SNAKE","WHALE","ZEBRA","APPLE","BERRY","PEAR","PLUM","LEMON","GRAPE","PEACH","MANGO","BREAD","MILK","EGG","WATER","RAIN","SNOW","WIND","CLOUD","LEAF","ROCK","SAND","SEED"};
 private static final int[][] WATER_BOARDS=waterBoards(), FACTORS=factors();
 static final int[] PERIMETER={0,1,2,3,7,11,15,14,13,12,8,4};
 static int[] digits(int number,int length){int[]v=new int[length];for(int i=0;i<length;i++){v[i]=number%3;number/=3;}return v;}
 static int[] permutation(int[]source,int rank){int[]a=source.clone();Arrays.sort(a);for(int i=0;i<rank;i++){int j=a.length-2;while(j>=0&&a[j]>=a[j+1])j--;if(j<0)throw new IllegalArgumentException("Permutation rank");int k=a.length-1;while(a[k]<=a[j])k--;int t=a[j];a[j]=a[k];a[k]=t;for(int l=j+1,h=a.length-1;l<h;l++,h--){t=a[l];a[l]=a[h];a[h]=t;}}return a;}
 static void expand(GameRules s){int k=s.game.round-10;
  switch(s.game.kind){
   case COUNT:s.answer=6+k%11;s.total=s.answer;s.values=digits(k+25,s.total);break;
   case ADD:s.a=1+k%8;s.b=8-k%8+k/8;s.answer=s.a+s.b;s.total=s.answer;s.values=digits(k+25,s.total);break;
   case SUBTRACT:s.a=9+k/8;s.b=1+k%8;s.answer=s.a-s.b;s.total=s.answer;s.values=digits(k+25,s.total);break;
   case MULTIPLY:s.a=FACTORS[k][0];s.b=FACTORS[k][1];s.answer=s.a*s.b;s.total=s.answer;s.values=digits(k+25,s.total);break;
   case DIVIDE:s.b=FACTORS[k][0];s.answer=FACTORS[k][1];s.a=s.b*s.answer;s.total=s.a;s.values=digits(k+25,s.total);break;
   case SHAPES:case COLORS:s.sequence=digits(k+20,6);s.answer=s.sequence[0];break;
   case PATTERN:int[]rule=digits(k+15,4);s.sequence=new int[7];for(int i=0;i<7;i++)s.sequence[i]=rule[i%4];s.answer=rule[3];break;
   case MEMORY:s.deck=permutation(new int[]{0,0,1,1,2,2,3,3},(k*47+311)%2520);break;
   case SIZES:s.values=permutation(new int[]{0,1,2,3,4},(k*7+19)%120);break;
   case TRACE:char glyph=k<26?"KLMNOPQRSTUVWXYZ0123456789".charAt(k):(char)('A'+k-26);s.letter=String.valueOf(glyph);float[][]base=glyph>='A'&&glyph<='J'?GameRules.traces(glyph-'A'):glyph(glyph);s.trace=densify(base,k<26?2:3,k<26?1:.82f);break;
   case WORD:s.word=WORDS[k];s.values=permutationIndices(s.word.length(),k);break;
   case MAZE:s.position=PERIMETER[k%12];s.goal=PERIMETER[(k%12+6+k/12)%12];s.path.clear();s.path.add(s.position);break;
   case ROBOT:s.position=PERIMETER[k%12];s.goal=PERIMETER[(k%12+6+k/12)%12];s.obstacles=k/12==0?new int[0]:new int[]{new int[]{5,6,9,10}[k/12-1]};break;
   case WATER:s.tubes.clear();for(int i=0;i<5;i++)s.tubes.add(new ArrayList<Integer>());int[]p=WATER_BOARDS[k];for(int i=0;i<6;i++)s.tubes.get(i/2).add(p[i]);break;
   case BLOCKS:int count=0;outer:for(int shape=1;shape<=3;shape++)for(int rotation=0;rotation<(shape==1?2:shape==3?1:4);rotation++)for(int anchor=0;anchor<16;anchor++){int[]cells=GameRules.blockCells(anchor,rotation,shape);boolean valid=true;for(int cell:cells)valid&=cell<16&&cell/4>=anchor/4&&cell%4>=anchor%4;if(!valid)continue;if(count++==k){s.blockShape=shape;s.a=anchor;s.answer=rotation;s.rotation=(rotation+1)%s.rotationCount();s.target=cells;break outer;}}break;
   case MERGE:s.values=new int[16];int index=k%16;int start=index<8?(index/2)*4+index%2:((index-8)/2)+((index-8)%2)*4;int delta=index<8?1:4;s.target=new int[]{start,start+delta,start+delta*2};s.a=start;s.b=16<<(k/16);s.answer=s.b*4;s.values[start]=s.b;s.values[start+delta]=s.b;s.values[start+delta*2]=s.b*2;break;
   case RHYTHM:s.sequence=digits(k+40,6);break;
   case BRIDGE:s.required=4;s.a=380+(k/24)*30;s.sequence=permutation(new int[]{0,1,2,3},k%24);break;
   case PLAN:s.costs=new int[][]{{2+k%5,1},{1,2+k%5},{3,0}};s.a=s.costs[0][0]+s.costs[1][0]+k/5;s.b=s.costs[0][1]+s.costs[1][1];break;
  }
 }
 private static int[][]factors(){List<int[]>pairs=new ArrayList<>();for(int a=1;a<=16;a++)for(int b=1;a*b<=16;b++)pairs.add(new int[]{a,b});Collections.sort(pairs,(a,b)->{int c=Integer.compare(a[0]*a[1],b[0]*b[1]);return c==0?Integer.compare(a[0],b[0]):c;});return pairs.toArray(new int[0][]);}
 private static int[][]waterBoards(){List<int[]>boards=new ArrayList<>();for(int rank=0;rank<90;rank++){int[]p=permutation(new int[]{0,0,1,1,2,2},rank);if(p[0]!=p[1]||p[2]!=p[3]||p[4]!=p[5])boards.add(p);}return boards.toArray(new int[0][]);}
 private static int[] permutationIndices(int length,int seed){int[]a=new int[length];for(int i=0;i<length;i++)a[i]=i;Random r=new Random(3011+seed);for(int i=length-1;i>0;i--){int j=r.nextInt(i+1),v=a[i];a[i]=a[j];a[j]=v;}return a;}
 private static float[][]densify(float[][]base,int steps,float size){List<float[]>out=new ArrayList<>();out.add(new float[]{.5f+(base[0][0]-.5f)*size,.5f+(base[0][1]-.5f)*size});for(int i=1;i<base.length;i++)for(int j=1;j<=steps;j++){float f=j/(float)steps;out.add(new float[]{.5f+(base[i-1][0]+(base[i][0]-base[i-1][0])*f-.5f)*size,.5f+(base[i-1][1]+(base[i][1]-base[i-1][1])*f-.5f)*size});}return out.toArray(new float[0][]);}
 private static float[][]glyph(char c){String path;switch(c){
 case 'K':path=".2,.15 .2,.85 .2,.5 .8,.15 .2,.5 .8,.85";break;
 case 'L':path=".2,.15 .2,.85 .8,.85";break;
 case 'M':path=".15,.85 .15,.15 .5,.55 .85,.15 .85,.85";break;
 case 'N':path=".2,.85 .2,.15 .8,.85 .8,.15";break;
 case 'O':case '0':path=".5,.15 .25,.2 .15,.5 .25,.8 .5,.85 .75,.8 .85,.5 .75,.2 .5,.15";break;
 case 'P':path=".2,.85 .2,.15 .65,.15 .8,.3 .65,.5 .2,.5";break;
 case 'Q':path=".5,.15 .2,.25 .15,.6 .35,.85 .7,.8 .85,.5 .7,.2 .5,.15 .65,.65 .85,.85";break;
 case 'R':path=".2,.85 .2,.15 .65,.15 .8,.3 .65,.5 .2,.5 .8,.85";break;
 case 'S':case '5':path=".8,.15 .3,.15 .2,.4 .75,.55 .8,.75 .6,.85 .2,.85";break;
 case 'T':path=".15,.15 .85,.15 .5,.15 .5,.85";break;
 case 'U':path=".2,.15 .2,.65 .35,.85 .65,.85 .8,.65 .8,.15";break;
 case 'V':path=".15,.15 .5,.85 .85,.15";break;
 case 'W':path=".1,.15 .25,.85 .5,.45 .75,.85 .9,.15";break;
 case 'X':path=".2,.15 .8,.85 .5,.5 .8,.15 .2,.85";break;
 case 'Y':path=".2,.15 .5,.5 .8,.15 .5,.5 .5,.85";break;
 case 'Z':case '2':path=".2,.15 .8,.15 .2,.85 .8,.85";break;
 case '1':path=".3,.3 .5,.15 .5,.85 .3,.85 .7,.85";break;
 case '3':path=".2,.15 .65,.15 .8,.3 .5,.5 .8,.65 .65,.85 .2,.85";break;
 case '4':path=".65,.85 .65,.15 .2,.6 .85,.6";break;
 case '6':path=".8,.15 .35,.2 .2,.5 .2,.75 .5,.85 .8,.75 .8,.5 .5,.4 .2,.5";break;
 case '7':path=".2,.15 .8,.15 .4,.85";break;
 case '8':path=".5,.15 .25,.2 .25,.35 .75,.65 .75,.8 .5,.85 .25,.8 .25,.65 .75,.35 .75,.2 .5,.15";break;
 case '9':path=".8,.5 .5,.6 .2,.45 .2,.2 .5,.15 .8,.25 .8,.6 .5,.85 .2,.85";break;
 default:throw new IllegalArgumentException("Glyph "+c);
 }String[]points=path.split(" ");float[][]out=new float[points.length][2];for(int i=0;i<points.length;i++){String[]xy=points[i].split(",");out[i][0]=Float.parseFloat(xy[0]);out[i][1]=Float.parseFloat(xy[1]);}return out;}
}
