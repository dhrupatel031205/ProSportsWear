package com.example.prosportswear.modal;

public class Shoe {
    private String id;
    private String name;
    private String companyName;
    private double price;
    private int count;
    private String pic; // ✅ Add image URL

    public Shoe(String id, String name, String companyName, double price, int count, String pic) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.price = price;
        this.count = count;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public String getPic() {
        return pic;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
