package com.guch8017.rediveEquipment.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDiskIOException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class DatabaseReflector {
    private SQLiteDatabase db;
    private Context mContext;

    public DatabaseReflector(Context context) throws SQLiteDiskIOException {
        mContext = context;
        Database database = Database.getInstance(context);
        try{
            db = database.getReadableDatabase();
        }catch (Exception e){
            e.printStackTrace();
            throw new SQLiteDiskIOException("Error: Can't open database");
        }
    }

    public void notisfyDatabaseChange(){
        if(db != null){
            db.close();
        }
        Database database = Database.getInstance(mContext);
        db = database.getReadableDatabase();
    }


    @Nullable
    public Object reflectClass(@NonNull String className, @NonNull String tableName){
        return reflectClass(className, tableName, null);
    }

    @Nullable
    public Object reflectClass(@NonNull String className, @NonNull String tableName,
                                    @Nullable String sqlLimitation){
        String sql = "SELECT * FROM " + tableName;
        if(sqlLimitation != null){
            sql += (" WHERE " + sqlLimitation);
        }
        List<Object> objects = new ArrayList<>();
        sql += ";";
        try {
            Class reflectClass = Class.forName(className);
            Field[] fields = reflectClass.getFields();
            Cursor cursor = db.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()){
                Object classObject = reflectClass.newInstance();
                for(Field field:fields) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        switch (field.getGenericType().toString()) {
                            case "int":
                                field.set(classObject, cursor.getInt(cursor.getColumnIndex(field.getName())));
                                break;
                            case "class java.lang.String":
                                field.set(classObject, cursor.getString(cursor.getColumnIndex(field.getName())));
                                break;
                            case "double":
                                field.set(classObject, cursor.getDouble(cursor.getColumnIndex(field.getName())));
                                break;
                            default:
                                Log.w("Reflector", "Unknown Field generic type: " + field.getGenericType());
                                break;
                        }
                    }
                }
                objects.add(classObject);
            }
            cursor.close();
            return objects;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
