package com.marcosdiez.ingressportalnavigator;

import android.database.Cursor;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Marcos on 12/19/13.
 */
public class Portal {

    public int id;
    public String guid;
    public String title;
    public double lat;
    public double lng;

    private static String TAG =  "ING_Portal";

    public Portal(Cursor theCursor){
        this.id = theCursor.getInt(0);
        this.guid= theCursor.getString(1);
        this.title = theCursor.getString(2);
        this.lat = theCursor.getDouble(3);
        this.lng = theCursor.getDouble(4);
    }

    public String GetImageFile(){
        //String path = Environment.getExternalStorageDirectory() + "/images/" + guid + ".jpg";

        String path = "/sdcard/Android/data/com.marcosdiez.ingressportalnavigator/images/" + guid + ".jpg";

        Log.d(TAG, Environment.getDataDirectory().getAbsolutePath());
        Log.d(TAG, Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d(TAG, Environment.getRootDirectory().getAbsolutePath());
        Log.d(TAG, Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES).getAbsolutePath());
        Log.d(TAG, path);

        File imageFile = new File(path);
        if( imageFile.isFile() && imageFile.canRead()){
            Log.d(TAG, "we are returning the path");
            return path;
        }
        return null;
    }
}
