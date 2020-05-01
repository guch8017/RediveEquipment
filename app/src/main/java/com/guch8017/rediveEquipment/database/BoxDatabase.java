package com.guch8017.rediveEquipment.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.guch8017.rediveEquipment.database.module.DBBox;
import com.guch8017.rediveEquipment.database.module.DBCharacter;

import java.util.ArrayList;
import java.util.List;

public class BoxDatabase {
    private Context mContext;
    private SQLiteDatabase database;
    public static boolean dataModified = false;
    public BoxDatabase(Context context){
        mContext = context;
        UserDatabase userDatabase = UserDatabase.getInstance(context);
        database = userDatabase.getWritableDatabase();
    }

    public List<DBBox> getBoxList(){
        List<DBBox> boxes = new ArrayList<>();
        String sql = "SELECT * FROM BoxHeader";
        Cursor cursor = database.rawQuery(sql, new String[]{});
        while(cursor.moveToNext()){
            DBBox box = new DBBox();
            box.id = cursor.getInt(cursor.getColumnIndex("id"));
            box.image_id = cursor.getInt(cursor.getColumnIndex("image_id"));
            box.title = cursor.getString(cursor.getColumnIndex("title"));
            boxes.add(box);
        }
        cursor.close();
        return boxes;
    }

    public @Nullable DBBox getBox(int boxId){
        DBBox box = null;
        String sql = "SELECT * FROM BoxHeader WHERE id=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(boxId)});
        if(cursor.moveToNext()){
            box = new DBBox();
            box.id = cursor.getInt(cursor.getColumnIndex("id"));
            box.image_id = cursor.getInt(cursor.getColumnIndex("image_id"));
            box.title = cursor.getString(cursor.getColumnIndex("title"));
        }
        cursor.close();
        return box;
    }

    public List<DBCharacter> getCharacterList(int boxId){
        List<DBCharacter> characters = new ArrayList<>();
        String sql = "SELECT * FROM Box WHERE box_id=" + boxId;
        Cursor cursor = database.rawQuery(sql, new String[]{});
        while (cursor.moveToNext()){
            DBCharacter character = new DBCharacter();
            character.boxId = boxId;
            character.characterId = cursor.getInt(cursor.getColumnIndex("character_id"));
            character.currentEquip = cursor.getInt(cursor.getColumnIndex("current_equip"));
            character.currentRank = cursor.getInt(cursor.getColumnIndex("current_rank"));
            character.targetRank = cursor.getInt(cursor.getColumnIndex("target_rank"));
            character.targetEquip = cursor.getInt(cursor.getColumnIndex("target_equip"));
            characters.add(character);
        }
        cursor.close();
        return characters;
    }

    public DBCharacter getCharacter(int boxId, int unitId){
        DBCharacter character = null;
        String sql = "SELECT * FROM Box WHERE box_id=? AND character_id=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(boxId), String.valueOf(unitId)});
        if(cursor.moveToNext()){
            character = new DBCharacter();
            character.boxId = boxId;
            character.characterId = unitId;
            character.currentEquip = cursor.getInt(cursor.getColumnIndex("current_equip"));
            character.currentRank = cursor.getInt(cursor.getColumnIndex("current_rank"));
            character.targetRank = cursor.getInt(cursor.getColumnIndex("target_rank"));
            character.targetEquip = cursor.getInt(cursor.getColumnIndex("target_equip"));
        }
        cursor.close();
        return character;
    }

    public void deleteBox(int boxId){
        String sql = "DELETE FROM Box WHERE box_id="+boxId;
        String sql2 = "DELETE FROM BoxHeader WHERE id="+boxId;
        database.rawQuery(sql, new String[]{}).close();
        database.rawQuery(sql2, new String[]{}).close();
        dataModified = true;
    }

    public void addBox(){
        String sql = "INSERT INTO BoxHeader (image_id, title) VALUES (?, ?)";
        database.execSQL(sql, new String[]{"1", "NewBox"});
        //dataModified = true;
    }

    public void modifyBox(DBBox box){
        String sql = "UPDATE BoxHeader SET image_id=?, title=? WHERE id=?";
        database.execSQL(sql, new String[]{String.valueOf(box.image_id), box.title, String.valueOf(box.id)});
        dataModified = true;
    }

    public void addCharacter(int box_id, int character_id){
        String sql = "INSERT INTO Box (box_id, character_id, current_rank, current_equip, target_rank, target_equip) VALUES (?, ?, 1, 0, 1, 0)";
        database.execSQL(sql, new String[]{String.valueOf(box_id), String.valueOf(character_id)});
    }

    public void addCharacter(int box_id, DBCharacter character){
        String sql = "INSERT INTO Box (box_id, character_id, current_rank, current_equip, target_rank, target_equip) VALUES (?, ?, ?, ?, ?, ?)";
        database.execSQL(sql, new String[]{String.valueOf(box_id), String.valueOf(character.characterId), String.valueOf(character.currentRank),
        String.valueOf(character.currentEquip), String.valueOf(character.targetRank), String.valueOf(character.targetEquip)});
    }

    public void removeCharacter(int box_id, int character_id){
        String sql = "DELETE FROM Box WHERE box_id=? AND character_id=?";
        database.execSQL(sql, new String[]{String.valueOf(box_id), String.valueOf(character_id)});
    }

    public void modifyCharacter(DBCharacter character){
        String sql = "UPDATE Box SET current_rank=?, current_equip=?, target_rank=?, target_equip=? WHERE box_id=? AND character_id=?";
        database.execSQL(sql, new String[]{String.valueOf(character.currentRank), String.valueOf(character.currentEquip),
                String.valueOf(character.targetRank), String.valueOf(character.targetEquip),
                String.valueOf(character.boxId), String.valueOf(character.characterId)});
    }
}
