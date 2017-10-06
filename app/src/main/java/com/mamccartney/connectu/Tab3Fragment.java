package com.mamccartney.connectu;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.mamccartney.connectu.DB.CityListener;
import com.mamccartney.connectu.DB.DBHelper;
import com.mamccartney.connectu.DB.FireDBHelper;
import com.mamccartney.connectu.DB.UserDataListener;
import com.mamccartney.connectu.Model.Ride;
import com.mamccartney.connectu.Model.User;
import com.mamccartney.connectu.Model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab3Fragment extends Fragment implements
    OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GeoQueryEventListener, UserDataListener, CityListener {

    private static final String TAG = "Tab3Fragment";

    private static final double MILE = 1609.344; // meters
    private static final double FIVE_MILES = 5*MILE;
    double milesToDestination = 0;

    GoogleMap googleMap;
    private Circle searchCircle;
    private GeoQuery geoQuery;
    private Map<String,Marker> markers;

    Context context;
    DBHelper db;
    View view;
    FireDBHelper FB;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = getActivity();
        // callback function from TabLayoutActivity
        db = ((TabLayoutActivity) getActivity()).getDB();
        FB = ((TabLayoutActivity) getActivity()).getFB();
        Log.d(TAG, "onCreate ------ " + context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView ------ ");
        //UiSettings ui = new UiSettings();

        if (container == null) {
            // prevent crash on orientation change, ie do not inflate view before tablayout...
            return null;
        }
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab3, container, false);

        // get userApp data and use it's LatLng as current location
        User userApp = ((TabLayoutActivity) getActivity()).getUserApp();
        Log.d(TAG, userApp.getFullName() + " " + userApp.getLatLng().toString());

        // https://developers.google.com/maps/documentation/android-api/map
        // https://developer.android.com/about/versions/android-4.2.html#NestedFragments

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager()
            .findFragmentById(R.id.mapView));

        mapFragment.getMapAsync(this);

        //show error dialog if GooglePlayServices not available

        return view;
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     * http://www.geomidpoint.com/calculation.html
     * https://developers.google.com/maps/documentation/android-api/start
     * https://developers.google.com/maps/documentation/android-api/map
     * https://developers.google.com/maps/documentation/android-api/utility/
     * https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary
     * https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady ");
        this.googleMap = googleMap;
        //ui.setZoomControlsEnabled(true);

//        map2();
//        computeRideDistance();
        mapGeoFire();
    }

    void mapGeoFire() {
        Log.d(TAG, "mapGeoFire ");

        // 1766 delta dr aurora il 60503 usa
        final GeoLocation INITIAL_CENTER = new GeoLocation(41.723169, -88.242980);
        LatLng latLngCenter = new LatLng(INITIAL_CENTER.latitude, INITIAL_CENTER.longitude);

        this.searchCircle = this.googleMap.addCircle(new CircleOptions()
            .center(latLngCenter)
            .radius(FIVE_MILES)
            .fillColor(Color.argb(66, 255, 0, 255))
            .strokeColor(Color.argb(66, 0, 0, 0))
        );

        final int INITIAL_ZOOM_LEVEL = (int) radiusToZoomLevel(FIVE_MILES); // 10; //14;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));
        this.googleMap.setOnCameraMoveListener(this);

        // setup GeoFire // TODO: 5/24/2017 - this blows on map rotate...
        GeoFire geoFire = FB.getGeoFire();

        // radius in km
        this.geoQuery = geoFire.queryAtLocation(INITIAL_CENTER, FIVE_MILES/1000);

        // add an event listener to start updating locations again
        this.geoQuery.addGeoQueryEventListener(this);

        // add an event listener to get locations info
        FB.addCityListener(this);
        FB.addUserDataListener(this);

        // setup markers
        this.markers = new HashMap<>();

        // setup initial circle
        setCenterCircle();
    }

    // original map logic
    void map2() {
        Log.d(TAG, "map2 ");

        Ride r = new Ride();
        User driver = r.getDriver();
        List<User> riders = r.getRiders();

        // set driver marker & location
        setMarkerLocation(driver.getFullName(), driver.getLatLng(), R.drawable.car_pin2);
        LatLng from_loc = driver.getLatLng();

        // for each rider...
        for (User rider : riders) {
            LatLng to_loc = rider.getLatLng();
            //PolylineOptions polylineOptions = new PolylineOptions();

            // Limit to riders within the five miles
            if (SphericalUtil.computeDistanceBetween(driver.getLatLng(), to_loc) < FIVE_MILES) {
                // set rider marker & location
                setMarkerLocation(rider.getFullName(), to_loc, -1);
                //googleMap.addPolyline(polylineOptions);
            }
            // TODO: 4/18/2017 try to center & zoom map equidistance from a group of points?
        }
        // to final destination!
        LatLng dst_loc = r.getDestination();
        setMarkerLocation("Destination", dst_loc, R.drawable.destination);
    }

    public void computeRideDistance() {
        Log.d(TAG, "computeRideDistance ");

        double distance = 0;
        double total_distance = 0;

        Ride r = new Ride();
        User driver = r.getDriver();
        List<User> riders = r.getRiders();

        LatLng from_loc = driver.getLatLng();

        // for each rider...
        for (User rider : riders) {
            LatLng to_loc = rider.getLatLng();
            //PolylineOptions polylineOptions = new PolylineOptions();

            // Limit to riders within the five miles
            if (SphericalUtil.computeDistanceBetween(driver.getLatLng(), to_loc) < FIVE_MILES) {
                // compute distance from_l to to_l
                distance = SphericalUtil.computeDistanceBetween(from_loc, to_loc);
                total_distance += distance;
                from_loc = to_loc;  // reset from_loc to to_loc
                Log.d(TAG, String.format("LatLng: %s Distance %.2f Total %.2f  %.2f",
                    to_loc.toString(), distance, total_distance, total_distance/MILE));
            }
        }
        // to final destination!
        LatLng dst_loc = r.getDestination();
        distance = SphericalUtil.computeDistanceBetween(from_loc, dst_loc);

        total_distance += distance;
        Log.d(TAG, String.format("LatLng: %s Distance %.2f Total %.2f  %.2f",
            dst_loc.toString(), distance, total_distance, total_distance/MILE));

        // for Toast message..
        milesToDestination = Math.round(total_distance / MILE);
    }

    /**
     * Function to manage setting up marker & location
     * @param title
     * @param latlng
     * @param resId
     */
    public void setMarkerLocation(String title, LatLng latlng, int resId) {
        // Set marker location
        if (resId > 0) {
            googleMap.addMarker(new MarkerOptions()
                .title(title)
                .position(latlng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(resId))
            );
        } else {
            // default marker locataion icons...
            googleMap.addMarker(new MarkerOptions()
                .title(title)
                .position(latlng)
                .draggable(true)
            );
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 100));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
    }

    /**
     * Only show toast when tab fragment is visible
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint " + (isVisibleToUser ? "True" : "False"));
        if (isVisibleToUser && view != null) {
            Toast.makeText(context, "Miles To Destination: "+ milesToDestination, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        Log.d(TAG, "onKeyEntered: " + key);
        // Add a new marker to the map
        Marker marker = this.googleMap.addMarker(
            new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
        );
        this.markers.put(key, marker);
        if (key.startsWith("17") && key.length() == 7)
            FB.findCity(key);
        else
            FB.findUserData(key);
    }

// http://www.avantica.net/blog/2016/11/03/tips-for-building-better-map-enabled-mobile-apps

    @Override
    public void onKeyExited(String key) {
        Log.d(TAG, "onKeyExited: " + key);
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d(TAG, "onKeyMoved ");

    }

    @Override
    public void onGeoQueryReady() {
        Log.d(TAG, "onGeoQueryReady ");
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d(TAG, "onGeoQueryError ");
        new AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
            .setPositiveButton(android.R.string.ok, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }

    @Override
    public void displayUser(String key, UserData userData) {
        if (userData == null)
            Log.d(TAG, "displayUser " + key);
        else
            Log.d(TAG, "displayUser " + key + " " + userData.getFullName());

        Marker marker = this.markers.get(key);
        if (marker != null) {
            // save position and remove old marker
            LatLng pos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            marker.remove();
            this.markers.remove(key);

            // add only if userData is available...
            if (userData != null) {
                int resId = R.drawable.driver48;
                switch (userData.getDriveOrRide()) {
                    case "Drive":
                        resId = R.drawable.ic_directions_car_black_24dp;
                        break;
                    case "Ride":
                        resId = R.drawable.ic_person_black_24dp;
                        break;
                    case "Inactive":
                    default:
                }

                // make new marker
                marker = this.googleMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .icon(BitmapDescriptorFactory.fromResource(resId))
                    .title(userData.getFullName())
                );
                // do not show marker if inactive
                if (resId == R.drawable.driver48) marker.setVisible(false);
                this.markers.put(key, marker);
            }
        }
    }

    @Override
    public void displayCity(String key, String city) {
        Log.d(TAG, "displayUser " + key + " " + city);

        Marker marker = this.markers.get(key);
        if (marker != null) {
            // randomize city markers with car/rider/add
            int resId = R.drawable.driver48;
            int random = (int )(Math.random() * 5 + 1);
            switch(random) {
                case 1: resId = R.drawable.ic_directions_car_black_24dp; break;
                case 2: resId = R.drawable.ic_directions_car_add_black_24dp; break;
                case 3: resId = R.drawable.ic_person_black_24dp; break;
                case 4: resId = R.drawable.ic_person_add_black_24dp; break;
                case 5: marker.setVisible(false); // inactive
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(resId));
            marker.setTitle(city);
        }
        // https://developers.google.com/maps/documentation/android-api/marker
        // https://developers.google.com/android/reference/com/google/android/gms/maps/model/Marker
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000/Math.pow(2, zoomLevel);
    }

    private double radiusToZoomLevel(double radius) {
        // Approximation to fit circle into view
        return Math.log(16384000/radius)/Math.log(2);
    }

    @Override
    public void onCameraMove() {
        // redraw center circle when moved
        setCenterCircle();
    }
// https://stackoverflow.com/questions/16681718/draw-five-transparent-circles-on-the-google-map-v2-by-taking-current-location-as
// https://stackoverflow.com/questions/15319431/how-to-convert-a-latlng-and-a-radius-to-a-latlngbounds-in-android-google-maps-ap

    public void setCenterCircle() {
        // Update the search criteria for this geoQuery and the circle on the map
        LatLng center = googleMap.getCameraPosition().target;
        double radius = zoomLevelToRadius(googleMap.getCameraPosition().zoom);
//        double zoom = radiusToZoomLevel(radius);
//        Log.d(TAG, " --------- radius is " + radius + " meters or " + radius/MILE + " miles");
//        Log.d(TAG, " --------- zoom is " + googleMap.getCameraPosition().zoom + " " + zoom);
        this.searchCircle.setCenter(center);
        this.searchCircle.setRadius(radius);
        this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
        this.geoQuery.setRadius(radius/1000);

        // place text label radius in miles on top of search/query circle
        IconGenerator iconFactory = new IconGenerator(context);
        addIcon(iconFactory,
            String.format("%.1f miles", radius/MILE),
            SphericalUtil.computeOffset(center, radius, 0)); // center);
    }

// https://developers.google.com/maps/documentation/android-api/utility/setup
// https://stackoverflow.com/questions/22536845/android-google-map-marker-with-label

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {

        // remove old text marker if present
        Marker marker = this.markers.get("txtMarker");
        if (marker != null) {
            marker.remove();
            this.markers.remove("txtMarker");
        }
        // add new text marker
        MarkerOptions markerOptions = new MarkerOptions().
            icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
            position(position).
            anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        marker = googleMap.addMarker(markerOptions);
        this.markers.put("txtMarker", marker);
    }

    // https://developer.android.com/guide/components/activities/activity-lifecycle.html
    // https://developer.android.com/guide/components/fragments.html#Lifecycle
    // https://developer.android.com/reference/android/app/Fragment.html

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach ------");
        super.onAttach(context);
    }

    // onCreate
    // onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated ---");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored ---");
    }

    @Override
    public void onStart() {
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
    public void onStop() {
        Log.d(TAG, "onStop ---");
        super.onStop();
        // remove all event listeners to stop updating in the background
        this.geoQuery.removeAllListeners();
        for (Marker marker: this.markers.values()) {
            marker.remove();
        }
        this.markers.clear();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView ------");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy ------");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach ------");
        super.onDetach();
    }
}
