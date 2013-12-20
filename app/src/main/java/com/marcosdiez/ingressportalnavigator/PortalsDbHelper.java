package com.marcosdiez.ingressportalnavigator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalsDbHelper extends SQLiteOpenHelper {
    private final Context mHelperContext;
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "portals";
    public static final String PORTAL_DATA_TABLE_NAME = "PortalData";
    private static final String PORTAL_DATA_TABLE_CREATE =

            "CREATE TABLE '"+ PORTAL_DATA_TABLE_NAME +  "'" +
            "(" +
            "'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "'guid' TEXT NOT NULL UNIQUE ,"+
            "'title' TEXT NOT NULL,"+
            "'imageUrl' TEXT,"+
            "'imageDownloaded' INTEGER NOT NULL default 0 ,"+
            "'lat' REAL NOT NULL default '0',"+
            "'lng' REAL NOT NULL default '0',"+
            "'like' INTEGER NOT NULL default 0 ,"+
            "'target' INTEGER NOT NULL default 0 ,"+
            "'hasKey' INTEGER NOT NULL default 0"+
            ");";


    PortalsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PORTAL_DATA_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
