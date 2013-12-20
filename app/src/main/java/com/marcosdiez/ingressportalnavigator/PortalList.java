package com.marcosdiez.ingressportalnavigator;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Marcos on 12/19/13.
 */
public class PortalList {

    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;


    private final static String TAG = "ING_PortalList";
    public Vector portals = new Vector(1900);
    private final PortalsDbHelper mPortalDbHelper;

    public PortalList(Context context){
        mPortalDbHelper = new PortalsDbHelper(context);
        loadPortalData(context, "igreja");
        Toast.makeText(context,"Loaded " + portals.size() + " portals", Toast.LENGTH_LONG).show();
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

    ///////////////////////// SEARCH BLOAT



    private static final HashMap<String,String> mColumnMap = buildColumnMap();

    private static HashMap<String,String> buildColumnMap() {
        //public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
        // public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

        HashMap<String,String> map = new HashMap<String,String>();
        map.put(BaseColumns._ID, "id AS " + BaseColumns._ID);

        map.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "title AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        map.put(SearchManager.SUGGEST_COLUMN_TEXT_2, "guid AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);

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
        String selection = "rowid = ?";
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
        String selection = KEY_WORD + " like ?";
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


}
