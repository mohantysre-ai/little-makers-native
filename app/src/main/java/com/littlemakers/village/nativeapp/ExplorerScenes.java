package com.littlemakers.village.nativeapp;
import java.util.*;
/** Shared display lists use actual lit meshes; only tracing guides and labels are flat overlays. */
final class ExplorerScenes {
 static final int[] COLORS={0xe9bb77,0x83baa7,0xa8b4db,0xd6a3bf,0x8bbccd,0xbbad84,0xe5a488};
 static float mapX(int i){return 80+(i/6%2==0?i%6:5-i%6)*96;}
 static SceneFrame build(ExplorerState s,TangramState shapes,MakerState world,boolean[]done,ThemeManager theme,int skin,float reaction){
  List<SceneFrame.Node>n=new ArrayList<>();int mode=s.mode;
  if(mode==0){ExplorerCatalog.Lesson q=ExplorerCatalog.LESSONS[s.level];for(int i=0;i<q.count;i++){float x=100+i%5*110,y=135+i/5*100;if(q.model>=0)n.add(SceneFrame.model(x,y,45,q.model,s.level==20?(i%2==0?0xdb8b90:0x80afd2):q.color,true));else if(q.model==-1)n.add(new SceneFrame.Node(9,x,y,50,50,25,q.color,0,true));else n.add(SceneFrame.shape(x,y,45,0,q.color));}n.add(new SceneFrame.Node(SceneFrame.CHARACTER,510,315,70,70,30,skin,0,true));}
  else if(mode==1){n.add(new SceneFrame.Node(6,320,205,310,198,-40,theme.dark?0x2e3a52:0xf2ead8,0,false));n.add(SceneFrame.model(78,325,50,StoryModels.BOOK,0x9cb9d5,true));n.add(new SceneFrame.Node(SceneFrame.CHARACTER,555,320,55,55,40,skin,0,true));if(s.level>=26)n.add(SceneFrame.model(320,175,80,StoryModels.STAR,0xefc679,true));}
  else if(mode==2){n.add(new SceneFrame.Node(6,320,210,310,200,-40,theme.dark?0x273d4c:0xc6dbb7,0,false));for(int i=0;i<23;i++){float x=mapX(i),y=StudioBoardView.cellY(i),xx=mapX(i+1),yy=StudioBoardView.cellY(i+1),dx=xx-x,dy=yy-y;n.add(new SceneFrame.Node(6,(x+xx)/2,(y+yy)/2,(float)Math.hypot(dx,dy)/2,4,-6,0x9a866e,(int)(Math.toDegrees(Math.atan2(dy,dx))*100),false));}for(int i=0;i<24;i++)n.add(SceneFrame.shape(mapX(i),StudioBoardView.cellY(i),29,0,done[i]?0x83b69c:COLORS[i/4]));}
  else if(mode==3){ExplorerCatalog.Tale t=ExplorerCatalog.TALES[s.level];n.add(SceneFrame.model(330,300,76,t.prop,COLORS[s.level%7],true));float move=s.phase==0?0:reaction;float a=s.first==0?75*move:-30*move,b=s.first==0?-30*move:30*move;n.add(new SceneFrame.Node(SceneFrame.CHARACTER,140+a,255,75,75,35,skin,0,true));n.add(new SceneFrame.Node(SceneFrame.CHARACTER,500+b,255,75,75,35,0xe4aa91,2,true));n.add(SceneFrame.model(65,105,90,StoryModels.TREE,0x72a783,true));}
  else if(mode==4){n.add(new SceneFrame.Node(6,320,210,310,200,-40,theme.dark?0x344359:0xf2ead8,0,false));for(int i=0;i<7;i++)n.add(new SceneFrame.Node(TangramState.KIND[i],shapes.x[i],shapes.y[i],TangramState.SIZE[i],TangramState.SIZE[i],shapes.selected==i?30:18,COLORS[i],shapes.rotation[i]*4500,false));}
  else {n.add(new SceneFrame.Node(6,320,210,310,200,-40,theme.dark?0x294333:0x99ba8c,0,false));for(int i=0;i<24;i++){float x=StudioBoardView.cellX(i),y=StudioBoardView.cellY(i);n.add(SceneFrame.panel(x-44,y-40,x+44,y+40,theme.dark?0x35543f:0xc8dcb1));int item=world.cells[i];if(item>=0){if(item==2)n.add(new SceneFrame.Node(6,x,y,36,27,15,0x75bac9,0,false));n.add(new SceneFrame.Node(SceneFrame.MODEL,x,y+8,31,31,35,COLORS[item],MakerState.MODELS[item],true,world.turns[i]*90));}}}
  return new SceneFrame(n,theme.background,420,"welcome",s.won);
 }
 private ExplorerScenes(){}
}
