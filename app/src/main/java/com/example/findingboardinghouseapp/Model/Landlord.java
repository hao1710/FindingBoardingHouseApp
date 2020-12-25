package com.example.findingboardinghouseapp.Model;

import java.io.Serializable;

public class Landlord implements Serializable {

    protected String idLandlord;
    protected String nameLandlord;
    protected String addressLandlord;
    protected String phoneNumberLandlord;
    protected String emailLandlord;
    protected String passwordLandlord;

    public String getIdLandlord() {
        return idLandlord;
    }

    public void setIdLandlord(String idLandlord) {
        this.idLandlord = idLandlord;
    }

    public String getNameLandlord() {
        return nameLandlord;
    }

    public void setNameLandlord(String nameLandlord) {
        this.nameLandlord = nameLandlord;
    }

    public String getAddressLandlord() {
        return addressLandlord;
    }

    public void setAddressLandlord(String addressLandlord) {
        this.addressLandlord = addressLandlord;
    }

    public String getPhoneNumberLandlord() {
        return phoneNumberLandlord;
    }

    public void setPhoneNumberLandlord(String phoneNumberLandlord) {
        this.phoneNumberLandlord = phoneNumberLandlord;
    }

    public String getEmailLandlord() {
        return emailLandlord;
    }

    public void setEmailLandlord(String emailLandlord) {
        this.emailLandlord = emailLandlord;
    }

    public String getPasswordLandlord() {
        return passwordLandlord;
    }

    public void setPasswordLandlord(String passwordLandlord) {
        this.passwordLandlord = passwordLandlord;
    }
}
