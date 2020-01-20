package com.guch8017.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {
    // DB ReadOnly !
    static private String db_name = "database.db";

    public Database(Context context){
        super(context, context.getDatabasePath(db_name).getAbsolutePath(), null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}