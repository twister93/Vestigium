package com.santiagoalvarez_andrealiz.vestigium.model;

public class Points extends Albums{
    String pointId;
    String latitude;
    String longitude;
    String altitude;
    String photoURL;

    public Points(){}//Por defecto por requerimiento de Firebase

    public Points(String pointId, String latitude, String longitude) {
        this.pointId = pointId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Points(String pointId, String latitude, String longitude, String altitude) {
        this.pointId = pointId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }
    public Points(String pointId, String latitude, String longitude, String altitude, String photoURL) {
        this.pointId = pointId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.photoURL = photoURL;
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

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String longitude) {
        this.altitude = altitude;
    }

    public String getPhotoURL() { return photoURL; }

    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }


}