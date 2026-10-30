package com.littlemakers.village.nativeapp;
/** Explicit allowlist works when application ID and generated resource namespace differ. */
final class CinematicResources {
 static int video(String clip){switch(clip){case "moon_arrival":return R.raw.moon_arrival;case "moon_crater":return R.raw.moon_crater;case "moon_sunlight":return R.raw.moon_sunlight;case "moon_departure":return R.raw.moon_departure;default:return 0;}}
 static int poster(String clip){switch(clip){case "moon_arrival":return R.drawable.moon_arrival;case "moon_crater":return R.drawable.moon_crater;case "moon_sunlight":return R.drawable.moon_sunlight;case "moon_departure":return R.drawable.moon_departure;default:return 0;}}
 private CinematicResources(){}
}
