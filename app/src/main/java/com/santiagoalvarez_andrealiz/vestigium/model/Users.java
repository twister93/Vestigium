package com.santiagoalvarez_andrealiz.vestigium.model;

/**
 * Created by andrealiz on 16/04/18.
 */

public class Users {
    String id, name, phone, email, photos;

    public Users() {//Por defecto Firebase lo exige
    }

    public Users(String id, String name, String phone, String email, String photos) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.photos = photos;
    }

    public Users(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }
}
