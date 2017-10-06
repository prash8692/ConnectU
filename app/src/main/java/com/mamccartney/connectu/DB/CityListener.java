package com.mamccartney.connectu.DB;

/**
 * onKeyEntered notify listeners about cities added in search area
 * Created by sonic on 5/24/2017.
 */
public interface CityListener {
    /**
     * Called if a key entered the search area of the GeoQuery.
     *
     * @param key the key that entered the search area
     * @param city the city for this key
     */
    void displayCity(String key, String city);
}
