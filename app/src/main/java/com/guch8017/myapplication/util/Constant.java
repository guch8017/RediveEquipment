package com.guch8017.myapplication.util;

public class Constant {
    public static final String equipImageUrl = "https://redive.estertion.win/icon/equipment/%d.webp";
    public static String unitImageUrl(int imageId){
        return "https://redive.estertion.win/icon/unit/"+imageId+".webp";
    }
    public static String unitImageUrl(int unitId, int star){
        final String prefix = "https://redive.estertion.win/icon/unit/";
        String postfix;
        switch (star){
            case 1:
                postfix = String.valueOf(unitId/100) + "11.webp";
                break;
            case 3:
                postfix = String.valueOf(unitId/100) + "31.webp";
                break;
            default:
                postfix = "000001.webp";
                break;
        }
        return prefix + postfix;
    }
    public static final String databaseVersionUrl = "https://redive.estertion.win/last_version_jp.json";
    public static final String databaseUrl = "https://redive.estertion.win/db/redive_tw.db.br";
}
