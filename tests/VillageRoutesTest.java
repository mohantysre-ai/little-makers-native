package com.littlemakers.village.nativeapp;
public final class VillageRoutesTest {
 public static void main(String[]args){for(String s:new String[]{"world","explore","village","world-explorer","adventure"})if(!VillageRoutes.matches(s))throw new AssertionError(s);for(String s:new String[]{null,"playroom","parents","privacy","fish","clay","shape",""})if(VillageRoutes.matches(s))throw new AssertionError(s);System.out.println("PASS: five explicit village aliases never fall through to the legacy Home list; gameplay and parent destinations remain distinct");}
}
