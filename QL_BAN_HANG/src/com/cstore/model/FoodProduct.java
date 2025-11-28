package com.cstore.model;

/** Lop FoodProduct - Ke thua tu Product. */
public class FoodProduct extends Product {
    private String expiryDate;

    public FoodProduct(String id, String name, double basePrice, int quantity, String expiryDate) {
        super(id, name, basePrice, quantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public double calculatePrice() {
        return getBasePrice() * 1.05; // 5% VAT
    }

    @Override
    public String getTypeName() { return "Do An"; }
    
    @Override
    public String getExtraInfo() { return expiryDate; }
}