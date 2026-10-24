package com.littlemakers.village.nativeapp;
/** Android-independent navigation decision, exercised for legacy and predictive Back. */
final class NavigationPolicy {
 enum Destination { CATALOG, HOME, EXIT }
 static Destination back(String screen,String gameId){if("game".equals(screen)&&gameId!=null)return Destination.CATALOG;if("home".equals(screen))return Destination.EXIT;return Destination.HOME;}
}
