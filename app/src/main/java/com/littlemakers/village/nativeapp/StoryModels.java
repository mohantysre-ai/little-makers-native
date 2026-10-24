package com.littlemakers.village.nativeapp;
import java.util.*;
/** Explicit semantic coverage; unknown props are reported instead of silently becoming a cube. */
final class StoryModels {
 static final int HOUSE=0,CLOUD=1,BIKE=2,MOON=3,FLOWER=4,PLANT=5,CORAL=6,FISH=7,BASKET=8,BREAD=9,TELESCOPE=10,STAR=11,BEAR=12,SHELL=13,CRAB=14,TURTLE=15,CARROT=16,UMBRELLA=17,SUN=18,BOOK=19,SEEDLING=20,BOAT=21,APPLE=22,KITE=23,TENT=24,BUTTERFLY=25,STRAWBERRY=26,TREE=27,RABBIT=28,PINE=29,PALETTE=30,BRUSH=31,TULIP=32,PLANK=33,SEAWEED=34,CHILD=35;
 static final Map<String,Integer> ALL;
 static {Map<String,Integer>m=new LinkedHashMap<>();String[]codes={"1F3E0","2601","1F6B2","1F315","1F33B","1FAB4","1FAB8","1F41F","1F9FA","1F35E","1F52D","2B50","1F9F8","1F41A","1F980","1F422","1F955","2602","2600","1F4D3","1F331","26F5","1F34E","1FA81","26FA","1F98B","1F353","1F333","1F407","1F332","1F3A8","1F58C","1F337"};for(int i=0;i<codes.length;i++)m.put("pictures/color-svg/"+codes[i]+".svg",i);m.put("pictures/color-svg/1F420.svg",FISH);m.put("fence.png",PLANK);m.put("seaweed_green_a.png",SEAWEED);m.put("seaweed_pink_a.png",SEAWEED);m.put("fish_blue.png",FISH);m.put("cloud2.png",CLOUD);m.put("player_walk1.png",CHILD);m.put("female_walk1.png",CHILD);ALL=Collections.unmodifiableMap(m);}
 static int forItem(String item){Integer model=ALL.get(item);if(model==null)throw new IllegalArgumentException("Unmodeled story prop: "+item);return model;}
 private StoryModels(){}
}
