package com.marcosdiez.ingressportalnavigator;

import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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
    public static Activity thisActivity;

    SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;

        setContentView(R.layout.activity_main);

        // load Data
        thePortalList = PortalList.getPortalList(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        handleIntent(getIntent());
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
                int tabId = thePortalList.portalHashMap.get(portalId).tabId;
                if(tabId > 0 ){
                    mViewPager.setCurrentItem(tabId, false);
                }
            }
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            //showResults(query);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(this.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        //searchView.setSuggestionsAdapter(
        //searchView.setQueryHint(Html.fromHtml("<font color = #00aaaa>elefante</font>"));
        //searchView.set


        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));



//        mSearchView = (SearchView) searchItem.getActionView();
//        setupSearchView(searchItem);
        return true;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }
        SearchManager searchManager = (SearchManager) getSystemService(this.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }

            mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
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
            case R.id.menu_map:
                openPortalMap();
                return true;
            default:
                return true;
        }
    }

    private void openPortalMap() {
        mViewPager = (ViewPager) findViewById(R.id.pager);
        Portal p = (Portal) thePortalList.portalsByName.get(mViewPager.getCurrentItem());
        openGpsUrl(p.lat + "", p.lng+ "");
    }
    void openGpsUrl(String latitude, String longitude){
        String theURL = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude ;
        Log.d(TAG, "Opening location:" + theURL);
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(theURL));
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
            return thePortalList.portalsByName.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ((Portal)thePortalList.portalsByName.get(position)).title;
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
            TextView txt_portal_guid  = (TextView) rootView.findViewById(R.id.portal_guid);
            TextView txt_portal_position = (TextView) rootView.findViewById(R.id.portal_position);
            TextView txt_portal_distance = (TextView) rootView.findViewById(R.id.portal_distance);


            int portalListID = getArguments().getInt(ARG_SECTION_NUMBER);

            Portal thePortal = (Portal) thePortalList.portalsByName.get(portalListID);

            txt_portal_title.setText(thePortal.title);
            txt_portal_guid.setText(thePortal.guid);
            txt_portal_position.setText("GPS: " + thePortal.lat  + "," + thePortal.lng);


            String distance = GpsStuff.getMyGpsStuff(thisActivity).distanceFromHereStr(thePortal.lat, thePortal.lng);
            txt_portal_distance.setText("Distance: " + distance  );

            String theImage = thePortal.GetImageFile();
            Drawable theImageDrawable = Drawable.createFromPath(theImage);
            image_portal.setImageDrawable(theImageDrawable);

            return rootView;
        }
    }

}
