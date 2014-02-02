package com.marcosdiez.ingressportalnavigator;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PortalLoaderActivity extends Activity {
    private final static String TAG = "PortalLoaderActivity";
    Activity me = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.setContext(this);
        setContentView(R.layout.activity_portal_loader);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        new LoadingPortals().execute();
    }

    private class LoadingPortals extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            PortalList.getPortalList();
            processIntent();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // call second Activity
            Intent i = new Intent(me , MainActivity.class);
            startActivity(i);
            super.onPostExecute(result);
            finish();
        }
    }

    private void processIntent() {
        Intent intent = getIntent();
        if(intent == null){
            return;
        }
        String data = intent.getDataString();
        if( data == null ){
            return;
        }
        Log.d(TAG, "Received URL:" + data);
        String jsonData = DownloadToMemory(data);
        Log.d(TAG, "Data downloaded");
        if(jsonData!=null){
            PortalList.getPortalList().loadExtraJson(jsonData);
        }
    }

    String DownloadToMemory(String theURL) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(theURL);
        HttpResponse response = null;
        StringBuilder output = new StringBuilder();
        try {
            response = client.execute(request);
            BufferedReader rd = new BufferedReader
                    (new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                output.append(line);
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_portal_loader, container, false);
            return rootView;
        }
    }

}
