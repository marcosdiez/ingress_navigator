package com.marcosdiez.ingressportalnavigator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity implements ActionBar.TabListener, SearchView.OnQueryTextListener {
    private final static String TAG = "ING_MainActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    static PortalList thePortalList;
    static SeekBar seek_portals;
    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals.setContext(this);
        setContentView(R.layout.activity_main);

        // load Data
        thePortalList = PortalList.getPortalList();
        Toast.makeText(Globals.getContext(),"Loaded " + thePortalList.size() + " portals", Toast.LENGTH_LONG).show();

        prepareSeekBar();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        handleIntent(getIntent());
    }



    private void prepareSeekBar() {
        seek_portals = (SeekBar) findViewById(R.id.seek_portals);
        seek_portals.setMax(thePortalList.size());
        seek_portals.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekBarPosition, boolean fromTouch) {
                if(fromTouch){
                    LoadPortalByPosition(seekBarPosition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // Because this activity has set launchMode="singleTop", the system calls this method
        // to deliver the intent if this activity is currently the foreground activity when
        // invoked again (when the user executes a search from this activity, we don't create
        // a new instance of this activity, so the system delivers the search intent here)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if( intent.getDataString() != null){
            Log.d(TAG, intent.getDataString());
        }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String searchInfo = intent.getDataString();
            if(searchInfo!=null){
                String portalIdStr = searchInfo.substring(searchInfo.lastIndexOf('/')+1);
                int portalId = Integer.parseInt(portalIdStr);
                LoadPortalById(portalId);
            }
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            //showResults(query);
        }
    }

    private void LoadPortalByPosition(int portalPosition){
        mViewPager.setCurrentItem(portalPosition, false);
    }

    private void LoadPortalById(int portalId) {
        Portal thePortal = thePortalList.getPortalById(portalId);
        int tabId;
        if(sortByName){
            tabId = thePortal.positionByName;
        }else{
            tabId = thePortal.positionByDistance;
        }
        LoadPortalByPosition(tabId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        prepareSortMenu(menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "Query = " + newText);
        //PortalList.searchPortals(newText, this);

        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "Query = " + query + " : submitted");
        //PortalList.searchPortals(query, this);

        // mStatusView.setText("Query = " + query + " : submitted");
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        switch(itemId){
            case R.id.menu_uncheck_all_portals:
                thePortalList.uncheckAllPortals();
                CheckBox checkbox_like = (CheckBox) findViewById(R.id.checkbox_export);
                checkbox_like.setChecked(false);
                return true;
//            case R.id.menu_export_portals:
//                GoogleEarth.exportCheckedPortals(this);
//                return true;
            case R.id.menu_open_checked_portals_with_google_earth:
                GoogleEarth.showExportDialog(this);
                return true;
            case R.id.open_intel_url:
                openUrl(getCurrentPortal().getIntelUrl());
                return true;
            case R.id.menu_map:
                openUrl(getCurrentPortal().getGoogleMapsUrl());
                return true;
            case R.id.menu_share_intel_url:
                menuShare(getCurrentPortal().getIntelUrl(), getResources().getString(R.string.menu_share_intel_url));
                return true;
            case R.id.menu_share_googlemaps_url:
                menuShare(getCurrentPortal().getGoogleMapsUrl(), getResources().getString(R.string.menu_share_googlemaps_url));
                return true;
            default:
                return true;
        }
    }

    private void prepareSortMenu(Menu menu){
        if(menu!=null){
            final MenuItem menuSortByGps =  menu.findItem(R.id.menu_portal_sort_by_gps);
            final MenuItem menuSortByDistanceFromThisPortal =  menu.findItem(R.id.menu_portal_sort_distance_from_this_portal);
            final MenuItem menuSortByName = menu.findItem(R.id.menu_portal_sort_name);

            if(menuSortByDistanceFromThisPortal != null){
                menuSortByDistanceFromThisPortal.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "menuSortByDistanceFromThisPortal");
                        Portal currentPortal = getCurrentPortal();

                        GpsStuff.getMyGpsStuff().setLocationManual(currentPortal.lat, currentPortal.lng);
                        sortByName=false;
                        thePortalList.sortPortalsByDistance();

                        menuItem.setChecked(true);
                        LoadPortalById(currentPortal.id);
                        return true;
                    }
                });
            }


            if(menuSortByGps != null){
                menuSortByGps.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "menuSortByGps");
                        Portal currentPortal = getCurrentPortal();

                        GpsStuff.getMyGpsStuff().setLocationToGps();
                        sortByName=false;

                        thePortalList.sortPortalsByDistance();

                        menuItem.setChecked(true);
                        LoadPortalById(currentPortal.id);
                        return true;
                    }
                });
            }
            if(menuSortByName != null){
                menuSortByName.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Log.d(TAG, "menuSortByName");
                        Portal currentPortal = getCurrentPortal();

                        sortByName = true;

                        menuItem.setChecked(true);
                        LoadPortalById(currentPortal.id);
                        return true;
                    }
                });
            }
        }
    }

    private static boolean sortByName = false;

    private Portal getCurrentPortal(){
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        Portal p = getPortalByScreenPosition(mViewPager.getCurrentItem());
        return p;
    }


    private static Portal getPortalByScreenPosition(int pos){
        if(sortByName){
            return thePortalList.getPortalByName(pos);
        }else{
            return thePortalList.getPortalByDistance(pos);
        }
    }




    private void menuShare(String theUrl, String shareTitle){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, theUrl);
        startActivity(Intent.createChooser(sharingIntent, shareTitle));
    }

    private void openUrl(String theUrl) {
        Log.d(TAG, "Opening location:" + theUrl);
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(theUrl));
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return thePortalList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getPortalByScreenPosition(position).title;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ImageView image_portal = (ImageView) rootView.findViewById(R.id.image_portal);
            TextView txt_portal_title = (TextView) rootView.findViewById(R.id.portal_title);
            TextView txt_portal_address  = (TextView) rootView.findViewById(R.id.portal_address);
            TextView txt_portal_position = (TextView) rootView.findViewById(R.id.portal_position);
            TextView txt_portal_distance = (TextView) rootView.findViewById(R.id.portal_distance);
            ProgressBar loading_spinner = (ProgressBar) rootView.findViewById(R.id.loading_spinner);
            CheckBox checkbox_like = (CheckBox) rootView.findViewById(R.id.checkbox_export);

            int portalListID = getArguments().getInt(ARG_SECTION_NUMBER);

            final Portal thePortal  = getPortalByScreenPosition(portalListID);

            if(sortByName){
                seek_portals.setProgress(thePortal.positionByName);
            }else{
                seek_portals.setProgress(thePortal.positionByDistance);
            }

            txt_portal_title.setText(thePortal.title);
            txt_portal_address.setText("");


            checkbox_like.setChecked(thePortal.getLike());
            checkbox_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkbox_like = (CheckBox) view;
                    thePortal.setLike(checkbox_like.isChecked());
                }
            });

            // txt_portal_guid.setText(thePortal.guid);
            txt_portal_position.setText(thePortal.lat  + "," + thePortal.lng);

            String distance = GpsStuff.getMyGpsStuff().distanceFromHereStr(thePortal.lat, thePortal.lng);
            txt_portal_distance.setText("Distance: " + distance  );

            new PortalImageLoader(image_portal, loading_spinner, thePortal).loadImage();
            new PortalAddressLoader(txt_portal_address, thePortal).loadAddress();

            return rootView;
        }
    }
}
