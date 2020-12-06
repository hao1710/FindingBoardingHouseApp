package com.example.findingboardinghouseapp.Model;

import java.io.Serializable;

public class BoardingHouse implements Serializable {
    protected String idBoardingHouse;
    protected String nameBoardingHouse;
    protected String addressBoardingHouse;
    protected String descriptionBoardingHouse;
    protected double distanceBoardingHouse;
    protected double electricityPriceBoardingHouse;
    protected double waterPriceBoardingHouse;
    protected String idOwnerBoardingHouse;
    protected double latitude;
    protected double longitude;

    public BoardingHouse() {
    }

    public double getElectricityPriceBoardingHouse() {
        return electricityPriceBoardingHouse;
    }

    public void setElectricityPriceBoardingHouse(double electricityPriceBoardingHouse) {
        this.electricityPriceBoardingHouse = electricityPriceBoardingHouse;
    }

    public double getWaterPriceBoardingHouse() {
        return waterPriceBoardingHouse;
    }

    public void setWaterPriceBoardingHouse(double waterPriceBoardingHouse) {
        this.waterPriceBoardingHouse = waterPriceBoardingHouse;
    }

    public String getIdBoardingHouse() {
        return idBoardingHouse;
    }

    public void setIdBoardingHouse(String idBoardingHouse) {
        this.idBoardingHouse = idBoardingHouse;
    }

    public String getNameBoardingHouse() {
        return nameBoardingHouse;
    }

    public void setNameBoardingHouse(String nameBoardingHouse) {
        this.nameBoardingHouse = nameBoardingHouse;
    }

    public String getAddressBoardingHouse() {
        return addressBoardingHouse;
    }

    public void setAddressBoardingHouse(String addressBoardingHouse) {
        this.addressBoardingHouse = addressBoardingHouse;
    }

    public String getDescriptionBoardingHouse() {
        return descriptionBoardingHouse;
    }

    public void setDescriptionBoardingHouse(String descriptionBoardingHouse) {
        this.descriptionBoardingHouse = descriptionBoardingHouse;
    }

    public double getDistanceBoardingHouse() {
        return distanceBoardingHouse;
    }

    public void setDistanceBoardingHouse(double distanceBoardingHouse) {
        this.distanceBoardingHouse = distanceBoardingHouse;
    }

    public String getIdOwnerBoardingHouse() {
        return idOwnerBoardingHouse;
    }

    public void setIdOwnerBoardingHouse(String idOwnerBoardingHouse) {
        this.idOwnerBoardingHouse = idOwnerBoardingHouse;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
