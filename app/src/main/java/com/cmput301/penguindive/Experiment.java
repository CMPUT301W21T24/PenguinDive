package com.cmput301.penguindive;

import java.io.Serializable;

public class Experiment implements Serializable {
    private String title;
    private String description;
    private String region;
    private String totalTrail;
    private String ownerUserName;
    private String status;

    public Experiment(String title, String description, String region, String totalTrail, String ownerUserName, String status) {
        this.title = title;
        this.description = description;
        this.region = region;
        this.totalTrail = totalTrail;
        this.ownerUserName = ownerUserName;
        this.status = status;
    }
    String getTitle(){return this.title;}
    String getDescription() {return this.description;}
    String getRegion() {return this.region;}
    String getTotalTrail() {return this.totalTrail;}
    String getOwnerUserName() {return this.ownerUserName;}
    String getStatus() {return status;}

    public void setStatus(String status) {
        this.status = status;
    }
}
