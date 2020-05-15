package com.guch8017.rediveEquipment.util;

import android.content.Context;
import android.util.Log;

import com.guch8017.rediveEquipment.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Constant {
    public static String equipImageUrl(int equipId){
        if(equipId < 100000){
            return "https://redive.estertion.win/icon/item/"+equipId+".webp";
        }else {
            return "https://redive.estertion.win/icon/equipment/" + equipId + ".webp";
        }
    }
    public static String unitImageUrl(int imageId){
        return "https://redive.estertion.win/icon/unit/"+imageId+".webp";
    }
    public static String unitImageUrl(int unitId, int star){

        final String prefix = "https://redive.estertion.win/icon/unit/";

        String postfix;
        if(unitId == 0){
            postfix = "000000.webp";
        }else if(unitId == 1){
            postfix = "000001.webp";
        }else {
            switch (star) {
                case 1:
                    postfix = unitId / 100 + "11.webp";
                    break;
                case 3:
                    postfix = unitId / 100 + "31.webp";
                    break;
                default:
                    postfix = "000001.webp";
                    break;
            }
        }
        return prefix + postfix;
    }

    public static String compliedDropDataFilepath(Context context){
        Log.w(TAG, "compliedDropDataFilepath: " + context.getFilesDir().toString());
        return context.getFilesDir().toString() + "/drop.data";
    }
    public static String compliedComposeDataFilepath(Context context){
        return context.getFilesDir().toString() + "/compose.data";
    }
    public static final String databaseVersionUrl = "https://redive.estertion.win/last_version_cn.json";
    public static final String databaseUrl = "https://redive.estertion.win/db/redive_cn.db.br";
    public final static DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .showImageOnLoading(R.drawable.ic_launcher_background).build();
    public static ImageLoaderConfiguration imageLoaderConfiguration(Context context){
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context)
            .memoryCacheExtraOptions(400,400)
            .diskCacheFileCount(1000).build();
        return configuration;
    }
}
