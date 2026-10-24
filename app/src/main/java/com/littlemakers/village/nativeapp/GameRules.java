package com.littlemakers.village.nativeapp;

import java.util.*;
import java.io.*;
import static com.littlemakers.village.nativeapp.GameCatalog.Kind;

/** Android-independent rules. Rendering, audio, storage and rewards are separate. */
public final class GameRules {
 public final GameCatalog.Game game;
 public int a,b,answer,total,selected=-1,next=0,position=12,goal=3,rotation=0,required=2;
 public boolean won=false,running=false;
 public String message="Take your time. Try an idea.";
 public int[] choices,deck,values,target,sequence;
 public boolean[] observed;
 public final List<Integer> open=new ArrayList<>(), matched=new ArrayList<>(), chosen=new ArrayList<>(), path=new ArrayList<>(), commands=new ArrayList<>();
 public final List<List<Integer>> tubes=new ArrayList<>();
 public final List<int[]> history=new ArrayList<>();
 public final List<List<Integer>> edges=new ArrayList<>();
 public String word,letter;
 public float[][] trace;
 public int startPosition,blockShape=0;public int[] obstacles={5,6,9};public int[][]costs={{2,1},{1,2},{3,0}};
 private final Random random;
 public GameRules(GameCatalog.Game game){
  this.game=game;random=new Random(1027+game.kind.ordinal()*8191+game.round*97);
  int r=game.round%10;
  a=1+r%5;b=1+(r/2)%4;
  switch(game.kind){
   case COUNT: answer=r+1;total=answer;break;
   case ADD: answer=a+b;total=answer;break;
   case SUBTRACT:a=3+r;b=1+r%3;answer=a-b;total=answer;break;
   case MULTIPLY:a=2+r%3;b=2+r/3;answer=a*b;total=answer;break;
   case DIVIDE:b=2+r%3;answer=2+r/3;a=b*answer;total=a;break;
   case SHAPES: case COLORS:answer=r%3;choices=shuffle(new int[]{0,1,2});break;
   case PATTERN:sequence=new int[5+r%3];int[] rule=new int[][]{{0,1},{0,0,1},{0,1,2},{0,1,1},{0,1,0,2}}[r%5];for(int i=0;i<sequence.length;i++)sequence[i]=rule[i%rule.length];answer=rule[sequence.length%rule.length];choices=shuffle(new int[]{0,1,2});break;
   case MEMORY:int n=2+r%3;deck=new int[n*2];for(int i=0;i<deck.length;i++)deck[i]=i%n;deck=shuffle(deck);break;
   case SIZES:values=new int[3+r%3];for(int i=0;i<values.length;i++)values[i]=i;values=shuffle(values);break;
   case TRACE:letter="ABCDEFGHIJ".substring(r,r+1);trace=traces(r);break;
   case WORD:word=new String[]{"CAT","DOG","SUN","BUS","FISH","MOON","TREE","BIRD","STAR","BOAT"}[r];values=new int[word.length()];for(int i=0;i<values.length;i++)values[i]=i;values=shuffle(values);break;
   case MAZE:position=12;goal=3;makeMaze();path.add(position);break;
   case ROBOT:position=new int[]{12,15,0,3,8,11,4,7,13,14}[r];goal=15-position;break;
   case WATER:for(int i=0;i<4;i++)tubes.add(new ArrayList<Integer>());int c=r%3,d=(c+1)%3;tubes.get(r%4).addAll(Arrays.asList(c,d));tubes.get((r+1)%4).addAll(Arrays.asList(d,c));break;
   case BLOCKS:a=new int[]{0,1,2,4,5,6,8,9,10,1}[r];answer=r%4;rotation=(answer+1)%4;target=blockCells(a,answer);break;
   case MERGE:values=new int[16];a=(r%4)*4+(r%2);b=2<<(r/4);values[a]=b;values[a+1]=b;values[a+2]=b*2;answer=b*4;break;
   case RHYTHM:sequence=new int[3+r%4];for(int i=0;i<sequence.length;i++)sequence[i]=(r+i+(i/2))%3;break;
   case BRIDGE:required=2+r/4;a=4+r;break;
   case PLAN:a=3+r%4;b=3+r%3;break;
  }
  if(game.round>=10)ContentPack.expand(this);startPosition=position;
  if(game.kind.ordinal()<=Kind.DIVIDE.ordinal()){observed=new boolean[total];choices=shuffle(new int[]{Math.max(0,answer-1),answer,answer+1});}
 }
 private int[] shuffle(int[] data){for(int i=data.length-1;i>0;i--){int j=random.nextInt(i+1),x=data[i];data[i]=data[j];data[j]=x;}return data;}
 public static boolean adjacent(int a,int b){return a>=0&&a<16&&b>=0&&b<16&&Math.abs(a/4-b/4)+Math.abs(a%4-b%4)==1;}
 private void makeMaze(){for(int i=0;i<16;i++)edges.add(new ArrayList<Integer>());boolean[] seen=new boolean[16];visit(0,seen);}
 private void visit(int c,boolean[] seen){seen[c]=true;int[] candidates=shuffle(new int[]{c-4,c+1,c+4,c-1});for(int n:candidates)if(adjacent(c,n)&&!seen[n]){edges.get(c).add(n);edges.get(n).add(c);visit(n,seen);}}
 public static int[] blockCells(int anchor,int rotation){return blockCells(anchor,rotation,0);}
 public static int[] blockCells(int anchor,int rotation,int shape){int[][]source=shape==1?new int[][]{{0,0},{1,0},{2,0}}:shape==2?new int[][]{{0,0},{1,0},{2,0},{1,1}}:shape==3?new int[][]{{0,0},{1,0},{0,1},{1,1}}:new int[][]{{0,0},{0,1},{1,1}};int minX=99,minY=99;for(int[]p:source){for(int j=0;j<rotation;j++){int x=p[0];p[0]=-p[1];p[1]=x;}minX=Math.min(minX,p[0]);minY=Math.min(minY,p[1]);}int[]cells=new int[source.length];for(int i=0;i<cells.length;i++)cells[i]=anchor+source[i][0]-minX+4*(source[i][1]-minY);return cells;}
 public int rotationCount(){return blockShape==1?2:blockShape==3?1:4;}
 public boolean obstacle(int cell){for(int blocked:obstacles)if(blocked==cell)return true;return false;}

 public boolean action(String action,int index){
  if(won&&!action.equals("undo"))return false;
  boolean changed=false;message="We can try another idea.";
  if(action.equals("count")&&observed!=null&&index>=0&&index<observed.length){observed[index]=true;changed=true;message="You counted this item.";}
  else if(action.equals("choose")&&(game.kind==Kind.SHAPES||game.kind==Kind.COLORS)&&index>=0&&index<3){selected=index;changed=true;}
  else if(action.equals("match")&&(game.kind==Kind.SHAPES||game.kind==Kind.COLORS)){if(index==answer&&selected==answer){changed=true;if(sequence==null||++next==sequence.length)win();else{answer=sequence[next];selected=-1;message="A match! Find the next parcel.";}}else message="Choose the parcel with the same shape or color.";}
  else if(action.equals("answer")&&(game.kind.ordinal()<=Kind.DIVIDE.ordinal()||game.kind==Kind.PATTERN)){
   boolean ready=true;if(observed!=null)for(boolean touched:observed)ready&=touched;
   if(!ready){message="Touch each item first, then choose the answer.";return false;}
   if(index==answer){win();changed=true;}else message="Look closely. Try another answer.";
  }else if(action.equals("card")&&game.kind==Kind.MEMORY&&index>=0&&index<deck.length){
   if(open.size()==2||open.contains(index)||matched.contains(index))return false;
   open.add(index);changed=true;if(open.size()==2){if(deck[open.get(0)]==deck[open.get(1)]){matched.addAll(open);open.clear();if(matched.size()==deck.length)win();else message="A matching pair!";}else message="Different friends. Turn them over and try another pair.";}
  }else if(action.equals("turn")){open.clear();changed=true;}
  else if(action.equals("size")&&game.kind==Kind.SIZES&&index==next){chosen.add(index);next++;changed=true;if(next==values.length)win();else message="Now choose the smallest still waiting.";}
  else if(action.equals("trace")&&game.kind==Kind.TRACE&&index==next){next++;changed=true;if(next==trace.length)win();else message="Follow the next stepping stone.";}
  else if(action.equals("letter")&&game.kind==Kind.WORD&&index>=0&&index<word.length()&&!chosen.contains(index)&&word.charAt(index)==word.charAt(chosen.size())){chosen.add(index);changed=true;if(chosen.size()==word.length())win();}
  else if(action.equals("cell")&&game.kind==Kind.MAZE&&edges.get(position).contains(index)){position=index;path.add(index);changed=true;if(position==goal)win();}
  else if(action.equals("command")&&game.kind==Kind.ROBOT&&!running&&commands.size()<24&&index>=0&&index<4){commands.add(index);changed=true;}
  else if(action.equals("run")&&game.kind==Kind.ROBOT&&!commands.isEmpty()){position=startPosition;next=0;running=true;changed=true;}
  else if(action.equals("rotate")&&game.kind==Kind.BLOCKS){rotation=(rotation+1)%rotationCount();changed=true;}
  else if(action.equals("fit")&&game.kind==Kind.BLOCKS){if(index==a&&rotation==answer){win();changed=true;}else message="Match the direction and the top square of the outline.";}
  else if(action.equals("pour")&&game.kind==Kind.WATER&&index>=0&&index<tubes.size()){if(selected<0){selected=index;changed=true;}else{List<Integer>from=tubes.get(selected),to=tubes.get(index);if(selected!=index&&!from.isEmpty()&&to.size()<2&&(to.isEmpty()||to.get(to.size()-1).equals(from.get(from.size()-1)))){if(history.size()==100)history.remove(0);history.add(waterSnapshot());to.add(from.remove(from.size()-1));changed=true;boolean done=true;for(List<Integer>t:tubes)if(!t.isEmpty()&&(t.size()!=2||!t.get(0).equals(t.get(1))))done=false;if(done)win();}else message="Pour onto the same color, or into an empty bottle.";selected=-1;}}
  else if(action.equals("merge")&&game.kind==Kind.MERGE&&index>=0&&index<16){if(selected<0){if(values[index]>0){selected=index;changed=true;}}else{if(adjacent(selected,index)&&values[selected]>0&&values[selected]==values[index]){if(history.size()==100)history.remove(0);history.add(values.clone());values[index]*=2;values[selected]=0;changed=true;if(values[index]==answer)win();}else message="Join equal numbers that share an edge.";selected=-1;}}
  else if(action.equals("note")&&game.kind==Kind.RHYTHM){if(index==sequence[next]){next++;changed=true;if(next==sequence.length)win();}else message="Look at the next shape in the tune.";}
  else if(action.equals("part")&&game.kind==Kind.BRIDGE){if(index==required&&chosen.size()<required)message="Add each support before the deck.";else if(index>=0&&index<=required&&!chosen.contains(index)){if(sequence!=null&&index<required&&index!=sequence[next]){message="Try support "+(sequence[next]+1)+" next.";return false;}chosen.add(index);if(index<required&&sequence!=null)next++;changed=true;if(chosen.size()==required+1)win();else if(sequence!=null)message=next<required?"Try support "+(sequence[next]+1)+" next.":"Now add the deck.";}}
  else if(action.equals("plan")&&game.kind==Kind.PLAN&&index>=0&&index<3&&!chosen.contains(index)){int[][]cost=costs;int wood=a,water=b;for(int i:chosen){wood-=cost[i][0];water-=cost[i][1];}if(cost[index][0]<=wood&&cost[index][1]<=water){chosen.add(index);changed=true;if(chosen.contains(0)&&chosen.contains(1))win();}else message="That needs more supplies. Undo a build and try another plan.";}
  else if(action.equals("undo")){
   if(game.kind==Kind.WATER&&!history.isEmpty()){restoreWater(history.remove(history.size()-1));won=false;changed=true;}
   else if(game.kind==Kind.MERGE&&!history.isEmpty()){values=history.remove(history.size()-1);won=false;changed=true;}
   else if(game.kind==Kind.ROBOT&&!running&&!commands.isEmpty()){commands.remove(commands.size()-1);changed=true;}
   else if(game.kind==Kind.PLAN&&!chosen.isEmpty()){chosen.remove(chosen.size()-1);won=false;changed=true;}
  }
  return changed;
 }
 public void robotStep(){if(!running||won)return;int direction=commands.get(next++),delta=new int[]{-4,1,4,-1}[direction],to=position+delta;if(!adjacent(position,to)||obstacle(to)){running=false;message="That step meets an obstacle. Edit the program.";return;}position=to;if(position==goal){running=false;win();}else if(next==commands.size()){running=false;message="Add more steps to reach the flower.";}}
 private void win(){won=true;running=false;message="You explored it! Replay freely or choose another game.";}
 private int[] waterSnapshot(){int[] result=new int[tubes.size()*3];Arrays.fill(result,-1);for(int i=0;i<tubes.size();i++){result[i*3]=tubes.get(i).size();for(int j=0;j<tubes.get(i).size();j++)result[i*3+1+j]=tubes.get(i).get(j);}return result;}
 private void restoreWater(int[] data){for(int i=0;i<tubes.size();i++){tubes.get(i).clear();for(int j=0;j<data[i*3];j++)tubes.get(i).add(data[i*3+1+j]);}selected=-1;}
 public byte[] saveState(){try{ByteArrayOutputStream bytes=new ByteArrayOutputStream();DataOutputStream out=new DataOutputStream(bytes);out.writeInt(2);out.writeUTF(game.id);out.writeBoolean(won);out.writeInt(next);out.writeInt(position);out.writeInt(rotation);write(out,chosen);write(out,open);write(out,matched);write(out,path);write(out,commands);if(observed==null)out.writeInt(0);else{out.writeInt(observed.length);for(boolean v:observed)out.writeBoolean(v);}writeArray(out,values);out.writeInt(tubes.size());for(List<Integer>tube:tubes)write(out,tube);out.writeInt(history.size());for(int[]entry:history)writeArray(out,entry);out.flush();return bytes.toByteArray();}catch(IOException impossible){throw new IllegalStateException(impossible);}}
 public void restore(byte[] bytes){if(bytes==null)return;try{DataInputStream in=new DataInputStream(new ByteArrayInputStream(bytes));if(in.readInt()!=2||!in.readUTF().equals(game.id))throw new IOException();won=in.readBoolean();next=in.readInt();position=in.readInt();rotation=in.readInt();read(in,chosen);read(in,open);read(in,matched);read(in,path);read(in,commands);int n=bounded(in.readInt());if(n>0){if(observed==null||n!=observed.length)throw new IOException();for(int i=0;i<n;i++)observed[i]=in.readBoolean();}int[] restored=readArray(in);if(restored!=null){if(values==null||restored.length!=values.length)throw new IOException();values=restored;}n=bounded(in.readInt());if(n!=tubes.size())throw new IOException();for(List<Integer>tube:tubes)read(in,tube);n=bounded(in.readInt());history.clear();for(int i=0;i<n;i++){int[]entry=readArray(in);if(entry==null||entry.length!=(game.kind==Kind.WATER?tubes.size()*3:16))throw new IOException();history.add(entry);}validate();if((game.kind==Kind.SHAPES||game.kind==Kind.COLORS)&&sequence!=null)answer=sequence[Math.min(next,sequence.length-1)];if(in.available()!=0)throw new IOException();running=false;selected=-1;message=won?"You already explored this. Replay freely.":"Welcome back. Continue at your own pace.";}catch(IOException e){throw new IllegalArgumentException("Invalid saved game state",e);}}
 private void validate()throws IOException{
  if(position<0||position>=16||rotation<0||rotation>3||next<0||next>128)throw new IOException();
  for(int v:commands)if(v<0||v>3)throw new IOException();
  for(int v:path)if(v<0||v>=16)throw new IOException();
  if(deck!=null){boolean[]seen=new boolean[deck.length];for(int v:matched){if(v<0||v>=deck.length||seen[v])throw new IOException();seen[v]=true;}for(int v:open)if(v<0||v>=deck.length||seen[v])throw new IOException();if(open.size()>2)throw new IOException();}
  int limit=game.kind==Kind.WORD?word.length():game.kind==Kind.SIZES?values.length:game.kind==Kind.BRIDGE?required+1:game.kind==Kind.PLAN?3:128;
  Set<Integer>seen=new HashSet<>();for(int v:chosen)if(v<0||v>=limit||!seen.add(v))throw new IOException();
  if(game.kind==Kind.TRACE&&next>trace.length||game.kind==Kind.RHYTHM&&next>sequence.length||game.kind==Kind.SIZES&&next>values.length||game.kind==Kind.ROBOT&&next>commands.size()||(game.kind==Kind.SHAPES||game.kind==Kind.COLORS)&&sequence!=null&&next>sequence.length)throw new IOException();
  for(List<Integer>tube:tubes){if(tube.size()>2)throw new IOException();for(int v:tube)if(v<0||v>2)throw new IOException();}
  if(values!=null){for(int v:values)if(v<0||v>65536)throw new IOException();}
 }
 private static int bounded(int n)throws IOException{if(n<0||n>128)throw new IOException();return n;}
 private static void write(DataOutputStream out,List<Integer>list)throws IOException{out.writeInt(list.size());for(int v:list)out.writeInt(v);}
 private static void read(DataInputStream in,List<Integer>list)throws IOException{int n=bounded(in.readInt());list.clear();for(int i=0;i<n;i++)list.add(in.readInt());}
 private static void writeArray(DataOutputStream out,int[]a)throws IOException{out.writeInt(a==null?-1:a.length);if(a!=null)for(int v:a)out.writeInt(v);}
 private static int[] readArray(DataInputStream in)throws IOException{int n=in.readInt();if(n==-1)return null;n=bounded(n);int[]a=new int[n];for(int i=0;i<n;i++)a[i]=in.readInt();return a;}
 static float[][] traces(int r){float[][][] paths={{{.2f,.85f},{.5f,.15f},{.8f,.85f},{.65f,.5f},{.35f,.5f}},{{.25f,.85f},{.25f,.15f},{.65f,.15f},{.8f,.35f},{.25f,.5f},{.7f,.5f},{.8f,.7f},{.6f,.85f},{.25f,.85f}},{{.8f,.2f},{.5f,.15f},{.2f,.35f},{.2f,.65f},{.5f,.85f},{.8f,.8f}},{{.2f,.85f},{.2f,.15f},{.6f,.15f},{.8f,.35f},{.8f,.65f},{.6f,.85f},{.2f,.85f}},{{.8f,.15f},{.2f,.15f},{.2f,.85f},{.8f,.85f},{.2f,.85f},{.2f,.5f},{.65f,.5f}},{{.8f,.15f},{.2f,.15f},{.2f,.85f},{.2f,.5f},{.65f,.5f}},{{.8f,.2f},{.4f,.15f},{.2f,.4f},{.25f,.75f},{.65f,.85f},{.8f,.6f},{.55f,.6f}},{{.2f,.15f},{.2f,.85f},{.2f,.5f},{.8f,.5f},{.8f,.15f},{.8f,.85f}},{{.3f,.15f},{.7f,.15f},{.5f,.15f},{.5f,.85f},{.3f,.85f},{.7f,.85f}},{{.3f,.15f},{.8f,.15f},{.6f,.15f},{.6f,.7f},{.5f,.85f},{.25f,.8f}}};return paths[r];}
}
