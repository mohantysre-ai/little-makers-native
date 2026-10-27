package com.littlemakers.village.nativeapp;
/** Eight original silhouettes; story pairs are deterministic and always different. */
final class CompanionCast {
 static final String[]NAMES={"Pip the rabbit","Fenn the fox","Dotti the dinosaur","Tula the turtle","Ollie the owl","Bolt the robot","Nova the explorer","Ember the dragon"};
 static final int[]COLORS={0xffe7d8bd,0xffe99b61,0xff83bd9a,0xff86b9a5,0xffc2a0d0,0xff85b9d9,0xffe9ded0,0xffa99bda};
 static int first(int story){return Math.floorMod(story,8);}static int second(int story){return Math.floorMod(story+3,8);}
}
