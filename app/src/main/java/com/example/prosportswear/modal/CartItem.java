package com.example.prosportswear.modal;

public class CartItem {
    private String id;
    private String shoeName;
    private String shoeCompany;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem() {
        // Empty constructor required for Firestore
    }

    public CartItem(String id, String shoeName, String shoeCompany, double price, int quantity, String imageUrl) {
        this.id = id;
        this.shoeName = shoeName;
        this.shoeCompany = shoeCompany;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // ✅ Getters
    public String getId() {
        return id;
    }

    public String getShoeName() {
        return shoeName;
    }

    public String getShoeCompany() {
        return shoeCompany;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // ✅ Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setShoeName(String shoeName) {
        this.shoeName = shoeName;
    }

    public void setShoeCompany(String shoeCompany) {
        this.shoeCompany = shoeCompany;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
