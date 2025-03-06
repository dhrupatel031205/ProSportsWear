package com.example.prosportswear.modal;

public class Shoe {
    private String id;
    private String name;
    private String companyName;
    private double price;
    private int count;

    // Default constructor (needed for Firebase)
    public Shoe() {}

    public Shoe(String id, String name, String companyName, double price, int count) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.price = price;
        this.count = count;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
