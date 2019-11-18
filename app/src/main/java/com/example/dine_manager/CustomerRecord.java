package com.example.dine_manager;

import java.util.ArrayList;

public class CustomerRecord {

    private String name;
    private String contact;
    private String customerID;
    private String recordID;
    private String seatsBooked;
    private String roomNumber;
    private ArrayList<String> tags;
    private String emailAddress;
    private String date;
    private String time;
    private int visit;

    public CustomerRecord() {}

    public CustomerRecord(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    public int getVisit() {
        return visit;
    }
    public void setVisit(int visit) {
        this.visit = visit;
    }

    public String getCustomerID() {
        return customerID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getRecordID() {
        return recordID;
    }
    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSeatsBooked() {
        return seatsBooked;
    }
    public void setSeatsBooked(String seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
}
