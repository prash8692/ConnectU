package com.mamccartney.connectu;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mamccartney.connectu.Model.User;

import java.util.List;

/**
 * Created by sonic on 4/18/2017.
 */

public class UserListAdapter extends ArrayAdapter<User> {

    private List<User> items;

    public UserListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public UserListAdapter(Context context, int resource, List<User> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.itemlistrow, null);
        }

        final User p = getItem(position);
        if (p != null) {

            TextView tvName = (TextView) v.findViewById(R.id.fullName);
            tvName.setText(p.getFullName());
            tvName.setTypeface(Typeface.DEFAULT_BOLD);
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // 18sp=Medium text appearance.

            final ImageView ivUser = (ImageView) v.findViewById(R.id.ivUser);
            if (items.get(position).getDriveOrRide().equals("Drive")) {
                ivUser.setImageResource(R.drawable.ic_directions_car_black_24dp);
                ivUser.setTag(R.drawable.ic_directions_car_black_24dp);
            } else {  // Ride
                ivUser.setImageResource(R.drawable.ic_person_add_black_24dp);
                ivUser.setTag(R.drawable.ic_person_add_black_24dp);
            }

            final Context context = parent.getContext();
            ivUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (items.get(position).getDriveOrRide().equals("Ride")) {
                        // for adding or removing riders only
                        if ((Integer) ivUser.getTag() == R.drawable.ic_person_add_black_24dp) {
                            ivUser.setImageResource(R.drawable.ic_person_black_24dp);
                            ivUser.setTag(R.drawable.ic_person_black_24dp);
                            Toast.makeText(context, "Rider added: " + p.getFullName(),
                                Toast.LENGTH_SHORT).show();
                        } else {
                            ivUser.setImageResource(R.drawable.ic_person_add_black_24dp);
                            ivUser.setTag(R.drawable.ic_person_add_black_24dp);
                            Toast.makeText(context, "Rider removed: " + p.getFullName(),
                                Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, p.getFirstName() + ", you are the driver!",
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // TODO: 4/21/2017 add in chat message - perhaps only after accept
            final ImageView ivChat = (ImageView) v.findViewById(R.id.ivChat);
            ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Start chat message to " + p.getFirstName(),
                        Toast.LENGTH_SHORT).show();
                }
            });

            // TODO: 4/21/2017 add in phone call - perhaps only after accept
            final ImageView ivPhone = (ImageView) v.findViewById(R.id.ivPhone);
            ivPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Start phone call to " + p.getFirstName(),
                        Toast.LENGTH_SHORT).show();
                }
            });

        }

        return v;
    }
}
