package com.example.spotapicheck.models;

public class User {
    public String birthdate;
    public String country;
    public String display_name;
    public String email;
    public String id;

    public User(String birthdate, String country, String display_name, String email, String id) {
        this.birthdate = birthdate;
        this.country = country;
        this.display_name = display_name;
        this.email = email;
        this.id = id;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }
}
