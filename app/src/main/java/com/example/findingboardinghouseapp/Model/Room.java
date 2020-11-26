package com.example.findingboardinghouseapp.Model;

import java.io.Serializable;

public class Room extends RoomType implements Serializable {

    protected String nameRoom;
    protected boolean statusRoom;
    protected String imageRoom;


    public Room() {
    }

    public Room(boolean statusRoom, String imageRoom) {
        this.statusRoom = statusRoom;
        this.imageRoom = imageRoom;
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public boolean isStatusRoom() {
        return statusRoom;
    }

    public void setStatusRoom(boolean statusRoom) {
        this.statusRoom = statusRoom;
    }

    public String getImageRoom() {
        return imageRoom;
    }

    public void setImageRoom(String imageRoom) {
        this.imageRoom = imageRoom;
    }

}
