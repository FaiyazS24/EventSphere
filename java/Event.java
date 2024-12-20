## Developed By: Faiyaz Sattar & Mohit Singh
    
package com.example.eventsclientapp1;

import java.io.Serializable;

public class Event implements Serializable {
    private String id; // Firestore document ID
    private long eventID;
    private String eventName;
    private String imageUrl;
    private String location;
    private int ticketsAvailable;
    private double price;
    private String description;
    private String eventDate; // New field for event date

    // Required empty constructor for Firestore deserialization
    public Event() {
    }

    public Event(String id, long eventID, String eventName, String imageUrl, String location, int ticketsAvailable, double price, String eventDate) {
        this.id = id;
        this.eventID = eventID;
        this.eventName = eventName;
        this.imageUrl = imageUrl;
        this.location = location;
        this.ticketsAvailable = ticketsAvailable;
        this.price = price;
        this.eventDate = eventDate;
    }

    // Getters and Setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTicketsAvailable() {
        return ticketsAvailable;
    }

    public void setTicketsAvailable(int ticketsAvailable) {
        this.ticketsAvailable = ticketsAvailable;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}
