package com.example.findingboardinghouseapp.Model;

import java.util.ArrayList;

public class RoomCRUD {
    private ArrayList<String> image;
    private boolean status;

    public RoomCRUD() {
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
