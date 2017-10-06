package com.mamccartney.connectu;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by sonic on 2/11/2017.
 */

public class TabPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "TabPagerAdapter";

    int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem " + String.valueOf(position));

        switch(position) {
            case 0:
                Tab2Fragment tab2 = new Tab2Fragment();
                return tab2;
            case 1:
                Tab3Fragment tab3 = new Tab3Fragment();
                return tab3;
            case 2:
                Tab4Fragment tab4 = new Tab4Fragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
