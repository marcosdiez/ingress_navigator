package com.marcosdiez.ingressportalnavigator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalList {
    private final static String TAG = "ING_PortalList";
    public Vector portals = new Vector(1900);


    public PortalList(Context context){
        loadPortalData(context, "igreja");
        Toast.makeText(context,"Loaded " + portals.size() + " portals", Toast.LENGTH_LONG).show();
    }

    public static void searchPortals(String title, Context context ){
        PortalsDbHelper p = new PortalsDbHelper(context);
        SQLiteDatabase portalsRo = p.getReadableDatabase();
        Cursor theCursor = portalsRo.query(PortalsDbHelper.PORTAL_DATA_TABLE_NAME,
                new String[]{"id", "guid", "title", "lat", "lng"},
                "title like ? ", new String[]{"%" + title + "%"},
                null, null, "title");

        Log.d(TAG, "-------------");
        if (theCursor.moveToFirst()) {
            do {
                Portal myPortal = new Portal(theCursor);
                Log.d(TAG, myPortal.title);
                //Log.d(TAG, theCursor.getString(0));
            } while (theCursor.moveToNext());
        }
        Log.d(TAG, "-------------");

        if (theCursor != null && !theCursor.isClosed()) {
            theCursor.close();
        }
        portalsRo.close();
    }

    public void loadPortalData(Context context, String titleHint) {
        int counter = 0;
        PortalsDbHelper p = new PortalsDbHelper(context);
        SQLiteDatabase portalsRo = p.getReadableDatabase();

        Cursor theCursor = portalsRo.query(PortalsDbHelper.PORTAL_DATA_TABLE_NAME,
                new String[]{"id", "guid", "title", "lat", "lng"},
                "title like ? ", new String[]{"%" + titleHint + "%"},
                null, null, "title");

        Log.d(TAG,"Loading portals...");
        if (theCursor.moveToFirst()) {
            do {
                Portal myPortal = new Portal(theCursor);
                portals.add(myPortal);
                counter++;
            } while (theCursor.moveToNext());
        }
        Log.d(TAG,"We loaded "+ counter + " portals...");

        if (theCursor != null && !theCursor.isClosed()) {
            theCursor.close();
        }
        portalsRo.close();
    }
}
