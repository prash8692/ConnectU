package com.mamccartney.connectu.Model;

/**
 * Created by sonic on 5/3/2017.
 */

public class UserData {
    private String awid;
    private String email;
    private String contactNo;
    private String address;
    private String fullName;
    private String driveOrRide;

    // default constructor
    public UserData() {}

    // constructor
    public UserData(String awid, String email, String contactNo, String address, String fullName, String driveOrRide) {
        this.awid = awid;
        this.email = email;
        this.contactNo = contactNo;
        this.address = address;
        this.fullName = fullName;
        this.driveOrRide = driveOrRide;
    }

    // getters, setters & toString

    public String getAwid() { return awid; }

    public void setAwid(String awid) { this.awid = awid; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getContactNo() { return contactNo; }

    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDriveOrRide() { return driveOrRide; }

    public void setDriveOrRide(String driveOrRide) { this.driveOrRide = driveOrRide; }

    @Override
    public String toString() {
        return "UserData{" +
            "awid='" + awid + '\'' +
            ", email='" + email + '\'' +
            ", contactNo='" + contactNo + '\'' +
            ", address='" + address + '\'' +
            ", fullName='" + fullName + '\'' +
            ", driveOrRide='" + driveOrRide + '\'' +
            '}';
    }
}
