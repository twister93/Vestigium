package com.santiagoalvarez_andrealiz.vestigium.model;

public class Points extends Albums{
    String pointId, latitude,longitude;

    public Points(){}//Por defecto por requerimiento de Firebase

    public Points(String pointId, String latitude, String longitude) {
        this.pointId = pointId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}