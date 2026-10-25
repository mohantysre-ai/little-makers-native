package com.littlemakers.village.nativeapp;
/** Deterministic guided exploration and one finite counting/rescue expedition. No reward mutation. */
final class AdventureState {
 static final float[][] SEEDS={{-1.45f,2.5f},{1.5f,1.25f},{-1.35f,-.95f}};
 static final float[][] PLACES=VillageSceneState.PLACES;
 volatile float x=0,z=4,targetX=0,targetZ=4,yaw=0,seconds,heading,walkPhase;volatile int collected,bridge;
 float rescue=1;boolean moving;float stickX,stickZ;private float[][]path=new float[0][];private int waypoint;
 synchronized void target(float tx,float tz){if(!Float.isFinite(tx)||!Float.isFinite(tz))return;float length=(float)Math.sqrt(tx*tx/30.25f+tz*tz/19.36f);if(length>1){tx/=length;tz/=length;}targetX=tx;targetZ=tz;stickX=stickZ=0;path=AdventurePath.find(this,x,z,tx,tz);waypoint=0;moving=path.length>0;}
 synchronized void joystick(float dx,float dz){if(!Float.isFinite(dx)||!Float.isFinite(dz)){dx=0;dz=0;}float length=(float)Math.hypot(dx,dz);stickX=length>1?dx/length:dx;stickZ=length>1?dz/length:dz;moving=length>.1f;targetX=x;targetZ=z;}
 synchronized void turn(float delta){if(Float.isFinite(delta))yaw=Math.max(-70,Math.min(70,yaw+delta));}
 synchronized void advance(float delta,boolean reduced){float dt=Float.isFinite(delta)?Math.max(0,Math.min(.05f,delta)):0;if(!reduced)seconds+=dt;if(bridge>=3)rescue=reduced?1:Math.min(1,rescue+dt/2.5f);float dx=targetX-x,dz=targetZ-z;if(Math.hypot(stickX,stickZ)<=.1&&moving){while(waypoint<path.length&&Math.hypot(path[waypoint][0]-x,path[waypoint][1]-z)<.06)waypoint++;if(waypoint>=path.length){moving=false;return;}dx=path[waypoint][0]-x;dz=path[waypoint][1]-z;}float length=(float)Math.hypot(dx,dz);
  if(Math.hypot(stickX,stickZ)>.1){double a=Math.toRadians(yaw);dx=stickX*(float)Math.cos(a)+stickZ*(float)Math.sin(a);dz=-stickX*(float)Math.sin(a)+stickZ*(float)Math.cos(a);length=1;}
  if(!moving||length<.06){moving=false;return;}float step=Math.min(length,dt*2.2f);float nx=x+dx/length*step,nz=z+dz/length*step;
  boolean moved=false;if(walkable(nx,nz)){x=nx;z=nz;moved=true;}else if(walkable(nx,z)){x=nx;moved=true;}else if(walkable(x,nz)){z=nz;moved=true;}
  if(moved){heading=(float)Math.toDegrees(Math.atan2(dx,dz));if(!reduced)walkPhase+=step*7;}else moving=false;
 }
 static boolean land(float x,float z){return x*x/30.25f+z*z/19.36f<=1.001;}
 boolean walkable(float x,float z){if(!land(x,z))return false;if(bridge<3&&z<-3.2f)return false;for(float[]p:PLACES)if(Math.hypot(x-p[0],z-p[2])<.85f)return false;return true;}
 synchronized int nearSeed(){for(int i=0;i<3;i++)if((collected&(1<<i))==0&&Math.hypot(x-SEEDS[i][0],z-SEEDS[i][1])<1.1)return i;return -1;}
 synchronized int nearPlace(){for(int i=0;i<PLACES.length;i++)if(Math.hypot(x-PLACES[i][0],z-PLACES[i][2])<1.65)return i;return -1;}
 synchronized boolean nearBridge(){return Math.hypot(x,z+2.6f)<1.2;}
 synchronized int seeds(){return Integer.bitCount(collected);}
 synchronized String action(){if(nearSeed()>=0)return "Collect seed";if(nearBridge())return bridge<3?seeds()>bridge?"Plant a bridge seed":"Find another seed":"Visit the aquarium";int p=nearPlace();return p>=0?"Enter "+new String[]{"Clay mountain","Color meadow","Shape space","Story tree","Texture garden"}[p]:"Follow the next clue";}
 /** Returns collect=1, bridge segment=2, final segment=3, otherwise 0. */
 synchronized int interact(){int seed=nearSeed();if(seed>=0){collected|=1<<seed;return 1;}if(nearBridge()&&bridge<3&&seeds()>bridge){bridge++;if(bridge==3)rescue=0;return bridge==3?3:2;}return 0;}
 synchronized void reset(){x=targetX=0;z=targetZ=4;collected=bridge=0;yaw=seconds=heading=walkPhase=stickX=stickZ=0;moving=false;rescue=1;path=new float[0][];waypoint=0;}
 synchronized void nextClue(){if(bridge>=3){target(0,-4.05f);return;}int i=0;while(i<3&&(collected&(1<<i))!=0)i++;if(i<3)target(SEEDS[i][0],SEEDS[i][1]);else target(0,-2.6f);}
 synchronized String save(){return x+","+z+","+collected+","+bridge+","+yaw;}
 synchronized void restore(String text){try{String[]s=text.split(",");float a=Float.parseFloat(s[0]),b=Float.parseFloat(s[1]),angle=Float.parseFloat(s[4]);int mask=Integer.parseInt(s[2]),parts=Integer.parseInt(s[3]);if(!Float.isFinite(a)||!Float.isFinite(b)||!Float.isFinite(angle)||!land(a,b)||mask<0||mask>7||parts<0||parts>Integer.bitCount(mask)||Math.abs(angle)>70)return;collected=mask;bridge=parts;x=targetX=walkable(a,b)?a:0;z=targetZ=walkable(a,b)?b:4;yaw=angle;rescue=1;moving=false;}catch(RuntimeException ignored){}}
}
