package com.marcosdiez.ingressportalnavigator;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by Marcos on 12/26/13.
 */
public class PortalAddressLoader extends AsyncTask<Portal, Void, String> {
    TextView theTextView;
    public PortalAddressLoader(TextView theTextView){
        super();
        this.theTextView = theTextView;
    }

    @Override
    protected String doInBackground(Portal... portals) {
        return portals[0].getAddress();
    }

    protected void onPostExecute(String result) {
        theTextView.setText(result);
    }

}
