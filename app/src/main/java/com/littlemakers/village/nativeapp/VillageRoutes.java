package com.littlemakers.village.nativeapp;
/** Explicit aliases: a village link must never silently fall through to the legacy Home list. */
final class VillageRoutes {
 static boolean matches(String destination){return "world".equals(destination)||"explore".equals(destination)||"village".equals(destination)||"world-explorer".equals(destination)||"adventure".equals(destination);}
 private VillageRoutes(){}
}
