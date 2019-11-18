package com.example.dine_manager;

public class OEvent {

    private int dayOfMonth;
    private int month;
    private int year;
    private String customerName;
    private String customerID;
    private String date;
    private String customerContact;

    public String getCustomerContact() {
        return customerContact;
    }
    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerID() {
        return customerID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
