package com.santiagoalvarez_andrealiz.vestigium.model;

public class Points extends Albums{
    String pointId;
    String latitude;
    String longitude;
    String altitude;
    String photoURL;
    Double lat;
    Double log;

    public Points(){}//Por defecto por requerimiento de Firebase

    public Points(Double lat, Double log) {
        this.lat = lat;
        this.log = log;
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

    //That's for the constructor with double latitud and longitude
    public Double getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.latitude = lat;
    }

    public Double getLog() {
        return log;
    }

    public void setLog(String longitude) {
        this.log = log;
    }

    // ...

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