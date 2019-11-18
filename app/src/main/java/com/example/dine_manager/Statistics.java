package com.example.dine_manager;

import java.util.ArrayList;

public class Statistics {
    private ArrayList<Integer> foodRatings;
    private ArrayList<Integer> experienceRatings;
    private ArrayList<Integer> serviceRatings;
    private int numberOfVisits;
    private int numberOfSeatsBooked;

    public ArrayList<Integer> getServiceRatings() {
        return serviceRatings;
    }
    public void setServiceRatings(ArrayList<Integer> serviceRatings) {
        this.serviceRatings = serviceRatings;
    }

    public ArrayList<Integer> getFoodRatings() {
        return foodRatings;
    }
    public void setFoodRatings(ArrayList<Integer> foodRatings) {
        this.foodRatings = foodRatings;
    }

    public ArrayList<Integer> getExperienceRatings() {
        return experienceRatings;
    }
    public void setExperienceRatings(ArrayList<Integer> experienceRatings) {
        this.experienceRatings = experienceRatings;
    }

    public int getNumberOfVisits() {
        return numberOfVisits;
    }
    public void setNumberOfVisits(int numberOfVisits) {
        this.numberOfVisits = numberOfVisits;
    }

    public int getNumberOfSeatsBooked() {
        return numberOfSeatsBooked;
    }
    public void setNumberOfSeatsBooked(int numberOfSeatsBooked) {
        this.numberOfSeatsBooked = numberOfSeatsBooked;
    }
}
