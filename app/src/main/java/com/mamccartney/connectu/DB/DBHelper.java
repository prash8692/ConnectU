package com.mamccartney.connectu.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.mamccartney.connectu.Model.User;

/**
 * Created by kapilan on 4/4/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "StudentDB";
    private static final String TABLE_STUDENTS = "Student";

    private static final String COL_ROWID = "_id";
    private static final String COL_EMAIL ="email";
    private static final String COL_SIGNIN_ID ="signin_id";
    private static final String COL_A_NUMBER = "a_number";
    private static final String COL_FULL_NAME = "full_name";
    private static final String COL_START_ADDRESS = "start_address";
    private static final String COL_DRIVE_OR_RIDE = "drive_or_ride";
    private static final String COL_PHONE_NUMBER = "phone_number";
    private static final String COL_LAT_VALUE ="lat_value";
    private static final String COL_LNG_VALUE ="lng_value";

    private static final String TABLE_CREATE = "CREATE TABLE " +
        TABLE_STUDENTS + "(" +
        COL_ROWID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
        COL_EMAIL +" TEXT UNIQUE, " +
        COL_SIGNIN_ID +" TEXT UNIQUE, " +
        COL_A_NUMBER +" TEXT UNIQUE, " +
        COL_FULL_NAME +" TEXT, " +
        COL_START_ADDRESS +" TEXT, " +
        COL_DRIVE_OR_RIDE +" TEXT, "+
        COL_PHONE_NUMBER + " NUMBER, "+
        COL_LAT_VALUE +" DOUBLE, "+
        COL_LNG_VALUE +" DOUBLE);";

    private Context context;

    /**
     * Constructor
     * @param context
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Create the database table
     * @param db database object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.d(TAG, "onCreate database " + TABLE_STUDENTS + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        this.onCreate(db);
    }

    /**
     * Add a new user to the db
     * @param user object to add
     */
    public void addUser(User user) {
        Log.d(TAG, "addUser " + user.getFullName());
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EMAIL, user.getEmail());
        values.put(COL_SIGNIN_ID, user.getSignInId());
        values.put(COL_A_NUMBER, user.getAwid());
        values.put(COL_FULL_NAME, user.getFullName());
        values.put(COL_START_ADDRESS, user.getAddress());
        values.put(COL_DRIVE_OR_RIDE, user.getDriveOrRide());
        values.put(COL_PHONE_NUMBER, user.getContactNo());
        values.put(COL_LAT_VALUE, user.getLatLng().latitude);
        values.put(COL_LNG_VALUE, user.getLatLng().longitude);

        db.insert(TABLE_STUDENTS, null, values);
        db.close();
    }

    /**
     * Get user object given email address and signin_id
     * @param email string
     * @param signin_id string
     * @return User object
     */
    public User getUser(String email, String signin_id) {
        Log.d(TAG, "getUser " + email + " " + signin_id);

        String query = "SELECT * FROM " + TABLE_STUDENTS +
            " WHERE " + COL_EMAIL + "=? AND " + COL_SIGNIN_ID + "=?";

        String[] args = new String [] {email, signin_id};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, args);

        User user = new User();
        if (c != null) {
            if (c.moveToFirst()) {
                user.setRowId(Integer.parseInt(c.getString(0)));
                user.setEmail(c.getString(1));
                user.setSignInId(c.getString(2));
                user.setAwid(c.getString(3));
                user.setFullName(c.getString(4));
                user.setAddress(c.getString(5));
                user.setDriveOrRide(c.getString(6));
                user.setContactNo(c.getString(7));
                double lat = Double.parseDouble(c.getString(8));
                double lng = Double.parseDouble(c.getString(9));
                user.setLatLng(lat, lng);
                Log.d(TAG, "getUser found " + user.toString());
            }
            c.close();
        }
        db.close();

        return user;
    }

    /**
     * update user information in the database
     * @param user object to update in the db
     * @return int count of rows affected
     */
    public int updateUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        // email & signin Id not modifiable by design
        values.put(COL_A_NUMBER, user.getAwid());
        values.put(COL_FULL_NAME, user.getFullName());
        values.put(COL_START_ADDRESS, user.getAddress());
        values.put(COL_DRIVE_OR_RIDE, user.getDriveOrRide());
        values.put(COL_PHONE_NUMBER, user.getContactNo());
        values.put(COL_LAT_VALUE, user.getLatLng().latitude);
        values.put(COL_LNG_VALUE, user.getLatLng().longitude);

        int i = db.update(TABLE_STUDENTS, values,
            COL_ROWID + "=?",
            new String[] {String.valueOf(user.getRowId())}
        );

        Log.d(TAG, "updateUser " + String.valueOf(i) + " " + values.toString());
        db.close();
        return i;
    }

    /**
     * Check if user is in DB
     * @param userApp
     * @return true if user is in DB, otherwise false
     */
    public boolean isUserInDB(User userApp) {

        String query = "SELECT * FROM " + TABLE_STUDENTS +
            " WHERE " + COL_EMAIL + "=? AND " + COL_SIGNIN_ID + "=?";

        String[] args = new String [] {
            userApp.getEmail(),
            userApp.getSignInId()
        };

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, args);

        boolean ans = false;
        if (c != null) {
            if (c.moveToFirst()) {
                ans = true;
            }
            c.close();
        }
        db.close();

        String isuser = (ans ? " is" : " is not") + " in database";
        Log.d(TAG, "User " + userApp.getEmail() + isuser);
        return ans;
    }

    /**
     * register new user account if not in database or
     * update existing user account if in database
     * @param userApp object to process
     */
    public void register(User userApp) {
        String status = "Account Failed to Insert or Update";
        try {
            if (isUserInDB(userApp)) {
                updateUser(userApp);
                status = "Account Updated Successfully";
            } else {
                addUser(userApp);
                status = "Account Inserted Successfully";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }
}
