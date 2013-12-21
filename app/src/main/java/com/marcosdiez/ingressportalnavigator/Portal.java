package com.marcosdiez.ingressportalnavigator;

import android.database.Cursor;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Marcos on 12/19/13.
 */
public class Portal  implements Comparable<Portal>{

    public int id;
    public String guid;
    public String title;
    public double lat;
    public double lng;
    public int positionByName;
    public int positionByDistance;
    public double lastDistance = 0;


    private static String TAG =  "ING_Portal";

    public Portal(Cursor theCursor) {
        this.id = theCursor.getInt(0);
        this.guid= theCursor.getString(1);
        this.title = theCursor.getString(2);
        this.lat = theCursor.getDouble(3);
        this.lng = theCursor.getDouble(4);
        this.positionByName = -1;
        this.positionByDistance = -1;
    }

    public double getLastDistance(){
        return lastDistance;
    }

    public double GetDistance(){
        lastDistance = GpsStuff.getMyGpsStuff().distanceFromHere(lat,lng);
        return lastDistance;
    }

    public double GetDistance(double fromLat, double fromLng){
        float result[] = { 0 };
        Location.distanceBetween(fromLat, fromLng, lat, lng, result);
        lastDistance =result[0];
        return lastDistance;
    }


    public String GetImageFile(){
        // /storage/emulated/0/Android/data/com.marcosdiez.ingressportalnavigator/images/5fe1f3cd994c4651afc179d43b29b943.16.jpg
        String path = Environment.getExternalStorageDirectory() + "/Android/data/" +
                Globals.getContext().getPackageName() + "/images/" + guid + ".jpg";
        Log.d(TAG, path);
        File imageFile = new File(path);
        if( imageFile.isFile() && imageFile.canRead()){
            return path;
        }
        return null;
    }

    public int compareTo(Portal otherPortal){
        double otherLastDistance = otherPortal.getLastDistance();

        if(otherLastDistance < lastDistance){
            return 1;
        }
        if(otherLastDistance > lastDistance){
            return -1;
        }
        return 0;
    }
}
