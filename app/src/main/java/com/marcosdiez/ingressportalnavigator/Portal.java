package com.marcosdiez.ingressportalnavigator;

import android.database.Cursor;

/**
 * Created by Marcos on 12/19/13.
 */
public class Portal {

    public int id;
    public String guid;
    public String title;
    public double lat;
    public double lng;

    public Portal(Cursor theCursor){
        this.id = theCursor.getInt(0);
        this.guid= theCursor.getString(1);
        this.title = theCursor.getString(2);
        this.lat = theCursor.getDouble(3);
        this.lng = theCursor.getDouble(4);
    }
}
