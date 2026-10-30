package com.littlemakers.village.nativeapp;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.*;

public final class PlayerWallet {
 public static final class Item {
  public final String id,name,category;public final int cost,color;
  Item(String id,String name,String category,int cost,int color){this.id=id;this.name=name;this.category=category;this.cost=cost;this.color=color;}
 }
 public static final List<Item> SHOP=Collections.unmodifiableList(Arrays.asList(
  new Item("mint","Mint friend","skin",15,0xff58c9b4),new Item("peach","Peach friend","skin",20,0xffffa27c),new Item("sky","Sky friend","skin",25,0xff78b8ee),new Item("violet","Violet friend","skin",30,0xffb098ee),new Item("sunshine","Sunshine friend","skin",35,0xffebc56e),
  new Item("hat","Explorer hat","accessory",15,0xffe5b77e),new Item("bow","Friendly bow","accessory",20,0xffde8faa),new Item("star","Star badge","accessory",25,0xffebc56e)
 ));
 private final GameDatabase db;
 PlayerWallet(GameDatabase db){this.db=db;}
 /** Fixed first-clear reward for original creative activities. Atomic with the same wallet ledger. */
 public boolean creativeCompletion(String activity){if(!activity.matches("(story|texture|dots|shape|adventure|guardian|balance|robot|discovery|letters|island|friends|tangram|maker|school|cinema)-[0-9]+"))throw new IllegalArgumentException("Unknown creative completion");String key="creative-"+activity;SQLiteDatabase sql=db.getWritableDatabase();sql.beginTransaction();try{ContentValues v=new ContentValues();v.put("game_id",key);v.put("completed_at",System.currentTimeMillis());v.put("coins",5);v.put("stars",1);boolean first=sql.insertWithOnConflict("completion",null,v,SQLiteDatabase.CONFLICT_IGNORE)!=-1;if(first){sql.execSQL("UPDATE wallet SET coins=coins+5, stars=stars+1 WHERE id=1");ContentValues l=new ContentValues();l.put("event_key","win:"+key);l.put("coin_delta",5);l.put("star_delta",1);l.put("created_at",System.currentTimeMillis());sql.insertOrThrow("ledger",null,l);}sql.setTransactionSuccessful();return first;}finally{sql.endTransaction();}}
 public int coins(){return balance("coins");}public int stars(){return balance("stars");}
 private int balance(String name){try(Cursor c=db.getReadableDatabase().rawQuery("SELECT "+name+" FROM wallet WHERE id=1",null)){c.moveToFirst();return c.getInt(0);}}
 public boolean owns(String id){try(Cursor c=db.getReadableDatabase().rawQuery("SELECT 1 FROM inventory WHERE item_id=?",new String[]{id})){return c.moveToFirst();}}
 public String equipped(String category){try(Cursor c=db.getReadableDatabase().rawQuery("SELECT item_id FROM equipped WHERE category=?",new String[]{category})){return c.moveToFirst()?c.getString(0):"";}}
 public Item item(String id){for(Item i:SHOP)if(i.id.equals(id))return i;throw new IllegalArgumentException("Unknown shop item");}
 public String purchase(String id){
  Item item=item(id);SQLiteDatabase sql=db.getWritableDatabase();sql.beginTransaction();
  try {
   if(owns(id)){equip(sql,item);sql.setTransactionSuccessful();return "Equipped "+item.name+".";}
   if(coins()<item.cost)return "You need "+item.cost+" coins. Practice freely and explore a new game when ready.";
   ContentValues inventory=new ContentValues();inventory.put("item_id",id);inventory.put("purchased_at",System.currentTimeMillis());sql.insertOrThrow("inventory",null,inventory);
   sql.execSQL("UPDATE wallet SET coins=coins-? WHERE id=1",new Object[]{item.cost});
   ContentValues ledger=new ContentValues();ledger.put("event_key","purchase:"+id);ledger.put("coin_delta",-item.cost);ledger.put("star_delta",0);ledger.put("created_at",System.currentTimeMillis());sql.insertOrThrow("ledger",null,ledger);
   equip(sql,item);sql.setTransactionSuccessful();return "Your "+item.name+" is ready.";
  }finally{sql.endTransaction();}
 }
 private void equip(SQLiteDatabase sql,Item item){ContentValues v=new ContentValues();v.put("category",item.category);v.put("item_id",item.id);sql.insertWithOnConflict("equipped",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
 public int skinColor(){String id=equipped("skin");return id.isEmpty()?0xff58c9b4:item(id).color;}
}
