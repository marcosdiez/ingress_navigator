package com.marcosdiez.ingressportalnavigator;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Marcos on 12/25/13.
 */
public class JsonLoader {
    public static final String TAG = "JsonLoader";
    public static String readRawTextFile(int resId)
    {
        InputStream inputStream = Globals.getContext().getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    PortalsDbHelper mPortalDbHelper;
    SQLiteDatabase portalsRw;
    public void load(String jsonString){

        try {
            Log.e(TAG, "Parsing JSON");
            JSONArray jArray = new JSONArray(jsonString);
            Log.d(TAG, "Loaded " + jArray.length() + " entries.");
            loadPortals(jArray);
            Log.e(TAG, "Portal:" + ((JSONObject) jArray.get(0)).getString("title"));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
    }

    private void loadPortals(JSONArray jArray) throws JSONException {
        mPortalDbHelper = new PortalsDbHelper();
        portalsRw = mPortalDbHelper.getWritableDatabase();
        int addedPortals=0;
        for( int i = 0 ; i < jArray.length() ; i++ ){
            JSONObject portalJson = (JSONObject) jArray.get(i);
            long result = addToDatabase(portalJson);
            if(result>0){
                addedPortals++;
            }
        }
        Log.d(TAG, "Loaded " + addedPortals + " new portals out of the " + jArray.length() +  " on the file.");
        portalsRw.close();
        mPortalDbHelper.close();
    }

    private long addToDatabase(JSONObject portalJson) {
        try{
            ContentValues values = new ContentValues();
            addStringToDb(portalJson, values, "guid");
            addStringToDb(portalJson, values, "title");
            addStringToDb(portalJson, values, "imageUrl");
            addDoubleToDb(portalJson, values, "lat");
            addDoubleToDb(portalJson, values, "lng");

            long returnValue = 0;
            try{
                returnValue = portalsRw.insert(PortalsDbHelper.PORTAL_DATA_TABLE_NAME, null, values);
            }catch(android.database.sqlite.SQLiteConstraintException e){
                // we don't care if the GUID already exists.
            }
            return returnValue;
        }catch (JSONException e){
            Log.d(TAG, e.toString());
        }
        return 0;
    }

    private void addStringToDb(JSONObject portalJson, ContentValues values, String fieldName) throws JSONException {
        values.put(fieldName, portalJson.getString(fieldName));
    }

    private void addDoubleToDb(JSONObject portalJson, ContentValues values, String fieldName) throws JSONException {
        values.put(fieldName, portalJson.getDouble(fieldName));
    }

}
