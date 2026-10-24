package com.littlemakers.village.nativeapp;
import java.util.*;
public final class RulesTest {
 static int checks=0;
 static void check(boolean ok,String reason){checks++;if(!ok)throw new AssertionError(reason);}
 static List<Integer> route(GameRules s,boolean robot){Queue<Integer>q=new ArrayDeque<>();int[]prev=new int[16];Arrays.fill(prev,-1);q.add(s.position);prev[s.position]=s.position;while(!q.isEmpty()){int v=q.remove();for(int n=0;n<16;n++)if(prev[n]<0&&(robot?GameRules.adjacent(v,n)&&!s.obstacle(n):s.edges.get(v).contains(n))){prev[n]=v;q.add(n);}}check(prev[s.goal]>=0,"reachable goal");List<Integer>result=new ArrayList<>();for(int v=s.goal;v!=s.position;v=prev[v])result.add(v);Collections.reverse(result);return result;}
 static List<int[]> water(GameRules initial){Queue<GameRules>q=new ArrayDeque<>();Queue<List<int[]>>paths=new ArrayDeque<>();Set<String>seen=new HashSet<>();q.add(initial);paths.add(new ArrayList<>());while(!q.isEmpty()){GameRules s=q.remove();List<int[]>p=paths.remove();if(s.won)return p;for(int a=0;a<s.tubes.size();a++)for(int b=0;b<s.tubes.size();b++){GameRules next=new GameRules(s.game);next.restore(s.saveState());next.action("pour",a);next.action("pour",b);String key=next.tubes.toString();if(seen.add(key)){List<int[]>np=new ArrayList<>(p);np.add(new int[]{a,b});q.add(next);paths.add(np);}}}throw new AssertionError("Unsolvable water");}
 static String fingerprint(GameRules s){return s.a+":"+s.b+":"+s.answer+":"+s.total+":"+s.position+":"+s.goal+":"+s.rotation+":"+s.required+":"+s.blockShape+":"+s.word+":"+s.letter+":"+Arrays.toString(s.deck)+":"+Arrays.toString(s.values)+":"+Arrays.toString(s.target)+":"+Arrays.toString(s.sequence)+":"+Arrays.toString(s.obstacles)+":"+Arrays.deepToString(s.costs)+":"+Arrays.deepToString(s.trace)+":"+s.edges+":"+s.tubes;}
 static void solve(GameRules s){switch(s.game.kind){
 case COUNT:case ADD:case SUBTRACT:case MULTIPLY:case DIVIDE:check(!s.action("answer",s.answer),"count before answer");for(int i=0;i<s.total;i++)s.action("count",i);s.action("answer",s.answer);break;
 case SHAPES:case COLORS:check(!s.action("match",s.answer),"selection required");s.action("choose",(s.answer+1)%3);check(!s.action("match",s.answer),"wrong match");while(!s.won){s.action("choose",s.answer);s.action("match",s.answer);}break;
 case PATTERN:s.action("answer",s.answer);break;
 case MEMORY:for(int value=0;value<s.deck.length/2;value++)for(int i=0;i<s.deck.length;i++)if(s.deck[i]==value)s.action("card",i);break;
 case SIZES:for(int i=0;i<s.values.length;i++)s.action("size",i);break;
 case TRACE:for(int i=0;i<s.trace.length;i++)s.action("trace",i);break;
 case WORD:for(int i=0;i<s.word.length();i++)s.action("letter",i);break;
 case MAZE:for(int n:route(s,false))s.action("cell",n);break;
 case ROBOT:int p=s.position;for(int n:route(s,true)){int d=n-p;s.action("command",d==-4?0:d==1?1:d==4?2:3);p=n;}s.action("run",0);while(s.running)s.robotStep();break;
 case WATER:for(int[]step:water(s)){s.action("pour",step[0]);s.action("pour",step[1]);}break;
 case BLOCKS:while(s.rotation!=s.answer)s.action("rotate",0);s.action("fit",s.a);break;
 case MERGE:int[]chain=s.target==null?new int[]{s.a,s.a+1,s.a+2}:s.target;s.action("merge",chain[0]);s.action("merge",chain[1]);s.action("merge",chain[1]);s.action("merge",chain[2]);break;
 case RHYTHM:for(int note:s.sequence)s.action("note",note);break;
 case BRIDGE:check(!s.action("part",s.required),"deck requires supports");for(int i=0;i<s.required;i++)s.action("part",s.sequence==null?i:s.sequence[i]);s.action("part",s.required);break;
 case PLAN:s.action("plan",0);s.action("plan",1);break;
 }}
 public static void main(String[]args){check(GameCatalog.ALL.size()==1200,"1200 catalog");Set<String>ids=new HashSet<>();Map<GameCatalog.Kind,Set<String>>fingerprints=new EnumMap<>(GameCatalog.Kind.class);for(GameCatalog.Kind k:GameCatalog.Kind.values())fingerprints.put(k,new HashSet<>());for(GameCatalog.Game game:GameCatalog.ALL){check(ids.add(game.id),"unique ID");GameRules s=new GameRules(game);if(game.round>=10)check(fingerprints.get(game.kind).add(fingerprint(s)),"Duplicate added board "+game.id);if(s.observed!=null)check(s.total<=16,"math layout cap");if(s.target!=null)for(int cell:s.target)check(cell>=0&&cell<16,"board cell bounds "+game.id);GameRules restored=new GameRules(game);restored.restore(s.saveState());solve(restored);check(restored.won,"win "+game.id);GameRules reload=new GameRules(game);reload.restore(restored.saveState());check(reload.won,"restored win "+game.id);check(!reload.action("answer",-1),"no second win");check(!s.won,"independent state");check(Arrays.equals(s.saveState(),new GameRules(game).saveState()),"determinism");try{reload.restore(new byte[]{1,2,3});throw new AssertionError("accepted corrupt state");}catch(IllegalArgumentException expected){checks++;}}
 for(GameCatalog.Kind k:GameCatalog.Kind.values())check(fingerprints.get(k).size()==50,"50 distinct added boards "+k);
 for(GameCatalog.Kind k:new GameCatalog.Kind[]{GameCatalog.Kind.SHAPES,GameCatalog.Kind.COLORS}){GameRules partial=new GameRules(GameCatalog.at(k,35));partial.action("choose",partial.answer);partial.action("match",partial.answer);GameRules resumed=new GameRules(partial.game);resumed.restore(partial.saveState());check(resumed.next==1&&resumed.answer==resumed.sequence[1],"resume matching sequence");solve(resumed);check(resumed.won,"resume multi-match");}
 check(!GameRules.adjacent(3,4),"no row wrap");GameRules merge=new GameRules(GameCatalog.find("native-merge-1"));merge.action("merge",0);merge.action("merge",1);byte[]snapshot=merge.saveState();GameRules again=new GameRules(merge.game);again.restore(snapshot);check(again.action("undo",0)&&again.values[0]==2&&again.values[1]==2,"undo survives reopen");System.out.println("PASS: all 1200 modules solvable; "+checks+" rule/state checks.");}
}
