package com.example.prosportswear.modal;

public class CartItem {
    private String id;
    private String shoeName;
    private String shoeCompany;
    private double price;
    private int quantity;

    public CartItem() {
        // Empty constructor required for Firestore
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShoeName() {
        return shoeName;
    }

    public void setShoeName(String shoeName) {
        this.shoeName = shoeName;
    }

    public String getShoeCompany() {
        return shoeCompany;
    }

    public void setShoeCompany(String shoeCompany) {
        this.shoeCompany = shoeCompany;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
