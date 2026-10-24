package com.littlemakers.village.nativeapp;
import java.util.*;
public final class SceneFrameTest {
 public static void main(String[]args){List<SceneFrame.Node>nodes=new ArrayList<>();nodes.add(SceneFrame.shape(10,20,30,-1,0xff8899aa));SceneFrame frame=new SceneFrame(nodes,0xff202030,1000,"game",false);nodes.clear();if(frame.nodes.length!=1||frame.nodes[0].type!=SceneFrame.BLOCK)throw new AssertionError("Mutable snapshot");for(String key:StoryModels.ALL.keySet())if(StoryModels.forItem(key)<0)throw new AssertionError(key);if(StoryModels.ALL.size()!=41)throw new AssertionError("Missing prop mapping");try{StoryModels.forItem("missing.png");throw new AssertionError("Silent substitute");}catch(IllegalArgumentException expected){}try{SceneFrame.shape(Float.NaN,0,20,1,0);throw new AssertionError("NaN geometry");}catch(IllegalArgumentException expected){}System.out.println("PASS: immutable scene handoff, valid geometry, 41 explicit story prop mappings and fail-fast coverage");}
}
