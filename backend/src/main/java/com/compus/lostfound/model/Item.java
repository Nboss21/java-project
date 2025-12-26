package com.campus.lostfound.model;

import java.util.Date;

/**
 * Unified model class representing both Lost and Found Items.
 */
public class Item {
    private String id;
    private String itemName;
    private String category;
    private String description;
    private String location;
    private Date date;
    private String status; // LOST, FOUND, CLAIMED, etc.
    private String contactInfo;
    private String type; // "LOST" or "FOUND"

    public Item() {}

    public Item(String id, String itemName, String category, String description, String location, Date date, String status, String contactInfo, String type) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.location = location;
        this.date = date;
        this.status = status;
        this.contactInfo = contactInfo;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", itemName='" + itemName + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
