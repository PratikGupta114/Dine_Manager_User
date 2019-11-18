package com.example.dine_manager;

public class FeedbackRecord {

    public static String customerName ="Unknown";
    public static String customerContact = "NA";
    public static String emailAddress = "";
    public static String customerID = "";
    public static float experienceRatingLevel = 0.0f;
    public static float foodRatingLevel = 0.0f;
    public static String remarks = "";
    public static String time = "";
    public static String date = "";
    public static float serviceRating = 0.0f;

    public static String getEmailAddress() {
        return emailAddress;
    }
    public static void setEmailAddress(String emailAddress) {
        FeedbackRecord.emailAddress = emailAddress;
    }

    public static float getServiceRatingLevel() {
        return serviceRating;
    }
    public static void setServiceRatingLevel(float serviceRating) {
        FeedbackRecord.serviceRating = serviceRating;
    }

    public static String getCustomerID() {
        return customerID;
    }
    public static void setCustomerID(String customerID) {
        FeedbackRecord.customerID = customerID;
    }

    public static String getCustomerName() {
        return customerName;
    }
    public static void setCustomerName(String customerName) {
        FeedbackRecord.customerName = customerName;
    }

    public static String getCustomerContact() {
        return customerContact;
    }
    public static void setCustomerContact(String customerContact) {
        FeedbackRecord.customerContact = customerContact;
    }

    public static float getExperienceRatingLevel() {
        return experienceRatingLevel;
    }
    public static void setExperienceRatingLevel(float experienceRatingLevel) {
        FeedbackRecord.experienceRatingLevel = experienceRatingLevel;
    }

    public static float getFoodRatingLevel() {
        return foodRatingLevel;
    }
    public static void setFoodRatingLevel(float foodRatingLevel) {
        FeedbackRecord.foodRatingLevel = foodRatingLevel;
    }

    public static String getRemarks() {
        return remarks;
    }
    public static void setRemarks(String remarks) {
        FeedbackRecord.remarks = remarks;
    }

    public static String getTime() {
        return time;
    }
    public static void setTime(String time) {
        FeedbackRecord.time = time;
    }

    public static String getDate() {
        return date;
    }
    public static void setDate(String date) {
        FeedbackRecord.date = date;
    }
}
