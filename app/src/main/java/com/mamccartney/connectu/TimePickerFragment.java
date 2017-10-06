package com.mamccartney.connectu;

import android.app.Dialog;
import android.os.Bundle;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

/**
 * Created by sonic on 4/21/2017.
 * https://developer.android.com/guide/topics/ui/controls/pickers.html
 * http://stackoverflow.com/questions/33149101/how-to-add-timepicker-using-fragment
 */

public class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "TimePickerFragment";

    OnTimePickerListener fragmentCallback;

    // for parent activity to implement this "callback" interface
    public interface OnTimePickerListener {
        void getTime(int hourOfDay, int minute);
    }

    // for parent fragment to verify that callback function was implemented
    public void onAttachToParentFragment(Fragment fragment) {
        try {
            fragmentCallback = (OnTimePickerListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString()
                + " must implement OnTimePickerListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Extract passed time to use in the picker
        Bundle b = getArguments();
        int hourOfDay = b.getInt("hourOfDay");
        int minute = b.getInt("minute");

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hourOfDay, minute,
            DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        fragmentCallback.getTime(hourOfDay, minute);
    }
}
