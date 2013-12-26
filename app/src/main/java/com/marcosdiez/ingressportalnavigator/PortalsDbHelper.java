package com.marcosdiez.ingressportalnavigator;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalsDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "PortalsDbHelper";
    private static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "portals";
    public static final String PORTAL_DATA_TABLE_NAME = "PortalData";
    private static final String PORTAL_DATA_TABLE_CREATE =

            "CREATE TABLE '"+ PORTAL_DATA_TABLE_NAME +  "'" +
            "(" +
            "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "'guid' TEXT NOT NULL UNIQUE ,"+
            "'title' TEXT NOT NULL,"+
            "'imageUrl' TEXT,"+
            "'lat' REAL NOT NULL default '0',"+
            "'lng' REAL NOT NULL default '0',"+
            "'like' INTEGER NOT NULL default 0 ,"+
            "'target' INTEGER NOT NULL default 0 ,"+
            "'hasKey' INTEGER NOT NULL default 0 , "+
            "'address' TEXT default NULL"+
            ");";


    PortalsDbHelper() {
        super(Globals.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PORTAL_DATA_TABLE_CREATE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,  int oldVersion, int newVersion ) {
        Log.d(TAG, "Upgrading SQLite DB from " + oldVersion + " to " + newVersion);
        int currentVersion = oldVersion;


        if(currentVersion < 10){
            currentVersion = 10;
            Log.d(TAG, "Upgrading SQLite DB to " + 10);
            sqLiteDatabase.execSQL("ALTER TABLE PortalData ADD COLUMN 'address' TEXT default NULL;");
        }


        Log.d(TAG, "Upgrading SQLite DB to " + DATABASE_VERSION);
        sqLiteDatabase.execSQL("UPDATE PortalData SET 'address' = null WHERE address = '';");

        Log.d(TAG, "Upgrading SQLite DB completed...");
    }
}
