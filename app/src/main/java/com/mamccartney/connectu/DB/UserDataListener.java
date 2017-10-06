package com.mamccartney.connectu.DB;

import com.mamccartney.connectu.Model.UserData;

/**
 * onKeyEntered notify listeners about cities added in search area
 * Created by sonic on 5/17/2017.
 */
public interface UserDataListener {
    /**
     * Called if a key entered the search area of the GeoQuery.
     *
     * @param key the key that entered the search area
     * @param userData the corresponding user data for this key
     */
    void displayUser(String key, UserData userData);
}
