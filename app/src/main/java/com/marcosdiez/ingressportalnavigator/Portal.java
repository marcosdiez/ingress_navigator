package com.marcosdiez.ingressportalnavigator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public String imageUrl;
    public double lat;
    public double lng;
    public int positionByName;
    public int positionByDistance;
    public double lastDistance = 0;
    private String address=null;


    private static String TAG =  "ING_Portal";

    public Portal(Cursor theCursor) {
        this.id = theCursor.getInt(0);
        this.guid= theCursor.getString(1);
        this.title = theCursor.getString(2);
        this.imageUrl = theCursor.getString(3);
        this.lat = theCursor.getDouble(4);
        this.lng = theCursor.getDouble(5);
        this.address = theCursor.getString(6);
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


    public String getAddress(){
        if(this.address == null){
            address = GpsStuff.getMyGpsStuff().locationToAddress(lat, lng);
            if(address != null){
                saveAddressToDb();
            }
        }
        if(address==null){
            return "";
        }
        return address;
    }

    private void saveAddressToDb() {
        Log.d(TAG, "Saving portal address to DB");
        SQLiteDatabase portalsRw =  new PortalsDbHelper().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("address", address);

        String query = "UPDATE " + PortalsDbHelper.PORTAL_DATA_TABLE_NAME +
                " SET address = ? " +
                " WHERE id = ? ";

        String args[] = { address , (id + "") };
        Cursor cu = portalsRw.rawQuery(query, args );
        cu.moveToFirst();
        cu.close();
        portalsRw.close();
    }


    public String GetImageFile(){
        // /storage/emulated/0/Android/data/com.marcosdiez.ingressportalnavigator/images/5fe1f3cd994c4651afc179d43b29b943.16.jpg
        String expectedPath = getExpectedImageFile();

        Log.d(TAG, expectedPath);
        File imageFile = new File(expectedPath);
        if( imageFile.isFile() && imageFile.canRead()){
            return expectedPath;
        }
        return null;
    }

    public String getExpectedImageFile() {
        String expectedDir = getExpectedImageFolder();
        String expectedFilename = guid + ".jpg";
        return expectedDir + expectedFilename;
    }

    public String getExpectedImageFolder() {
        return Environment.getExternalStorageDirectory() + "/Android/data/" +
                Globals.getContext().getPackageName() + "/images/";
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
