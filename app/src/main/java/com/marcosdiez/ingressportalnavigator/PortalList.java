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
    private final PortalsDbHelper mPortalDbHelper;

    public PortalList(Context context){
        mPortalDbHelper = new PortalsDbHelper(context);
        loadPortalData(context, "igreja");
        Toast.makeText(context,"Loaded " + portals.size() + " portals", Toast.LENGTH_LONG).show();
    }

    public  void searchPortals(String title, Context context ){
        SQLiteDatabase portalsRo = mPortalDbHelper.getReadableDatabase();
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
        SQLiteDatabase portalsRo = mPortalDbHelper.getReadableDatabase();

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
