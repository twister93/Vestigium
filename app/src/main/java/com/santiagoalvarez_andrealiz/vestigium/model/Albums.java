package com.santiagoalvarez_andrealiz.vestigium.model;

public class Albums {
    String albumId, albumName,creationDate,favorite,description,maxSpeed,avgSpeed;

    public Albums(){}//Por defecto por requerimiento de Firebase


    public Albums(String albumId, String albumName, String creationDate, String favorite, String description, String maxSpeed, String avgSpeed) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.creationDate = creationDate;
        this.favorite= favorite;
        this.description=description;
        this.maxSpeed=maxSpeed;
        this.avgSpeed=avgSpeed;
    }


    //constructor para pruebas
    public Albums(String albumId, String albumName, String creationDate, String favorite) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.creationDate = creationDate;
        this.favorite = favorite;
    }
    public String getAlbumId() {

        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }


}


