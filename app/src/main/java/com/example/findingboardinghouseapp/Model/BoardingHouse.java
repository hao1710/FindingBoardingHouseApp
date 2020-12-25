package com.example.findingboardinghouseapp.Model;

import java.io.Serializable;
import java.util.Objects;

public class BoardingHouse implements Serializable {

    protected String idBoardingHouse;
    protected String nameBoardingHouse;
    protected String addressBoardingHouse;
    protected String descriptionBoardingHouse;
    protected double distanceBoardingHouse;
    protected double electricityPriceBoardingHouse;
    protected double waterPriceBoardingHouse;
    protected String idOwnerBoardingHouse;
    protected String nameOwnerBoardingHouse;
    protected String phoneNumberOwnerBoardingHouse;
    protected double latitude;
    protected double longitude;
    protected boolean statusBoardingHouse;

    public BoardingHouse() {
    }

    public boolean isStatusBoardingHouse() {
        return statusBoardingHouse;
    }

    public void setStatusBoardingHouse(boolean statusBoardingHouse) {
        this.statusBoardingHouse = statusBoardingHouse;
    }

    public String getNameOwnerBoardingHouse() {
        return nameOwnerBoardingHouse;
    }

    public void setNameOwnerBoardingHouse(String nameOwnerBoardingHouse) {
        this.nameOwnerBoardingHouse = nameOwnerBoardingHouse;
    }

    public String getPhoneNumberOwnerBoardingHouse() {
        return phoneNumberOwnerBoardingHouse;
    }

    public void setPhoneNumberOwnerBoardingHouse(String phoneNumberOwnerBoardingHouse) {
        this.phoneNumberOwnerBoardingHouse = phoneNumberOwnerBoardingHouse;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardingHouse that = (BoardingHouse) o;
        return Double.compare(that.distanceBoardingHouse, distanceBoardingHouse) == 0 &&
                Double.compare(that.electricityPriceBoardingHouse, electricityPriceBoardingHouse) == 0 &&
                Double.compare(that.waterPriceBoardingHouse, waterPriceBoardingHouse) == 0 &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                statusBoardingHouse == that.statusBoardingHouse &&
                Objects.equals(idBoardingHouse, that.idBoardingHouse) &&
                Objects.equals(nameBoardingHouse, that.nameBoardingHouse) &&
                Objects.equals(addressBoardingHouse, that.addressBoardingHouse) &&
                Objects.equals(descriptionBoardingHouse, that.descriptionBoardingHouse) &&
                Objects.equals(idOwnerBoardingHouse, that.idOwnerBoardingHouse) &&
                Objects.equals(nameOwnerBoardingHouse, that.nameOwnerBoardingHouse) &&
                Objects.equals(phoneNumberOwnerBoardingHouse, that.phoneNumberOwnerBoardingHouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBoardingHouse, nameBoardingHouse, addressBoardingHouse, descriptionBoardingHouse, distanceBoardingHouse, electricityPriceBoardingHouse, waterPriceBoardingHouse, idOwnerBoardingHouse, nameOwnerBoardingHouse, phoneNumberOwnerBoardingHouse, latitude, longitude, statusBoardingHouse);
    }
}
