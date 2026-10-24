package com.littlemakers.village.nativeapp;

/** Shared, deterministic scene timing; no randomness or reward state in the renderer. */
final class VillageSceneState {
 static final float[][] PLACES={{-3.1f,0,-1.8f},{3.0f,0,-1.8f},{-3.0f,0,2.3f},{3.0f,0,2.3f},{0,0,.2f}};
 static final String[] ROUTES={"clay","paint","shape","story","texture"};
 float seconds,yaw;int selected=-1;float responseAge=2;
 void advance(float dt,boolean reduced){float step=Math.max(0,Math.min(.05f,dt));if(!reduced)seconds+=step;responseAge=Math.min(2,responseAge+step);}
 void turn(float delta){yaw=Math.max(-28,Math.min(28,yaw+delta));}
 void select(int place){if(place<0||place>=PLACES.length)throw new IllegalArgumentException("Unknown landmark");selected=place;responseAge=0;}
 float bounce(int place,boolean reduced){if(reduced||selected!=place||responseAge>=.75f)return 0;return(float)Math.sin(Math.PI*responseAge/.75f)*.65f;}
}
