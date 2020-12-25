package com.example.findingboardinghouseapp.Model;

import java.io.Serializable;

public class RoomType extends BoardingHouse implements Serializable {

    protected String idRoomType;
    protected String nameRoomType;
    protected int numberPeopleRoomType;
    protected double priceRoomType;
    protected double areaRoomType;
    protected String descriptionRoomType;


    public RoomType() {
    }

    public String getDescriptionRoomType() {
        return descriptionRoomType;
    }

    public void setDescriptionRoomType(String descriptionRoomType) {
        this.descriptionRoomType = descriptionRoomType;
    }

    public String getIdRoomType() {
        return idRoomType;
    }

    public void setIdRoomType(String idRoomType) {
        this.idRoomType = idRoomType;
    }

    public String getNameRoomType() {
        return nameRoomType;
    }

    public void setNameRoomType(String nameRoomType) {
        this.nameRoomType = nameRoomType;
    }

    public int getNumberPeopleRoomType() {
        return numberPeopleRoomType;
    }

    public void setNumberPeopleRoomType(int numberPeopleRoomType) {
        this.numberPeopleRoomType = numberPeopleRoomType;
    }

    public double getPriceRoomType() {
        return priceRoomType;
    }

    public void setPriceRoomType(double priceRoomType) {
        this.priceRoomType = priceRoomType;
    }

    public double getAreaRoomType() {
        return areaRoomType;
    }

    public void setAreaRoomType(double areaRoomType) {
        this.areaRoomType = areaRoomType;
    }




}
