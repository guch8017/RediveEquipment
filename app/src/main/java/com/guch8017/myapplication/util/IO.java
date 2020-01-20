package com.guch8017.myapplication.util;

import android.util.Log;

import java.io.File;

public class IO {
    public static boolean isFileExist(String filePath){
        try {
            File file = new File(filePath);
            if(file.exists()){
                Log.i("File Exists",filePath + " exists.");
                return true;
            }else {
                Log.i("File Exists",filePath + " doesn't exist.");
                return false;
            }
        }catch (Exception e){
            Log.i("File Exists",filePath + " doesn't exist.");
            return false;
        }
    }

    public static boolean deleteFile(String filePath){
        try{
            File file = new File(filePath);
            if(file.exists() && file.isFile()){
                Log.i("Delete File", filePath + " exists. Trying to delete");
                return file.delete();
            }
            else return false;
        }catch (Exception e){
            return false;
        }
    }
}