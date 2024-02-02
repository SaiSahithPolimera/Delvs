package com.example.delvs;

public class Users {
    Users(){
    }

    public String firstName, lastName, age, userName, mobileNumber;
    public double latitude, longitude;

    // Constructor for user details
    public Users(String firstName, String lastName, String age, String userName, String mobileNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.userName = userName;
        this.mobileNumber = mobileNumber;
    }

   public Users(String userName, String firstName,double latitude, double longitude, String mobileNumber) {
        this.userName = userName;
        this.firstName = firstName;
        this.mobileNumber = mobileNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // Constructor for location details
    public Users(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters for latitude and longitude
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Getters and setters for user details
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
