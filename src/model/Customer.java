package com.cstore.model;

/** Lop Customer - Quan ly thong tin va diem tich luy cua khach hang. */
public class Customer {
    private int id;
    private String name;
    private String phoneNumber;
    private int loyaltyPoints;

    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
    }

    public Customer(int id, String name, String phoneNumber, int loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = loyaltyPoints;
    }

    public Object[] toRowData() {
        return new Object[]{id, name, phoneNumber, loyaltyPoints};
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
