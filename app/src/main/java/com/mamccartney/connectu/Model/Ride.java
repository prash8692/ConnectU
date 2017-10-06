package com.mamccartney.connectu.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sonic on 4/1/2017.
 */

public class Ride {

    private String name;
    private User driver;
    private List<User> riders = new ArrayList<>();

    public Ride() {
        setRideList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getDriver() {
        return driver;
    }

    public void addRider(User rider) {
        riders.add(rider);
    }

    public void removeRider(User rider) {
        riders.remove(rider);
    }

    public List<User> getRiders() {
        return riders;
    }

    /*
     * Convenience functions to make some dummy data
     */

    /**
     * Set up a ride
     */
    private void setRideList() {
        setName("ConnectU Development Team");
        // Note: driver should always be added first.
        addRide("Prashanth Boovaragavan", "Drive", 41.839178, -87.616491);
        addRide("Aditya Chebiyyam", "Ride", 41.8349, -87.6270);// North Avenue Beach
        addRide("Kapilan Kumanan", "Ride", 41.8815672, -87.64412);
        addRide("Michael McCartney", "Ride", 41.893495, -87.612945); // Ohio Street Beach
    }

    /**
     * Adds new rider to riders
     * @param fullName
     * @param driveOrRide
     * @param lat
     * @param lng
     */
    public void addRide(String fullName, String driveOrRide, double lat, double lng) {
        User user = new User();
        user.setFullName(fullName);
        user.setDriveOrRide(driveOrRide);
        String[] n = fullName.split(" ");
        user.setFirstName(n[0]);
        user.setLastName(n[1]);
        user.setLatLng(lat, lng);
        if (driveOrRide.equals("Drive")) {
            setDriver(user);
        } else { // rider
            addRider(user);
        }
    }

    public LatLng getDestination() {
        // Hermann Hall, 3241 S. Federal St, Chicago, IL 60616
        return new LatLng(41.8356672, -87.628387);
    }
}
