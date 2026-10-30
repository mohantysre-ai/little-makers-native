package com.littlemakers.village.nativeapp;
import java.util.ArrayList;

/** Original anatomical surface meshes in story coordinates. No imported characters or texture downloads. */
final class WildlifeGeometry {
 static final int RABBIT=0,FOX=1,BEAR=2,STRIDE=10;
 private static final float[][]CACHE=new float[3][];
 private final ArrayList<Float> data=new ArrayList<>();
 private void v(float x,float y,float z,float nx,float ny,float nz,int color,float skin){float[]values={x,y,z,nx,ny,nz,((color>>16)&255)/255f,((color>>8)&255)/255f,(color&255)/255f,skin};for(float f:values)data.add(f);}
 private void oval(float x,float y,float z,float sx,float sy,float sz,float angle,int color,float skin){
  double c=Math.cos(angle),s=Math.sin(angle);int[]corners={0,1,2,0,2,3};
  for(int a=0;a<10;a++)for(int b=0;b<16;b++)for(int corner:corners){double lat=Math.PI*(a+(corner==1||corner==2?1:0))/10,lon=2*Math.PI*(b+(corner>=2?1:0))/16;float ux=(float)(Math.sin(lat)*Math.cos(lon)),uy=(float)Math.cos(lat),uz=(float)(Math.sin(lat)*Math.sin(lon));float px=ux*sx,py=uy*sy,nx=ux/sx,ny=uy/sy,nz=uz/sz;float length=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);nx/=length;ny/=length;nz/=length;v(x+(float)(px*c-py*s),y+(float)(px*s+py*c),z+uz*sz,(float)(nx*c-ny*s),(float)(nx*s+ny*c),nz,color,skin);}
 }
 private void eye(float x,float y,float z){oval(x,y,z,.024f,.029f,.017f,0,0x191c17,1);oval(x-.006f,y-.009f,z+.015f,.006f,.008f,.003f,0,0xe7e8d9,1);}
 private void ear(float x,float y,float z,float w,float h,int color){
  float[][]p={{x-w,y,z-.035f},{x+w,y,z-.035f},{x,y-h,z},{x-w,y,z+.035f},{x+w,y,z+.035f}};
  int[][]faces={{0,1,2},{4,3,2},{3,0,2},{1,4,2},{0,3,4},{0,4,1}};
  for(int[]face:faces){float[]a=p[face[0]],b=p[face[1]],c=p[face[2]];float ux=b[0]-a[0],uy=b[1]-a[1],uz=b[2]-a[2],vx=c[0]-a[0],vy=c[1]-a[1],vz=c[2]-a[2];float nx=uy*vz-uz*vy,ny=uz*vx-ux*vz,nz=ux*vy-uy*vx,l=(float)Math.sqrt(nx*nx+ny*ny+nz*nz);for(int id:face)v(p[id][0],p[id][1],p[id][2],nx/l,ny/l,nz/l,color,0);}
 }
 static synchronized float[]build(int species){if(species<0||species>2)throw new IllegalArgumentException("Unknown wildlife species");if(CACHE[species]!=null)return CACHE[species];WildlifeGeometry m=new WildlifeGeometry();if(species==RABBIT)m.rabbit();else if(species==FOX)m.fox();else m.bear();float[]out=new float[m.data.size()];for(int i=0;i<out.length;i++)out[i]=m.data.get(i);CACHE[species]=out;return out;}
 private void rabbit(){int coat=0x8f8979,light=0xc0b9a5;
  oval(-.11f,.10f,0,.46f,.28f,.235f,-.13f,coat,0);oval(-.31f,.16f,0,.27f,.31f,.25f,0,coat,0);
  for(int side:new int[]{-1,1}){float z=side*.15f;oval(-.20f,.43f,z,.25f,.075f,.10f,0,coat,0);oval(.22f,.29f,z,.065f,.20f,.07f,-.08f,coat,0);oval(.30f,.46f,z,.15f,.05f,.075f,0,light,0);}
  oval(.22f,-.12f,0,.17f,.26f,.17f,-.30f,coat,0);oval(.37f,-.24f,0,.22f,.225f,.18f,-.12f,coat,0);oval(.53f,-.16f,.025f,.15f,.095f,.13f,0,light,0);
  for(int side:new int[]{-1,1}){float x=.28f+side*.10f,z=side*.055f;oval(x,-.61f,z,.061f,.31f,.045f,side*.12f,coat,0);oval(x,-.63f,z+.034f,.030f,.23f,.010f,side*.12f,0xa58d82,2);eye(.43f,-.28f,side*.166f);}
  oval(.65f,-.16f,.025f,.022f,.017f,.038f,0,0x735c55,1);oval(-.58f,.13f,0,.11f,.12f,.11f,0,light,0);
  for(int i=0;i<3;i++)oval(.66f,-.14f+i*.018f,.13f,.15f,.0025f,.0025f,(i-1)*.15f,0xc8c0ac,2);
 }
 private void fox(){int coat=0x9f6035,cream=0xd6c6a7,dark=0x493d31;
  oval(-.07f,.04f,0,.52f,.245f,.18f,-.06f,coat,0);oval(.24f,-.16f,0,.19f,.27f,.17f,-.25f,coat,0);
  for(int side:new int[]{-1,1})for(int front:new int[]{-1,1}){float x=front<0?-.35f:.26f,z=side*.12f;oval(x,.26f,z,.068f,.22f,.065f,front*.12f,coat,0);oval(x+.03f,.46f,z,.11f,.05f,.065f,0,dark,0);}
  oval(.40f,-.32f,0,.22f,.195f,.155f,-.08f,coat,0);oval(.58f,-.24f,0,.22f,.085f,.085f,.06f,cream,0);oval(.77f,-.225f,0,.032f,.025f,.038f,0,dark,1);
  for(int side:new int[]{-1,1}){ear(.31f+side*.12f,-.43f,side*.06f,.078f,.24f,coat);ear(.31f+side*.12f,-.45f,side*.06f+.034f,.045f,.16f,dark);eye(.47f,-.35f,side*.145f);}
  oval(.29f,-.03f,.14f,.14f,.23f,.07f,-.20f,cream,0);oval(-.76f,.14f,-.03f,.40f,.13f,.135f,-.35f,coat,0);oval(-1.05f,.25f,-.03f,.14f,.10f,.105f,-.35f,cream,0);
 }
 private void bear(){int coat=0x66533e,light=0x9b8766;
  oval(-.10f,.01f,0,.60f,.34f,.30f,0,coat,0);oval(.28f,-.15f,0,.29f,.34f,.26f,0,coat,0);oval(.48f,-.30f,0,.27f,.25f,.22f,0,coat,0);oval(.68f,-.21f,0,.19f,.12f,.13f,0,light,0);oval(.84f,-.22f,0,.055f,.04f,.07f,0,0x292c23,1);
  for(int side:new int[]{-1,1}){oval(.34f,-.53f,side*.14f,.085f,.09f,.075f,0,coat,0);eye(.55f,-.36f,side*.195f);for(int front:new int[]{-1,1}){float x=front<0?-.43f:.27f;oval(x,.29f,side*.20f,.13f,.26f,.13f,0,coat,0);oval(x+.05f,.49f,side*.20f,.18f,.06f,.13f,0,0x493e2e,0);}}
 }
 private WildlifeGeometry(){}
}
