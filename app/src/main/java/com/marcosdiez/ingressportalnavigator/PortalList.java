package com.marcosdiez.ingressportalnavigator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Vector;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalList {
    private String TAG = "ING_PortalList";
    public Vector portals = new Vector(1000);


    public PortalList(Context context){
        loadPortalData(context);
    }

    private void loadPortalData(Context context) {
        int counter = 0;
        PortalsDbHelper p = new PortalsDbHelper(context);
        SQLiteDatabase portalsRo = p.getReadableDatabase();


        Cursor theCursor = portalsRo.query(PortalsDbHelper.PORTAL_DATA_TABLE_NAME,
                new String[]{"id", "guid", "title", "lat", "lng"},
                "title like ? ", new String[]{"%planta%"}, null, null, "title");

        Log.d(TAG,"Loading portals...");
        if (theCursor.moveToFirst()) {
            do {
                Portal myPortal = new Portal(theCursor);
                portals.add(myPortal);
                //Log.d(TAG, theCursor.getString(0));
                counter++;
            } while (theCursor.moveToNext());
        }
        Log.d(TAG,"We loaded "+ counter + " portals...");

        if (theCursor != null && !theCursor.isClosed()) {
            theCursor.close();
        }
    }
}
