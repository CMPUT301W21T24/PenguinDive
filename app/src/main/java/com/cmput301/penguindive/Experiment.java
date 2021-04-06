package com.cmput301.penguindive;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a Experiment object
 */
public class Experiment implements Serializable {
    private String experimentId;
    private String title;
    private String description;
    private String region;
    private Integer minTrials;
    private String ownerId;
    private String ownerUserName;
    private String status;
    private List<String> experimenters;
    private String trialType;  // string for requested type of trial
    private Boolean locationState;

    public Experiment(String experimentId, String title, String description, String region, Integer minTrials, String ownerId, String ownerUserName, String status, List<String> experimenters, Boolean locationState, String trialType) {
        this.experimentId = experimentId;
        this.title = title;
        this.description = description;
        this.region = region;
        this.minTrials = minTrials;
        this.ownerId = ownerId;
        this.ownerUserName = ownerUserName;
        this.status = status;
        this.experimenters = experimenters;
        this.locationState = locationState;
        this.trialType = trialType;
    }

    // Getters and Setters
    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getMinTrials() {
        return minTrials;
    }

    public void setMinTrials(Integer minTrials) {
        this.minTrials = minTrials;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getExperimenters() {
        return experimenters;
    }

    public void setExperimenters(List<String> experimenters) {
        this.experimenters = experimenters;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getTrialType() { return trialType; }

    public void setLocationState(boolean locationState) {
        this.locationState = locationState;
    }
    Boolean getLocationState() {
        return locationState;
    }

}
