package com.mamccartney.connectu.Model;

/**
 * Created by sonic on 4/1/2017.
 */

public class Car {
    private String driverId;
    private String carModel;
    private String carRegNo;
    private String catMake;
    private int catCapacity;
    private String catType;
    private String carColor;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarRegNo() {
        return carRegNo;
    }

    public void setCarRegNo(String carRegNo) {
        this.carRegNo = carRegNo;
    }

    public String getCatMake() {
        return catMake;
    }

    public void setCatMake(String catMake) {
        this.catMake = catMake;
    }

    public int getCatCapacity() {
        return catCapacity;
    }

    public void setCatCapacity(int catCapacity) {
        this.catCapacity = catCapacity;
    }

    public String getCatType() {
        return catType;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
