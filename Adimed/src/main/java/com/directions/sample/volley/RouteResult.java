package com.directions.sample.volley;

/**
 * Created by user on 2016-01-15.
 */
public class RouteResult {

    private String distanceText;
    private String distanceValue;
    private String durationText;
    private String durationValue;
    private String status;
    private String destinationAddresses;
    private String originAddresses;
    private String fareCost;

    public String getFareCost() {
        return fareCost;
    }

    public void setFareCost(String fareCost) {
        this.fareCost = fareCost;
    }

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public String getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public String getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(String durationValue) {
        this.durationValue = durationValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDestinationAddresses() {
        return destinationAddresses;
    }

    public void setDestinationAddresses(String destinationAddresses) {
        this.destinationAddresses = destinationAddresses;
    }

    public String getOriginAddresses() {
        return originAddresses;
    }

    public void setOriginAddresses(String originAddresses) {
        this.originAddresses = originAddresses;
    }
}
