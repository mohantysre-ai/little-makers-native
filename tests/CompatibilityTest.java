package com.littlemakers.village.nativeapp;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
public final class CompatibilityTest {public static void main(String[]args)throws Exception{int tested=0;for(String line:Files.readAllLines(Paths.get(args[0]),StandardCharsets.UTF_8)){String[]p=line.split("\t");byte[]old=Base64.getDecoder().decode(p[1]);GameRules rules=new GameRules(GameCatalog.find(p[0]));rules.restore(old);if(!Arrays.equals(old,rules.saveState()))throw new AssertionError("Legacy state changed: "+p[0]);if(!rules.won){RulesTest.solve(rules);if(!rules.won)throw new AssertionError("Legacy puzzle changed");}tested++;}if(tested!=400)throw new AssertionError("Fixture coverage");System.out.println("PASS: 400 saved-state fixtures from native 2.0.0 load unchanged (all original 200 IDs).");}}
