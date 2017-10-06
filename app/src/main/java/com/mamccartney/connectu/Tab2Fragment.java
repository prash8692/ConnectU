package com.mamccartney.connectu;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mamccartney.connectu.DB.DBHelper;
import com.mamccartney.connectu.DB.FireDBHelper;
import com.mamccartney.connectu.DB.UserDataListener;
import com.mamccartney.connectu.Model.User;
import com.mamccartney.connectu.Model.UserData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2Fragment extends Fragment
    implements View.OnClickListener, UserDataListener {

    private final static String TAG = "Tab2Fragment";

    private EditText mAnumber;
    private EditText mFullname;
    private EditText mStartingadd;
    private EditText mPhno;
    private Spinner mdriveorRide;
    private Button mSubmit;

    private Context context;
    private DBHelper DB;
    private FireDBHelper FB;
    private View view;

    private User userApp;
    private String[] aDriveOrRide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        context = getApplicationContext();
        context = getActivity();
        // callback function from TabLayoutActivity
        DB = ((TabLayoutActivity) getActivity()).getDB();
        Log.d(TAG, "onCreate ------ " + context);

        // TODO: 5/23/2017 just get id....
        userApp = ((TabLayoutActivity) getActivity()).getUserApp();
        Log.d(TAG, "onCreate " + userApp.getFullName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ------");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab2, container, false);

        mAnumber = (EditText) view.findViewById(R.id.anumber);
        mFullname = (EditText) view.findViewById(R.id.fullname);
        mStartingadd = (EditText) view.findViewById(R.id.startingadd);
        mPhno = (EditText) view.findViewById(R.id.phoneno);

        mSubmit = (Button) view.findViewById(R.id.btnRegister);
        mSubmit.setText("Register New Account");
        mSubmit.setOnClickListener(this);

        // spinner start...
        aDriveOrRide = getResources().getStringArray(R.array.driveoride);
        ArrayAdapter<String> adp = new ArrayAdapter<>(context,
            android.R.layout.simple_spinner_item, aDriveOrRide);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_item);

        mdriveorRide = (Spinner) view.findViewById(R.id.boolean_spinner);
        mdriveorRide.setAdapter(adp);
        mdriveorRide.setWillNotDraw(false);
        mdriveorRide.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, aDriveOrRide[position], Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onItemSelected " + String.valueOf(position));
                // TODO: 4/28/2017 save to DB
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // spinner end...

        // set up listener for user data from FB, displayUser() will be called.
        // next line crashes on orientation change in onCreate() - moved to here...
        FB = ((TabLayoutActivity) getActivity()).getFB();
        FB.addUserDataListener(this);
        FB.findUserData(userApp.getSignInId());

        return view;
    }

    /**
     * Call back function to display user settings
     * @param key key
     * @param userData userData
     */
    @Override
    public void displayUser(String key, UserData userData) {
        // process only actual app user
        if (key != userApp.getSignInId()) return;

        if (userData == null) {
            mFullname.setText(userApp.getFullName());
            mSubmit.setText("Register New Account");
            return;
        }

        mSubmit.setText("Update Account");
        Log.d(TAG, "displayUser " + userData.toString());

        mAnumber.setText(userData.getAwid());
        mFullname.setText(userData.getFullName());
        mStartingadd.setText(userData.getAddress());
        mPhno.setText(userData.getContactNo());

        int idx = Arrays.asList(aDriveOrRide).indexOf(userData.getDriveOrRide());
        // this only works first time
        mdriveorRide.setSelection(idx, true);
        Log.d(TAG, "displayUser " + userData.getDriveOrRide()
            + " " + mdriveorRide.getSelectedItemPosition()
            + " " + mdriveorRide.getSelectedItem()
            + " " + idx);
    }

    public void onClick(View v) {
        Log.d(TAG, "onClick");

        switch (v.getId()) {

            case R.id.btnRegister:

                String anumber = mAnumber.getText().toString().trim();
                String fullname = mFullname.getText().toString().trim();
                String startingaddress = mStartingadd.getText().toString().trim();
                String phonenum = mPhno.getText().toString().trim();
                String driveorride = mdriveorRide.getSelectedItem().toString().trim();

                if (anumber.equals("")) {
                    Toast.makeText(context, "Enter your A Number", Toast.LENGTH_SHORT).show();
                } else if (fullname.equals("")) {
                    Toast.makeText(context, "Please enter your Full Name", Toast.LENGTH_SHORT).show();
                } else if (startingaddress.equals("")) {
                    Toast.makeText(context, "Please enter your Starting Address", Toast.LENGTH_SHORT).show();
                } else if (phonenum.equals("")) {
                    Toast.makeText(context, "Please enter your Contact Number", Toast.LENGTH_SHORT).show();
                } else if (driveorride.equals("")) {
                    Toast.makeText(context, "Please indicate if Driver or Rider", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(ctx, "Please enter your Destination Address", Toast.LENGTH_SHORT).show();
                } else {

                    userApp.setAwid(anumber);
                    userApp.setFullName(fullname);
                    userApp.setAddress(startingaddress);
                    userApp.setContactNo(phonenum);
                    userApp.setDriveOrRide(driveorride);
                    Log.d(TAG, "onClick " + userApp.getDriveOrRide());

                    Geocoder geocoder = new Geocoder(context, Locale.US);
                    try {
                        ArrayList<Address> address_val = (ArrayList<Address>) geocoder.getFromLocationName(userApp.getAddress(), 1);
                        for (Address add : address_val) {
                            userApp.setLatLng(add.getLatitude(), add.getLongitude());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }

                    // TODO: 4/28/2017 fix this hack...
                    if (userApp.getEmail().equals("mmccartney@hawk.iit.edu"))
                        userApp.setLatLng(41.893495, -87.612945); // Ohio Street Beach

                    DB.register(userApp);
                    mSubmit.setText("Update Account");
                    FB.putUser(userApp);
                }
                break;
            default:
        }
    }

    // https://developer.android.com/guide/components/activities/activity-lifecycle.html
    // https://developer.android.com/guide/components/fragments.html#Lifecycle

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach ------");
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ---");
        // http://stackoverflow.com/questions/8997225/how-to-hide-android-soft-keyboard-on-edittext
        // "I sometimes use a bit of a trick to do just that. I put an invisible focus holder
        // somewhere on the top of the layout. It would be e.g. like this..."
        View editInvisibleFocusHolder;
        editInvisibleFocusHolder = (View) view.findViewById(R.id.editInvisibleFocusHolder);
        editInvisibleFocusHolder.requestFocus();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause ---");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView ------");
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach ------");
        super.onDetach();
    }

}
