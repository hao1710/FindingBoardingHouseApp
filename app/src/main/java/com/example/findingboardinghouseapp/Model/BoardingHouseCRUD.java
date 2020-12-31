package com.example.findingboardinghouseapp.Model;

import com.google.firebase.firestore.GeoPoint;

public class BoardingHouseCRUD {

    private String name;
    private String address;
    private String description;
    private double distance;
    private double electricityPrice;
    private double waterPrice;
    private String owner;
    private GeoPoint point;
    private double status;

    public BoardingHouseCRUD() {
    }

    public double getStatus() {
        return status;
    }

    public void setStatus(double status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public GeoPoint getPoint() {
        return point;
    }

    public void setPoint(GeoPoint point) {
        this.point = point;
    }

    public double getElectricityPrice() {
        return electricityPrice;
    }

    public void setElectricityPrice(double electricityPrice) {
        this.electricityPrice = electricityPrice;
    }

    public double getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(double waterPrice) {
        this.waterPrice = waterPrice;
    }
}
