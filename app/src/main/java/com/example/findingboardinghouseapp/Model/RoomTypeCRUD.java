package com.example.findingboardinghouseapp.Model;

public class RoomTypeCRUD {
    private String name;
    private double area;
    private double price;
    private double numberPeople;
    private String description;

    public RoomTypeCRUD() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getNumberPeople() {
        return numberPeople;
    }

    public void setNumberPeople(double numberPeople) {
        this.numberPeople = numberPeople;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
