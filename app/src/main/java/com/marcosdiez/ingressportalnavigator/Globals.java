package com.marcosdiez.ingressportalnavigator;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Marcos on 12/21/13.
 */
public class Globals {
    public static final int portalSampleJsonVersion = 12;

    private static Context context=null;
    public static void setContext(Context context){
        Globals.context=context;
    }
    public static Context getContext(){
        if(context == null){
            throw new NullPointerException("Context");
        }
        return context;
    }
    public static boolean isContextNull(){
        return context==null;
    }

    public static String getPublicWritableFolder(){
        return Environment.getExternalStorageDirectory() + "/Android/data/" +
                Globals.getContext().getPackageName();
    }
}
