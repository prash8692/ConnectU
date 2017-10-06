package com.mamccartney.connectu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mamccartney.connectu.Model.Ride;
import com.mamccartney.connectu.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab4Fragment extends Fragment implements
    AdapterView.OnItemClickListener,
    DatePickerFragment.OnDatePickerListener,
    TimePickerFragment.OnTimePickerListener {

    private static final String TAG = "Tab4Fragment";

    private View view;
    private List<User> mList;
    private EditText mCarpoolName;
    private Calendar calendar;
    private TextView tvDate;
    private TextView tvTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ------ ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ------");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab4, container, false);

        mCarpoolName = (EditText) view.findViewById(R.id.carpoolName);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvTime = (TextView) view.findViewById(R.id.tvTime);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        // Use current date/time
        calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // set current date
        SimpleDateFormat sdfDate = new SimpleDateFormat("M/d/y");
        tvDate.setText(sdfDate.format(date));

        // show date picker when date text is clicked
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        // set current time
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
        tvTime.setText(sdfTime.format(date));

        // show date picker when date text is clicked
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        // get ride/carpool
        Ride carpool = new Ride();
        mCarpoolName.setText(carpool.getName());
        // list of all riders
        mList = carpool.getRiders();
        mList.add(0, carpool.getDriver());

        UserListAdapter adapter = new UserListAdapter(getActivity(), R.layout.itemlistrow, mList);
        ListView listview = (ListView) getActivity().findViewById(android.R.id.list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Log.d(TAG, "onItemClick");
        Toast.makeText(getActivity(), "Hello " + mList.get(position).getFirstName(),
            Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to display date picker
     */
    public void showDatePickerDialog() {

        // Use the current date for the picker
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Bundle date for DatePickerFragment
        Bundle b = new Bundle();
        b.putString("type", "date");
        b.putInt("year", year);
        b.putInt("month", month);
        b.putInt("dayOfMonth", dayOfMonth);

        // set up Date Picker Dialog Fragment and show
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setArguments(b);
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    /**
     * DatePickerListener callback function, "returns date"
     *
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void getDate(int year, int month, int dayOfMonth) {
        String s = String.format("%d/%d/%d", month+1, dayOfMonth, year);
        tvDate.setText(s);
        Toast.makeText(getActivity(), "Date: " + s, Toast.LENGTH_LONG).show();
    }

    /**
     * Function to display time picker
     */
    public void showTimePickerDialog() {

        // Use the current time for the picker
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Bundle time for TimePickerFragment
        Bundle b = new Bundle();
        b.putString("type", "time");
        b.putInt("hourOfDay", hourOfDay);
        b.putInt("minute", minute);

        // set up Time Picker Dialog Fragment and show
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(b);
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    /**
     * TimePickerListener callback function, "returns time"
     *
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void getTime(int hourOfDay, int minute) {
        String s = showTime(hourOfDay, minute);
        tvTime.setText(s);
        Toast.makeText(getActivity(), "Time: " + s, Toast.LENGTH_LONG).show();
    }

    private String showTime(int hour, int minute) {
        String format;
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        return String.format("%d:%02d %s", hour, minute, format);
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
        editInvisibleFocusHolder = (View) view.findViewById(R.id.editInvisibleFocusHolder4);
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
