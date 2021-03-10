package com.cmput301.penguindive;

import java.io.Serializable;

public class Experiment implements Serializable {
    private String title;
    private String description;
    private String region;
    private String totalTrail;
    private String ownerUserName;

    public Experiment(String title, String description, String region, String totalTrail, String ownerUserName) {
        this.title = title;
        this.description = description;
        this.region = region;
        this.totalTrail = totalTrail;
        this.ownerUserName = ownerUserName;
    }

    String getTitle(){return this.title;}
    String getDescription() {return this.description;}
    String getRegion() {return this.region;}
    String getTotalTrail() {return this.totalTrail;}
    String getOwnerUserName() {return this.ownerUserName;}
}
