package com.guch8017.myapplication.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {

    static private String db_name = "box.db";
    static private UserDatabase sInstance;

    private UserDatabase(Context context){
        super(context, context.getDatabasePath(db_name).getAbsolutePath(), null, 1);
    }

    public static UserDatabase getInstance(Context context){
        if(sInstance == null){
            sInstance = new UserDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE Box(" +
                "box_id INTEGER NOT NULL," +
                "character_id INTEGER NOT NULL," +
                "current_rank INTEGER NOT NULL," +
                "current_equip INTEGER NOT NULL," +
                "target_rank INTEGER NOT NULL," +
                "target_equip INTEGER NOT NULL);";
        Cursor cursor = db.rawQuery(sql, new String[]{});
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
