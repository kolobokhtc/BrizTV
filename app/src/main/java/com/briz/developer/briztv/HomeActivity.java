package com.briz.developer.briztv;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Класс активности-контейнера фрагментов приложения
 */
public class HomeActivity extends ActionBarActivity implements
                NavigationDrawerFragment.NavigationDrawerCallbacks, SearchView.OnQueryTextListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    SearchView searchView;

    //private StalkerClient stalkerClient;
    StalkerLoader APILoader;

    private AllChannelsFragment allChannelsFragment;

    TextView tvSectionContent;
    //ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StalkerClient stalkerClient;
        stalkerClient = getIntent().getParcelableExtra(StalkerClient.class.getCanonicalName());
        Log.d(TAG, "stalkerClient imported: " + stalkerClient.getAccessToken());

        APILoader = new StalkerLoader(getApplicationContext(), stalkerClient);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        tvSectionContent = (TextView) findViewById(R.id.section_content);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /*progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_wait));
        progressDialog.setCancelable(false);*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments


            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();

    }

    public void onSectionAttached(int number) {
        //Fragment fragment = null;
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                showChannelFragment();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);

                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    /**
     * Метод вывода фрагмента показа каналов ТВ
     */
    public void showChannelFragment(){

        FragmentManager fragmentManager = getSupportFragmentManager();

        allChannelsFragment = (AllChannelsFragment) fragmentManager.findFragmentByTag("channels");



        if (allChannelsFragment == null){

            allChannelsFragment = new AllChannelsFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.container, allChannelsFragment, "channels")
                    .commit();

            allChannelsFragment.setAPILoader(APILoader);
            allChannelsFragment.getChannels();

            Log.d(TAG, "Hash code allChannel " + allChannelsFragment.hashCode());

        }



    }


    /**
     * Метод восстановления экшн-бара
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();

            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

            searchView.setOnQueryTextListener(this);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        allChannelsFragment.ApplyFilter(s.toString());
        Log.d(TAG, "Search String is " + s.toString() + " " + allChannelsFragment.hashCode());
        return true;

    }

    /**
     * Метод показа активности настроек приложения
     */
    private void showSettings() {

        Intent prefIntent = new Intent(getApplicationContext(), BrizTVSettingsActivity.class);
        startActivity(prefIntent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings || id == R.id.actions_settings) {

            this.showSettings();

            return true;
        }

        if (item.getItemId() == R.id.action_example) {

            allChannelsFragment.startChooseGenres();

            return true;

        }


        return super.onOptionsItemSelected(item);
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
            return inflater.inflate(R.layout.fragment_home, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((HomeActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();



        //if (searchView != null) allChannelsFragment.setAPILoader(APILoader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) searchView.setOnQueryTextListener(this);
    }
}
