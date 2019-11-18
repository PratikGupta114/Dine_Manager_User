package com.example.dine_manager;

public class Feedback {

    private String contact;
    private String name;
    private String email;
    private String customerID;
    private String feedbackID;
    private String date;
    private String time;
    private String remarks;
    private float foodRating;
    private float experienceRating;
    private float serviceRating;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public float getServiceRating() {
        return serviceRating;
    }
    public void setServiceRating(float serviceRating) {
        this.serviceRating = serviceRating;
    }

    public String getCustomerID() {
        return customerID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getFeedbackID() {
        return feedbackID;
    }
    public void setFeedbackID(String feedbackID) {
        this.feedbackID = feedbackID;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public float getFoodRating() {
        return foodRating;
    }
    public void setFoodRating(float foodRating) {
        this.foodRating = foodRating;
    }

    public float getExperienceRating() {
        return experienceRating;
    }
    public void setExperienceRating(float experienceRating) {
        this.experienceRating = experienceRating;
    }
}
