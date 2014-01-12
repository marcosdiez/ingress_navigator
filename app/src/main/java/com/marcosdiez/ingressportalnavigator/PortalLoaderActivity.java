package com.marcosdiez.ingressportalnavigator;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class PortalLoaderActivity extends Activity {
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

    public class LoadingPortals extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            PortalList.getPortalList();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // call second Activity
            Intent i = new Intent(me , MainActivity.class);
            startActivity(i);
            super.onPostExecute(result);
        }


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
