package com.littlemakers.village.nativeapp;
import java.util.*;
/** Immutable display list. Coordinates also define the existing accessible input layout. */
final class SceneFrame {
 static final int PANEL=0,SPHERE=1,CONE=2,BLOCK=3,CHARACTER=4,MODEL=5,ACTOR=11;
 static final class Node {
  final int type,color,model;final float x,y,w,h,z;final boolean animate;final float rotation;
  Node(int type,float x,float y,float w,float h,float z,int color,int model,boolean animate){this(type,x,y,w,h,z,color,model,animate,0);}
  Node(int type,float x,float y,float w,float h,float z,int color,int model,boolean animate,float rotation){if(!Float.isFinite(rotation))throw new IllegalArgumentException("Invalid rotation");this.rotation=rotation;if(!Float.isFinite(x)||!Float.isFinite(y)||!Float.isFinite(w)||!Float.isFinite(h)||!Float.isFinite(z)||w<=0||h<=0)throw new IllegalArgumentException("Invalid scene node");this.type=type;this.x=x;this.y=y;this.w=w;this.h=h;this.z=z;this.color=color;this.model=model;this.animate=animate;}
 }
 final Node[]nodes;final int background;final float height;final String environment;final boolean won;final int branch,cast,event;
 SceneFrame(List<Node>nodes,int background,float height,String environment,boolean won){this(nodes,background,height,environment,won,-1);}
 SceneFrame(List<Node>nodes,int background,float height,String environment,boolean won,int branch){this(nodes,background,height,environment,won,branch,-1,0);}
 SceneFrame(List<Node>nodes,int background,float height,String environment,boolean won,int branch,int cast,int event){if(cast< -1||cast>7||event<0)throw new IllegalArgumentException("Invalid cast or event");this.cast=cast;this.event=event;if(!Float.isFinite(height)||height<=0||environment==null)throw new IllegalArgumentException("Invalid scene viewport");this.branch=branch;this.nodes=nodes.toArray(new Node[0]);this.background=background;this.height=height;this.environment=environment;this.won=won;}
 static Node panel(float left,float top,float right,float bottom,int color){return new Node(PANEL,(left+right)/2,(top+bottom)/2,Math.max(1,right-left),Math.max(1,bottom-top),0,color,0,false);}
 static Node shape(float x,float y,float radius,int kind,int color){return new Node(1+Math.floorMod(kind,3),x,y,radius,radius,28,color,0,true);}
 static Node model(float x,float y,float size,int model,int color,boolean animate){return new Node(MODEL,x,y,size,size,25,color,model,animate);}
}
