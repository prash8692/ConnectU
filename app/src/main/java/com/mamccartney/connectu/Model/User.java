package com.mamccartney.connectu.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sonic on 4/1/2017.
 */

public class User {
    private int rowId;
    private String signInId;
    private String firstName;
    private String lastName;
    private LatLng latLng;
    public double lat;
    public double lng;

    // UserData...
    private String awid;
    private String email;
    private String contactNo;
    private String fullName;
    private String address;
    private String driveOrRide;

    // default constructor
    public User() {
        // defaults
        setDriveOrRide("Inactive");
        setLatLng(-33.350534, -71.653268); // World's biggest pool
        setLatLng(50.010083,-110.113006); // Badlands Guardian
        setLatLng(41.893495, -87.612945); // Ohio Street Beach
        // TODO: 5/15/2017 set current location?
        setEmail("dummy@email.com");
        setSignInId("12344576890");
        setFullName("Unknown IITUser");
    }

    // constructor
    public User(
        String id, String fullName, String awid,
        String driveOrRide, Double lat, Double lng,
        String email, String phone, String address
    ) {
        this.signInId = id;
        String[] n = fullName.split(" ");
        this.firstName = n[0];
        this.lastName = n[1];
        this.setLatLng(lat, lng);

        // UserData...
        this.awid = awid;
        this.email = email;
        this.contactNo = phone;
        this.fullName = fullName;
        this.address = address;
        this.driveOrRide = driveOrRide;
    }

    /**
     * Update user data with UserData
     * @param userData UserData
     * @return UserData
     */
    public User addUserData(UserData userData) {
        String[] n = this.fullName.split(" ");
        this.firstName = n[0];
        this.lastName = n[1];
//        this.setLatLng(lat, lng);

        // UserData...
        this.awid = userData.getAwid();
        this.fullName = userData.getFullName();
        this.email = userData.getEmail();
        this.contactNo = userData.getContactNo();
        this.address = userData.getAddress();
        this.driveOrRide = userData.getDriveOrRide();

        return this;
    }

    // getters, setters & toString

    public String getDriveOrRide() {
        return driveOrRide;
    }

    public void setDriveOrRide(String driveOrRide) {
        this.driveOrRide = driveOrRide;
    }

    public String getAwid() {
        return awid;
    }

    public void setAwid(String awid) {
        this.awid = awid;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignInId() {
        return signInId;
    }

    public void setSignInId(String signInId) {
        this.signInId = signInId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
        latLng = new LatLng(lat, lng);
    }

    public void setLatLng(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
        this.latLng = latLng;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Override
    public String toString() {
        return "User{" +
            "rowId=" + rowId +
            ", signInId='" + signInId + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", latLng=" + latLng.toString() +
            ", awid='" + awid + '\'' +
            ", email='" + email + '\'' +
            ", contactNo='" + contactNo + '\'' +
            ", fullName='" + fullName + '\'' +
            ", address='" + address + '\'' +
            ", driveOrRide='" + driveOrRide + '\'' +
            '}';
    }
}
