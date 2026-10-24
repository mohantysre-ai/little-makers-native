package com.littlemakers.village.nativeapp;

import java.util.*;

/** 1,200 native modules: 20 mechanics with sixty explicitly numbered challenges each. */
public final class GameCatalog {
    public static final int LEVELS_PER_KIND=60;
    public enum Kind { COUNT, ADD, SUBTRACT, MULTIPLY, DIVIDE, SHAPES, COLORS, PATTERN, MEMORY, SIZES, TRACE, WORD, MAZE, ROBOT, WATER, BLOCKS, MERGE, RHYTHM, BRIDGE, PLAN }
    public static final class Game {
        public final String id, title, skill;
        public final Kind kind;
        public final int round, difficulty;
        Game(Kind kind, int round, String title, String skill) {
            this.kind=kind; this.round=round; this.title=title; this.skill=skill;
            this.difficulty=round<10?round/4:Math.min(2,(round-10)/20); this.id="native-"+kind.name().toLowerCase(Locale.ROOT)+"-"+(round+1);
        }
        public int rewardCoins(){ return 5+difficulty*2; }
    }
    private static final String[] TITLES={"Count the garden","Addition picnic","Take-away orchard","Times-table tiles","Share fairly","Shape post","Color paths","Pattern parade","Memory friends","Small to tall","Letter trails","Word garden","Maze meadow","Robot routes","Color bottles","Block workshop","Number doubles","Rhythm steps","Bridge builder","Island planner"};
    private static final String[] SKILLS={"Numbers","Numbers","Numbers","Numbers","Numbers","Motor","Motor","Logic","Logic","Motor","Letters","Letters","Logic","Coding","Logic","Building","Numbers","Music","Building","Planning"};
    public static final List<Game> ALL;
    static {
        List<Game> games=new ArrayList<>();
        for(Kind kind:Kind.values()) for(int round=0;round<LEVELS_PER_KIND;round++) games.add(new Game(kind,round,TITLES[kind.ordinal()]+" "+(round+1),SKILLS[kind.ordinal()]));
        ALL=Collections.unmodifiableList(games);
    }
    public static Game find(String id){ for(Game game:ALL)if(game.id.equals(id))return game; throw new IllegalArgumentException("Unknown module"); }
    public static String sectionTitle(Kind kind){return TITLES[kind.ordinal()];}
    public static Game at(Kind kind,int round){if(round<0||round>=LEVELS_PER_KIND)throw new IllegalArgumentException("Level");return ALL.get(kind.ordinal()*LEVELS_PER_KIND+round);}
    private GameCatalog(){}
}
