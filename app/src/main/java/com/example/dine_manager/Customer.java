package com.example.dine_manager;

import java.util.ArrayList;

public class Customer {
    private int numberOfVisits;
    private String contact;
    private String name;
    private int totalSeatsBooked;
    private String emailAddress;
    private String customerID;
    private ArrayList<String> feedbacks;
    private String lastVisitedOn;

    public Customer() {}

    public Customer(Customer oldCustomer) {
        this.numberOfVisits = oldCustomer.getNumberOfVisits();
        this.name = oldCustomer.getName();
        this.customerID = oldCustomer.getCustomerID();
        this.contact = oldCustomer.getContact();
        this.totalSeatsBooked = oldCustomer.getTotalSeatsBooked();
        this.lastVisitedOn = oldCustomer.lastVisitedOn;
        this.feedbacks = oldCustomer.getFeedbacks();
        this.emailAddress = oldCustomer.getEmailAddress();
    }

    public String getLastVisitedOn() {
        return lastVisitedOn;
    }
    public void setLastVisitedOn(String lastVisitedOn) {
        this.lastVisitedOn = lastVisitedOn;
    }

    public ArrayList<String> getFeedbacks() {
        return feedbacks;
    }
    public void setFeedbacks(ArrayList<String> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }
    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
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

    public int getTotalSeatsBooked() {
        return totalSeatsBooked;
    }
    public void setTotalSeatsBooked(int totalSeatsBooked) {
        this.totalSeatsBooked = totalSeatsBooked;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCustomerID() {
        return customerID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
