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
    public double lastDistance = 0; // meters
    boolean like = false;

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
        if(this.address != null && this.address.equals("")){
            this.address = null;
        }
        this.like = (theCursor.getInt(7) == 1); // boolean
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

    public double GetDistance(Portal otherPortal){
        return GetDistance(otherPortal.lat , otherPortal.lng);
    }

    public double GetDistance(double fromLat, double fromLng){
        float result[] = { 0 };
        Location.distanceBetween(fromLat, fromLng, lat, lng, result);
        lastDistance =result[0];
        return lastDistance;
    }

    public Boolean hasAddress(){
        return address != null;
    }

    public String getAddress(){
        if(address != null){
            return address;
        }
        address = GpsStuff.getMyGpsStuff().locationToAddress(lat, lng);
        if(address == null){
            return "";
        }else{
            saveAddressToDb();
            return address;
        }
    }

    private void saveAddressToDb() {
        Log.d(TAG, "Saving portal address to DB");
        SQLiteDatabase portalsRw =  new PortalsDbHelper().getWritableDatabase();

        String query = "UPDATE " + PortalsDbHelper.PORTAL_DATA_TABLE_NAME +
                " SET address = ? " +
                " WHERE id = ? ";

        String args[] = { address , (id + "") };
        Cursor cu = portalsRw.rawQuery(query, args );
        cu.moveToFirst();
        cu.close();
        portalsRw.close();
    }

    public boolean getLike(){
        return like;
    }

    public void setLike(boolean newLike){
        if(newLike == like ){
            return;
        }
        Log.d(TAG, "Saving portal " + title + " like to DB:" + newLike);
        SQLiteDatabase portalsRw =  new PortalsDbHelper().getWritableDatabase();

        String query = "UPDATE " + PortalsDbHelper.PORTAL_DATA_TABLE_NAME +
                " SET like = ? " +
                " WHERE id = ? ";

        int likeValue = newLike ? 1 : 0;

        String args[] = { likeValue + "" , (id + "") };
        Cursor cu = portalsRw.rawQuery(query, args );
        cu.moveToFirst();
        cu.close();
        portalsRw.close();
        this.like=newLike;
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
        return Globals.getPublicWritableFolder() + "/images/";
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

    public String getGoogleMapsUrl(){
        return GoogleMapsUrl.getSinglePointUrl(lat,lng);
    }

    public String getIntelUrl(){
        return "http://www.ingress.com/intel?ll=" + lat + "," + lng + "&pll="+ lat + "," + lng;
    }

    public String getDescription(){
        String br = "\n";
        String fixedAddress = address != null ? address : "";

        return
                title + br + br +
                fixedAddress + br + br +
                "Picture: " + imageUrl + br + br +
                "Map: " + getGoogleMapsUrl() + br + br +
                "Intel URL: " + getIntelUrl();
    }

    public String getKmlPart(){
        String fixedAddress = address != null ? address : "";
        return "        <Placemark>\n" +
                "                <name>"+title+"</name>\n" +
                "                <description><![CDATA[\n" + title + "<br>" +
                "                <a href=\"" + getGoogleMapsUrl() + "\">Open Google Maps</a>\n" +
                "                <a href=\"" + getIntelUrl() + "\">Open Intel/IITC</a>\n" +
                fixedAddress + "\n" +
                "                <img src=\""+imageUrl+"\">\n" +
                "                ]]></description>\n" +
                "                <Point>\n" +
                "                        <coordinates>"+lng+","+lat+",0</coordinates>\n" +
                "                </Point>\n" +
                "        </Placemark>\n";

    }


}
