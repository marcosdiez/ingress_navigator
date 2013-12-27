package com.marcosdiez.ingressportalnavigator;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by Marcos on 12/26/13.
 */
public class PortalAddressLoader extends AsyncTask<Integer, Void, String> {
    TextView theTextView;
    Portal thePortal;
    public PortalAddressLoader(TextView theTextView, Portal thePortal){
        super();
        this.theTextView = theTextView;
        this.thePortal = thePortal;
    }


    public void loadAddress(){
        if(thePortal.hasAddress()){
            theTextView.setText(thePortal.getAddress());
        }else{
            execute(0);
        }
    }

    @Override
    protected String doInBackground(Integer... whatever) {
        return thePortal.getAddress();
    }

    protected void onPostExecute(String result) {
        theTextView.setText(result);
    }

}
