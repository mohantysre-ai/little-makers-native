package com.littlemakers.village.nativeapp;
/** Keep scrolling available in tap games; reserve drag gestures for tactile exercises. */
final class GesturePolicy {
 static boolean capture(GameCatalog.Kind kind,String action){return kind==GameCatalog.Kind.TRACE||((kind==GameCatalog.Kind.SHAPES||kind==GameCatalog.Kind.COLORS)&&("choose".equals(action)||"match".equals(action)));}
}
