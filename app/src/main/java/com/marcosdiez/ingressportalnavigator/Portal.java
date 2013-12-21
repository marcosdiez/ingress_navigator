package com.marcosdiez.ingressportalnavigator;

import android.database.Cursor;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Marcos on 12/19/13.
 */
public class Portal implements Comparable<Portal> {

    public int id;
    public String guid;
    public String title;
    public double lat;
    public double lng;
    public int tabId;
    public float distanceSquare = 0;

    private static String TAG =  "ING_Portal";

    public Portal(Cursor theCursor){
        this.id = theCursor.getInt(0);
        this.guid= theCursor.getString(1);
        this.title = theCursor.getString(2);
        this.lat = theCursor.getDouble(3);
        this.lng = theCursor.getDouble(4);
        this.tabId = -1;
    }

    public String GetImageFile(){
        // /storage/emulated/0/Android/data/com.marcosdiez.ingressportalnavigator/images/5fe1f3cd994c4651afc179d43b29b943.16.jpg
        String path = Environment.getExternalStorageDirectory() + "/Android/data/" +
                MainActivity.thisActivity.getPackageName() + "/images/" + guid + ".jpg";
        Log.d(TAG, path);
        File imageFile = new File(path);
        if( imageFile.isFile() && imageFile.canRead()){
            return path;
        }
        return null;
    }

    double getDistanceSquare(double otherLat, double otherLng ){
        double deltaLat = this.lat -  otherLat;
        double deltaLng = this.lng -  otherLng;

        return deltaLat*deltaLat + deltaLng*deltaLng;
    }

    public int compareTo(Portal other){
        if( this.distanceSquare == other.distanceSquare ){
            return 0;
        }
        if( this.distanceSquare > other.distanceSquare){
            return 1;
        }
        return -1;
    }
}
