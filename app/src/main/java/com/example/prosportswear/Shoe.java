package com.example.prosportswear;
public class Shoe {
    private String name, companyName;
    private int count, price, id;

    // Required empty constructor for Firestore
    public Shoe() {}

    public Shoe(String name, String companyName, int count, int price, int id) {
        this.name = name;
        this.companyName = companyName;
        this.count = count;
        this.price = price;
        this.id = id;
    }

    // Getters
    public String getName() { return name; }
    public String getCompanyName() { return companyName; }
    public int getCount() { return count; }
    public int getPrice() { return price; }
    public int getId() { return id; }
}
