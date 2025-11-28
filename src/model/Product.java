package com.cstore.model;

/** Lop Product - Lop co so cho tat ca san pham. */
public abstract class Product {
    private String id;
    private String name;
    private double basePrice;
    private int quantity;

    public Product(String id, String name, double basePrice, int quantity) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.quantity = quantity;
    }

    public abstract double calculatePrice();

    public Object[] toRowData() {
        return new Object[]{id, name, getTypeName(), String.format("%,.0f", basePrice), quantity, String.format("%,.0f", calculatePrice())};
    }

    public abstract String getTypeName();
    public abstract String getExtraInfo();

    public String getId() { return id; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
