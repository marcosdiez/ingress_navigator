package com.marcosdiez.ingressportalnavigator;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalList {
    private final static String TAG = "ING_PortalList";
    private static int initalNumPortals = 1900;

    private ArrayList<Portal> portalsByName = new ArrayList<Portal>(initalNumPortals);
    private LinkedHashMap<Integer,Portal> portalById = new LinkedHashMap<Integer, Portal>(initalNumPortals);
    private ArrayList<Portal> portalsByLocation = new ArrayList<Portal>(initalNumPortals);

    private final PortalsDbHelper mPortalDbHelper;
    private static PortalList thePortalList=null;

    public static synchronized PortalList getPortalList(){
        if(thePortalList == null ){
            thePortalList = new PortalList();
        }
        return thePortalList;
    }

    public int size(){
        return portalsByName.size();
    }

    public Portal getPortalById(int id){
        return portalById.get(id);
    }

    public Portal getPortalByDistance(int pos){
        return portalsByLocation.get(pos);
    }

    public Portal getPortalByName(int pos){
        return portalsByName.get(pos);
    }

    private PortalList(){
        mPortalDbHelper = new PortalsDbHelper();
        loadDataFromJsonIfNecessary();
        loadPortalData();
        Toast.makeText(Globals.getContext(),"Loaded " + portalsByName.size() + " portals", Toast.LENGTH_LONG).show();
    }

    private void loadDataFromJsonIfNecessary(){
        SharedPreferences settings = Globals.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        int myJsonVersion = 5;
        Log.d(TAG, "APK json data version: "  + myJsonVersion + "/" + settings.getInt("jsonVersion" , 0 ));
        if( settings.getInt("jsonVersion" , 0 ) < myJsonVersion ){
            Log.d(TAG, "Loading portals from JSON");
            loadDataFromJson();
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putInt("jsonVersion", myJsonVersion);
            editor1.commit();
        }
    }

    private void loadDataFromJson() {
        String theJson = JsonLoader.readRawTextFile(R.raw.sample);
        new JsonLoader().load(theJson);
    }
    private void loadPortalData() {
        int counter = 0;
        SQLiteDatabase portalsRo = mPortalDbHelper.getReadableDatabase();

        Cursor theCursor = portalsRo.query(PortalsDbHelper.PORTAL_DATA_TABLE_NAME,
                new String[]{"id", "guid", "title", "imageUrl", "lat", "lng" , "address" , "like" },
                null , null,
                null, null, "title");

        Log.d(TAG,"Loading portals...");
        if (theCursor.moveToFirst()) {
            do {
                Portal myPortal = new Portal(theCursor);

                myPortal.positionByName =counter;
                portalById.put(myPortal.id,myPortal);
                portalsByName.add(myPortal);
                portalsByLocation.add(myPortal);
                counter++;
            } while (theCursor.moveToNext());
        }
        Log.d(TAG,"We loaded "+ counter + " portals...");

        if (theCursor != null && !theCursor.isClosed()) {
            theCursor.close();
        }
        portalsRo.close();
        sortPortalsByDistance();
    }

    public void sortPortalsByDistance(){
        Location theLocation = GpsStuff.getMyGpsStuff().GetNewLocation();
        double lat = 0;
        double lng = 0;

        if(theLocation!=null){
            lat = theLocation.getLatitude();
            lng = theLocation.getLongitude();
        }
        for(Portal p : portalsByLocation){
            p.GetDistance(lat,lng);
        }
        Collections.sort(portalsByLocation);
        int counter=0;
        for(Portal p : portalsByLocation){
            p.positionByDistance=counter++;
        }
    }


    ///////////////////////// SEARCH BLOAT
    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(BaseColumns._ID, "id AS " +
                BaseColumns._ID);

        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "title AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "'' AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);

        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "id AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "id AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);

        return map;
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     *
     * @param rowId id of word to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getWord(String rowId, String[] columns) {
        String selection = "id = ?";
        String[] selectionArgs = new String[] {rowId};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE rowid = <rowId>
         */
    }

    /**
     * Returns a Cursor over all words that match the given query
     *
     * @param query The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getWordMatches(String query, String[] columns) {
        String selection = DictionaryProvider.KEY_WORD + " like ?";
        String[] selectionArgs = new String[] {"%"+query+"%"};

        return query(selection, selectionArgs, columns);

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <KEY_WORD> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the word column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the DictionaryProvider when queries are made.
         * - This can be revised to also search the definition text with FTS3 by changing
         *   the selection clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
    }

    /**
     * Performs a database query.
     * @param selection The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PortalsDbHelper.PORTAL_DATA_TABLE_NAME);
        builder.setProjectionMap(mColumnMap);

        Cursor cursor = builder.query(mPortalDbHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    public void uncheckAllPortals() {
        for( Portal thePortal: portalsByName){
            thePortal.setLike(false);
        }
    }

    public String makeTextOfLikedPortals(){
        StringBuilder output = new StringBuilder();
        String br = "\n";

        GoogleMapsUrl multiUrl = new GoogleMapsUrl();

        output.append("List of Portals" + br + br+ br);
        for( Portal thePortal: portalsByName){
            if(thePortal.getLike()){
                multiUrl.addTarget(thePortal);
                output.append(thePortal.getDescription());
                output.append(br);
                output.append(br);
            }
        }

        output.append("You can navigate to all portals though your PC's google maps following this URL:\n" + multiUrl.getTargetUrl() + "\n\n" );

        output.append("The attached KML file can be opened with Google Earth");

        return output.toString();
    }

    public void makePoligon(StringBuilder output,  ArrayList<Portal> likedPortals){
        output.append("<Placemark>\n");
        output.append("\t<name>The Field</name>\n");
        output.append("\t\t<Polygon>\n");
        output.append("\t\t\t<tessellate>1</tessellate>\n");
        output.append("\t\t\t<outerBoundaryIs>\n");
        output.append("\t\t\t<LinearRing>\n");
        output.append("\t\t\t<coordinates>\n");

        for( Portal thePortal : likedPortals){
            addASinglePortal(output, thePortal);
        }

        addASinglePortal(output, likedPortals.get(0));

//        -48.95054950770835,-21.30333748672975,0 -50.04440067124863,-20.46703351296742,0 -51.68390301748836,-21.28419204803828,0 -48.95054950770835,-21.30333748672975,0
        output.append("\t\t\t</coordinates>\n");
        output.append("\t\t\t</LinearRing>\n");
        output.append("\t\t\t</outerBoundaryIs>\n");
        output.append("\t\t\t</Polygon>\n");
        output.append("\t\t\t</Placemark>\n");
    }

    private void addASinglePortal(StringBuilder output, Portal thePortal) {
        output.append(thePortal.lng);
        output.append(",");
        output.append(thePortal.lat);
        output.append(",0 ");
    }


    public String makeKmlOfLikedPortals(){
        int counter=0;
        StringBuilder output = new StringBuilder();
        output.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "        <Document>\n" +
                "                <Folder>\n" +
                "                <name>Placemarks</name>\n" +
                "                <open>0</open>\n");

        ArrayList<Portal> likedPortals = new ArrayList<Portal>();

        for( Portal thePortal: portalsByName){
            if(thePortal.getLike()){
                output.append(thePortal.getKmlPart());
                likedPortals.add(thePortal);
                counter++;
            }
        }
        if(likedPortals.size() == 3){
            makePoligon(output,likedPortals);
        }

        output.append("                        </Folder>\n" +
                "        </Document>\n" +
                "</kml>\n");

        if(counter == 0){
            return null;
        }
        return output.toString();
    }
}
