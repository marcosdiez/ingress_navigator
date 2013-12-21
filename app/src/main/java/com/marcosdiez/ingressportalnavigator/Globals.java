package com.marcosdiez.ingressportalnavigator;

import android.content.Context;

/**
 * Created by Marcos on 12/21/13.
 */
public class Globals {
    private static Context context=null;
    public static void setContext(Context context){
        Globals.context=context;
    }
    public static Context getContext(){
        if(context == null){
            throw new NullPointerException("contex");
        }
        return context;
    }
    public static boolean isContextNull(){
        return context==null;
    }
}
