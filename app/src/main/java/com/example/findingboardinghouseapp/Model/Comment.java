package com.example.findingboardinghouseapp.Model;

public class Comment {
    protected String idComment;
    protected String nameTenant;
    protected String contentComment;
    protected String idBoardingHouse;

    public Comment() {
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    public String getNameTenant() {
        return nameTenant;
    }

    public void setNameTenant(String nameTenant) {
        this.nameTenant = nameTenant;
    }

    public String getContentComment() {
        return contentComment;
    }

    public void setContentComment(String contentComment) {
        this.contentComment = contentComment;
    }

    public String getIdBoardingHouse() {
        return idBoardingHouse;
    }

    public void setIdBoardingHouse(String idBoardingHouse) {
        this.idBoardingHouse = idBoardingHouse;
    }
}
