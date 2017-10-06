package com.mamccartney.connectu.DB;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mamccartney.connectu.Model.Place;
import com.mamccartney.connectu.Model.User;
import com.mamccartney.connectu.Model.UserData;
import com.mamccartney.connectu.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sonic on 5/2/2017.
 */

public class FireDBHelper {

    private final static String TAG = "FireDBHelper";

    private final static String FIRE_DATABASE = "https://fir-connectu.firebaseio.com/";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDataReference;
    private DatabaseReference mCitiesReference;
    private DatabaseReference geoFireRef;
    private GeoFire geoFire;

    private ValueEventListener mValueEventListener;
    private ChildEventListener mDataChildEventListener;
    private List<UserDataListener> mUserDataListeners = new ArrayList<>();
    private List<CityListener> mCityListeners = new ArrayList<>();

    private Context context;
    private HashMap<String, Object> hmUserData = new HashMap<>();

    public FireDBHelper (Context context) {
        Log.d(TAG, "In constructor");
        this.context = context;

        mFirebaseDatabase = FirebaseDatabase.getInstance(FIRE_DATABASE);
        mUserDataReference = mFirebaseDatabase.getReference().child("userData");
        mCitiesReference = mFirebaseDatabase.getReference().child("cities");

        geoFireRef = mFirebaseDatabase.getReference().child("geofire");
        geoFire = new GeoFire(geoFireRef);

        // for testing
//        testUserList();  // to initially populate data in FirebaseDB
//        testCityList();  // to initially populate data in FirebaseDB

        // get data...
        getUserData();
    }

    /**
     * Callback function for Tab3Fragment
     * @return geofire object
     */
    public GeoFire getGeoFire() { return geoFire; }

    /* listeners support for UserData */

    public void addUserDataListener(UserDataListener userDataListener) {
        mUserDataListeners.add(userDataListener);
    }

    public void sendUserDataListeners(String key, UserData userData) {
        for (UserDataListener l: mUserDataListeners)
            l.displayUser(key, userData);
    }

    public void findUserData(final String key) {
        Log.d(TAG, "findUserData " + key);
//        mValueEventListener = new ValueEventListener() {
        mUserDataReference.child(key)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange " + dataSnapshot.toString());
                    //                // loop appropriate for top of UserData node...
                    //                // note that we're only listening for SignInId in ref below
                    //                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //                    Log.d(TAG, "child " + child.toString());
                    //                    UserData user = child.getValue(UserData.class);
                    //                    Log.d(TAG, "UserData " + user.toString());
                    //                }
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    sendUserDataListeners(key, userData);
                    if (userData != null) {
                        Log.d(TAG, userData.toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
//        mUserDataReference.child(user.addValueEventListener(mValueEventListener);
//        mUserDataReference.child(user.getSignInId()).addValueEventListener(mValueEventListener);
    }

    /* listeners support for City */

    public void addCityListener(CityListener cityListener) {
        mCityListeners.add(cityListener);
    }

    public void sendCityListeners(String key, String city) {
        for (CityListener l: mCityListeners)
            l.displayCity(key, city);
    }

    public void findCity(final String key) {
        Log.d(TAG, "findCity " + key);
        mCitiesReference.child(key)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange " + dataSnapshot.toString());
                    String city = (String) dataSnapshot.getValue();
                    sendCityListeners(key, city);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
    }

    /**
     * Function to insert/update firebase userData
     * @param user object
     */
    public void putUser(User user){
        Log.d(TAG, "putUser " + user.toString());

        UserData userData = new UserData(
            user.getAwid(),
            user.getEmail(),
            user.getContactNo(),
            user.getAddress(),
            user.getFullName(),
            user.getDriveOrRide()
        );
        // put in FirebaseDB @ userData
        mUserDataReference.child(user.getSignInId()).setValue(userData);
        // set corresponding GeoFire location
        geoFire.setLocation(user.getSignInId(), new GeoLocation(user.lat, user.lng));
    }

    /**
     * function to retrieve Firebase userData data
     */
    public void getUserData() {
        Log.d(TAG, "getUserData ");

        mDataChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "getUserData getKey " + dataSnapshot.getKey());
                hmUserData.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                Log.d(TAG, "hmUserData " + hmUserData.get(dataSnapshot.getKey()).toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //noinspection Since15
                hmUserData.replace(dataSnapshot.getKey(), dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                hmUserData.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mUserDataReference.addChildEventListener(mDataChildEventListener);
    }

    /**
     * Function to insert/update firebase userData driveOrRide
     * @param id Id
     * @param driveOrRide value
     */
    public void putUserDriveOrRide(String id, String driveOrRide) {
        Log.d(TAG, "putUserDriveOrRide " + driveOrRide);
        mUserDataReference.child(id).child("driveOrRide").setValue(driveOrRide);
    }

    /**
     * Utility test function to insert data into Firebase database
     */
    public void testUserList() {
        Log.d(TAG, "testUserList ");
        putUser(new User(
            "12345647890001", "Prashanth Boovaragavan", "A20382888",
            "Drive", 41.839178,  -87.616491,
            "pboovaragavan@hawk.iit.edu",
            "6035551212", "3001 S King Dr, Chicago, IL 60616, USA"
        ));
        putUser(new User(
            "12345647890002", "Aditya Chebiyyam", "A20385547",
            "Ride",  41.8349,    -87.6270, // circle...
            "achebiyyam@hawk.iit.edu",
            "6035551212", "15 W 33rd St, Chicago, IL 60616, USA"
        ));
        putUser(new User(
            "12345647890003", "Kapilan Kumanan", "A20375833",
            "Ride",  41.8815672, -87.64412, // middle of street
            "kkumanan@hawk.iit.edu",
            "6035551212", "9 N Desplaines St, Chicago, IL 60661, USA"
        ));
        putUser(new User(
            "12345647890004", "Michael McCartney", "A20389663",
            "Ride",  41.893495,  -87.612945, // Ohio Street Beach
            "mmccartney@hawk.iit.edu",
            "6304056014", "1766 Delta Dr, Aurora, IL 60503, USA"
        ));
    }

    public void testCityList() {

        DatabaseReference mCitiesRef;
        mCitiesRef = mFirebaseDatabase.getReference().child("cities");

/*
        // internal storage...
        File iPath = context.getFilesDir();
        Log.d(TAG, "Internal storage path " + iPath.toString());
        // /data/user/0/com.mamccartney.connectu/files

        // external storage...
        File ePath = context.getExternalFilesDir(null);
        Log.d(TAG, "External storage path " + ePath.toString());
        // /storage/emulated/0/Android/data/com.mamccartney.connectu/files

        // assets - app/src/main/assets/...
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("");
            Log.d(TAG, "Trying for asset files " + files.length);
            for(int i=0; i < files.length; i++)
            {
                Log.d(TAG, "File :"+i+" Name => "+files[i]);
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // AssetManager.AssetInputStream...

*/
        Boolean log = false;
        try {
            // file in app/src/main/assets/...
//            InputStream input = context.getAssets().open("ILshort.json");

            int id = R.raw.places_il; // 1368 cities
            // places_il.json file in app/src/main/res/raw/...
            InputStream input = context.getResources().openRawResource(id);

            // using json reader...
            InputStreamReader in = new InputStreamReader(input);
            JsonReader reader = new JsonReader(in);
            reader.beginArray();
            while (reader.hasNext()) {  // for each json object
                Place city = Place.jsonReader2Place(reader);
                if (log) Log.d(TAG, "cityReader " + city.getName());
                // put City name in Firebase DB as cities/geoid/name
                mCitiesRef.child(city.getGeoid().toString())
                    .setValue(city.getName());
                // put corresponding geofire location in Firebase DB
                geoFire.setLocation(
                    city.getGeoid().toString(),
                    new GeoLocation(city.getIntptlat(), city.getIntptlong())
                );
                // experiment to see if ...
                // - adding city name as part of the geofire
                // - have onKeyEntered pass the city name
                // - need to get geofire source added to project and modify...
                // to return data in onKeyEntered()...
//                geoFireRef.child(city.getGeoid().toString())
//                    .child("name").setValue(city.getName());
            }
            reader.endArray();
            reader.close();
            in.close();
            input.close();
        }

/*
            // using byte reader, JSONARRAY, JSONObject...
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            String text = new String(buffer, "UTF-8");
            if (log) Log.d(TAG, "ilshort.json " + text);

            // JSONObject expects {}
//            JSONObject jsonObject = new JSONObject(text);

            // JSONArray expects [ {}, ...]
            JSONArray jsonArray = new JSONArray(text);
            if (log) Log.d(TAG, "jsonObject " + jsonArray.toString());

            Place city = new Place();
            for(int i=0; i < jsonArray.length(); i++ ){
                city = city.jsonObject2Place(jsonArray.getJSONObject(i));
                if (log) Log.d(TAG, "cityObject " + city.toString());
                mCitiesReference.child(city.getGeoid().toString()).setValue(city);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
*/
        catch (IOException e) {
            e.printStackTrace();

        }
    }
}
