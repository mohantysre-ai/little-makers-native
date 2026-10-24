package com.littlemakers.village.nativeapp;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class ProgressManager {
 private final GameDatabase db;
 ProgressManager(GameDatabase db){this.db=db;}
 public boolean completed(String id){try(Cursor c=db.getReadableDatabase().rawQuery("SELECT 1 FROM completion WHERE game_id=?",new String[]{id})){return c.moveToFirst();}}
 public int completedCount(){try(Cursor c=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM completion",null)){c.moveToFirst();return c.getInt(0);}}
 public byte[] load(String id){GameCatalog.find(id);try(Cursor c=db.getReadableDatabase().rawQuery("SELECT state FROM progress WHERE game_id=?",new String[]{id})){return c.moveToFirst()?c.getBlob(0):null;}}
 public void save(GameRules rules){GameCatalog.find(rules.game.id);writeState(db.getWritableDatabase(),rules);}
 private void writeState(SQLiteDatabase sql,GameRules rules){ContentValues values=new ContentValues();values.put("game_id",rules.game.id);values.put("state",rules.saveState());values.put("updated_at",System.currentTimeMillis());sql.insertWithOnConflict("progress",null,values,SQLiteDatabase.CONFLICT_REPLACE);}
 /** Atomic and idempotent: one first-clear reward per module, even after restart/retry. */
 public boolean complete(GameRules rules){
  GameCatalog.Game game=GameCatalog.find(rules.game.id);if(!rules.won)throw new IllegalArgumentException("Cannot reward an unfinished game");
  SQLiteDatabase sql=db.getWritableDatabase();sql.beginTransaction();try {
   ContentValues v=new ContentValues();v.put("game_id",game.id);v.put("completed_at",System.currentTimeMillis());v.put("coins",game.rewardCoins());v.put("stars",1);
   boolean first=sql.insertWithOnConflict("completion",null,v,SQLiteDatabase.CONFLICT_IGNORE)!=-1;
   if(first){sql.execSQL("UPDATE wallet SET coins=coins+?, stars=stars+1 WHERE id=1",new Object[]{game.rewardCoins()});ContentValues ledger=new ContentValues();ledger.put("event_key","win:"+game.id);ledger.put("coin_delta",game.rewardCoins());ledger.put("star_delta",1);ledger.put("created_at",System.currentTimeMillis());sql.insertOrThrow("ledger",null,ledger);}
   writeState(sql,rules);sql.setTransactionSuccessful();return first;
  }finally{sql.endTransaction();}
 }
}
