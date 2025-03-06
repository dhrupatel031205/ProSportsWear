package com.example.prosportswear.modal;

public class CartItem {
    private String shoeName;
    private String shoeCompany;
    private double price;
    private int quantity;

    public CartItem() { } // Required for Firestore

    public CartItem(String shoeName, String shoeCompany, double price, int quantity) {
        this.shoeName = shoeName;
        this.shoeCompany = shoeCompany;
        this.price = price;
        this.quantity = quantity;
    }

    public String getShoeName() { return shoeName; }
    public String getShoeCompany() { return shoeCompany; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
}
