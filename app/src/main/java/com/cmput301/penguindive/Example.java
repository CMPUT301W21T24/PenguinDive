package com.cmput301.penguindive;

public class Example {

    private String userName;
    private String owner;
    private String description;
    private String title;
    private String status;


    public  Example(String userName, String owner, String description, String title, String status){
        this.userName = userName;
        this.owner = owner;
        this.description = description;
        this.title = title;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }
}