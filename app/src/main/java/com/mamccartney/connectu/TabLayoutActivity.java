package com.mamccartney.connectu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mamccartney.connectu.DB.DBHelper;
import com.mamccartney.connectu.DB.FireDBHelper;
import com.mamccartney.connectu.DB.UserDataListener;
import com.mamccartney.connectu.Model.User;
import com.mamccartney.connectu.Model.UserData;

import java.util.Arrays;

/**
 * new class TabLayoutActivity extends AppCompatActivity
 *     set up tab layout in toolbar (first with text, then with icon)
 *     set up view pager adapter for tab fragments
 *     set up onTabSelected listener and method
 *
 * new class TabPagerAdapter extends FragmentPagerAdapter
 *     Fragment getItem(int position), returns class Tab#Fragment()
 *     int getCount(), for view pager adapter
 *
 * new class Tab2Fragment extends Fragment, Profile
 * new class Tab3Fragment extends Fragment, Map
 * new class Tab4Fragment extends Fragment, Ride
 *
 * add TabLayout in activity_tab_layout.xml, tab_layout, as part of toolbar
 * add ViewPager in activity_tab_layout.xml, pager
 *
 * new ConstraintLayout fragment_tab2.xml with TextView/EditText/Button
 * new ConstraintLayout fragment_tab3.xml with MapView
 * new ConstraintLayout fragment_tab4.xml with ListView
 *
 * https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout
 */

public class TabLayoutActivity extends AppCompatActivity
    implements UserDataListener {

    private static final String TAG = "TabLayoutActivity";
    private Toolbar toolbar;
    private Context context;

    private DBHelper DB;
    private FireDBHelper FB;
    private User userApp = new User();
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ------");

        setContentView(R.layout.activity_tab_layout);
        context = getApplicationContext();
        DB = new DBHelper(context);
        FB = new FireDBHelper(context);

        // get passed userApp information
        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
            userApp.setEmail(extras.getString("userEmail"));
            userApp.setSignInId(extras.getString("userSignInId"));
            userApp.setFullName(extras.getString("userFull"));
            userApp.setFirstName(extras.getString("userFirst"));
            userApp.setLastName(extras.getString("userLast"));
//        }
        // debug log check...
        Log.d(TAG, "onCreate: " + userApp.toString());
        FB.addUserDataListener(this);
        FB.findUserData(userApp.getSignInId());

        // set toolbar title
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("ConnectU - " + userApp.getFullName());

        // Add TabLayout to toolbar (activity_tab_layout.xml)
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

//        // Text labeled tabs
//        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
//        tabLayout.addTab(tabLayout.newTab().setText("Map"));
//        tabLayout.addTab(tabLayout.newTab().setText("Ride"));

        // Icon labeled tabs   https://material.io/icons/
        tabLayout.addTab(tabLayout.newTab()  // Profile
            .setIcon(R.drawable.ic_person_white_24dp));
        tabLayout.addTab(tabLayout.newTab()  // Map
                .setIcon(android.R.drawable.ic_dialog_map));
        tabLayout.addTab(tabLayout.newTab()  // Ride
            .setIcon(R.drawable.ic_directions_car_white_24dp));

        // https://developer.android.com/training/animation/screen-slide.html
        // https://developer.android.com/reference/android/support/v4/view/ViewPager.html

        // void setOffscreenPageLimit(int limit)   This setting defaults to 1.
        // Set the number of pages that should be retained to either side of the current
        // page in the view hierarchy in an idle state. Pages beyond this limit will be
        // recreated from the adapter when needed.  This means...

        // if Tab1 loads/displays, Tab2 loads in background, Tab3 destroyed/detached
        // if Tab3 loads/displays, Tab2 loads in background if needed, Tab1 destroyed/detached
        // if Tab2 loads/displays, Tab1 and Tab3 loads in background if needed.
        // so...
        // pay attention to lifecycle calls to understand how Fragment actually works...
        // (see at end class for lifecycle...)

        // Set up ViewPager PagerAdapter (activity_tab_layout.xml)
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter adapter = new TabPagerAdapter(
                getSupportFragmentManager(),
                tabLayout.getTabCount(), context);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set up onTabSelectionListener for onTabSelected
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "onTabSelected " + tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * call back function for sub-fragments to get database object
     * @return DB object
     */
    public DBHelper getDB() {
        return DB;
    }

    /**
     * call back function for sub-fragments to get Firebase database object
     * @return FB object
     */
    public FireDBHelper getFB() {
        return FB;
    }

    /**
     * call back data function for sub-fragments to get user object
     * @return userApp data
     */
    public User getUserApp() {
        return userApp;
    }

    /**
     * Create Option Menu (Driver, Rider, Inactive)
     * @param menu Menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu ");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_tab_layout, menu);
        return true; // super.onCreateOptionsMenu(menu);
    }
// http://www.mysamplecode.com/2011/07/android-options-menu-submenu-group.html

    /**
     * Handle Option Menu Selection
     * @param item MenuItem
     * @return selected item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        String driveOrRide;
        String[] aDriveOrRide = getResources().getStringArray(R.array.driveoride);

        switch (item.getItemId()) {
            case R.id.miDriver:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                setOverflowIcon(R.drawable.ic_directions_car_white_24dp);
                driveOrRide = aDriveOrRide[0];
                break;

            case R.id.miRider:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                setOverflowIcon(R.drawable.ic_person_white_24dp);
                driveOrRide = aDriveOrRide[1];
                break;

            case R.id.miInactive:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                setOverflowIcon(R.drawable.ic_menu_overflow_holo_dark);
                driveOrRide = aDriveOrRide[2];
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        Log.d(TAG, "onOptionsItemSelected " + item.getOrder() + " " + driveOrRide);

        userApp.setDriveOrRide(driveOrRide);
        DB.updateUser(userApp);
        FB.putUserDriveOrRide(userApp.getSignInId(), driveOrRide);
        return true;
    }

    /**
     * Function to set/change the Toolbar icon to resource id
     * @param id R.drawable resource
     */
    private void setOverflowIcon(int id) {
        Drawable newIcon = ContextCompat.getDrawable(context, id);
        toolbar.setOverflowIcon(newIcon);
    }

    /**
     * Call back function to display user settings
     * @param key key
     * @param userData userData
     */
    @Override
    public void displayUser(String key, UserData userData) {
        if (userData == null) // new user...
            return;
        Log.d(TAG, "displayUser " + userData.toString());

        // update userApp and option menu only if app user!
        if (key == userApp.getSignInId()) {
            userApp.addUserData(userData);
            if (menu == null) return;

            String[] aDriveOrRide = getResources().getStringArray(R.array.driveoride);
            int idx = Arrays.asList(aDriveOrRide).indexOf(userApp.getDriveOrRide());

            // ensure option menu is consistent with data
            onOptionsItemSelected(menu.getItem(idx));
        }
    }

    // https://developer.android.com/guide/components/activities/activity-lifecycle.html
    // https://developer.android.com/guide/components/fragments.html#Lifecycle

    // onCreate

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart ---");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ---");
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause ---");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop ---");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart ---");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy ------");
        super.onDestroy();
    }
}
