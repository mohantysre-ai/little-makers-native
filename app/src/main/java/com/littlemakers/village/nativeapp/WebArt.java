package com.littlemakers.village.nativeapp;
import android.content.Context;import android.graphics.*;import android.util.LruCache;import org.json.*;import java.io.*;
/** Local rasterizations of the exact web SVG assets. Fully offline assets. */
final class WebArt {
 final Context context;final JSONArray pictures,stories;private final LruCache<String,Bitmap>cache=new LruCache<String,Bitmap>(12*1024*1024){protected int sizeOf(String k,Bitmap b){return b.getByteCount();}};
 WebArt(Context c){context=c;pictures=json("catalog.json");stories=json("stories.json");}
 private JSONArray json(String name){try(InputStream in=context.getAssets().open("webart/"+name);ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[]b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);return new JSONArray(out.toString("UTF-8"));}catch(Exception e){throw new IllegalStateException("Bundled content missing: "+name,e);}}
 Bitmap image(String path){Bitmap b=cache.get(path);if(b==null){try(InputStream in=context.getAssets().open(path)){b=BitmapFactory.decodeStream(in);if(b!=null)cache.put(path,b);}catch(IOException ignored){}}return b;}
 String picture(int i,boolean line){return pictures.optJSONObject(Math.floorMod(i,pictures.length())).optString(line?"nativeLine":"nativeColor");}
 String title(int i){return pictures.optJSONObject(Math.floorMod(i,pictures.length())).optString("title");}
 String named(String name){for(int i=0;i<pictures.length();i++)if(title(i).equalsIgnoreCase(name))return picture(i,false);return picture(0,false);}
 String original(String path){if(path.endsWith(".png"))return "webart/story/"+path.substring(path.lastIndexOf('/')+1);for(int i=0;i<pictures.length();i++){JSONObject p=pictures.optJSONObject(i);if(path.endsWith(p.optString("colorPath")))return picture(i,false);}return named(path.contains("fence")?"Wood":path.contains("moon")?"Full Moon":"Rabbit");}
 void clear(){cache.evictAll();}
}
