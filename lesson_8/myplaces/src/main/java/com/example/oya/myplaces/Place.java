package com.example.oya.myplaces;

public class Place {
    private int id;
    private String name;
    private String description;
    private String address;
    private String category;
    private double latitude;
    private double longitude;
    private int imageResId;

    public Place(int id, String name, String description, String address,
                 String category, double latitude, double longitude, int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getCategory() { return category; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getImageResId() { return imageResId; }
}