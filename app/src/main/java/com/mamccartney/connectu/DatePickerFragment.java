package com.mamccartney.connectu;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.DatePicker;

/**
 * Created by sonic on 4/21/2017.
 * https://developer.android.com/guide/topics/ui/controls/pickers.html
 * http://stackoverflow.com/questions/33086786/how-to-use-date-picker-in-android-fragment
 */

public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";

    OnDatePickerListener fragmentCallback;

    // for parent activity to implement this "callback" interface
    public interface OnDatePickerListener {
        void getDate(int year, int month, int dayOfMonth);
    }

    // for parent fragment to verify that callback function was implemented
    public void onAttachToParentFragment(Fragment fragment) {
        try {
            fragmentCallback = (OnDatePickerListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                + " must implement OnDatePickerListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Extract passed date to use in the picker
        Bundle b = getArguments();
        int year = b.getInt("year");
        int month = b.getInt("month");
        int dayOfMonth = b.getInt("dayOfMonth");

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getContext(), this, year, month, dayOfMonth);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Do something with the date chosen by the user
        fragmentCallback.getDate(year, month, dayOfMonth);
    }
}
