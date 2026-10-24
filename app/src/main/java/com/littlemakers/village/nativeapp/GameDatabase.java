package com.littlemakers.village.nativeapp;
import android.content.Context;
import android.database.sqlite.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

final class GameDatabase extends SQLiteOpenHelper {
 GameDatabase(Context context){super(context,"makers_native.db",null,1);}
 @Override public void onCreate(SQLiteDatabase db){
  try(InputStream in=getContextStream();ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
   byte[] b=new byte[2048];int n;while((n=in.read(b))!=-1)bytes.write(b,0,n);
   for(String sql:new String(bytes.toByteArray(),StandardCharsets.UTF_8).split(";"))if(!sql.trim().isEmpty())db.execSQL(sql);
  }catch(IOException e){throw new IllegalStateException("Cannot create game database",e);}
 }
 void resetLocalProgress(){SQLiteDatabase sql=getWritableDatabase();sql.beginTransaction();try{try(InputStream in=app.getResources().openRawResource(R.raw.reset);ByteArrayOutputStream bytes=new ByteArrayOutputStream()){byte[]b=new byte[2048];int n;while((n=in.read(b))!=-1)bytes.write(b,0,n);for(String statement:new String(bytes.toByteArray(),StandardCharsets.UTF_8).split(";"))if(!statement.trim().isEmpty())sql.execSQL(statement);}catch(IOException e){throw new IllegalStateException("Cannot reset local progress",e);}sql.setTransactionSuccessful();}finally{sql.endTransaction();}}
 private Context app;
 static GameDatabase create(Context c){GameDatabase db=new GameDatabase(c);db.app=c.getApplicationContext();return db;}
 private InputStream getContextStream(){return app.getResources().openRawResource(R.raw.schema);}
 @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){throw new IllegalStateException("A non-destructive database migration is required.");}
}
