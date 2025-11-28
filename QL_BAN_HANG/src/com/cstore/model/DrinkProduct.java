package com.cstore.model;

/** Lop DrinkProduct - Ke thua tu Product. */
public class DrinkProduct extends Product {
    private double volumeLiter;

    public DrinkProduct(String id, String name, double basePrice, int quantity, double volumeLiter) {
        super(id, name, basePrice, quantity);
        this.volumeLiter = volumeLiter;
    }

    @Override
    public double calculatePrice() {
        return getBasePrice() * 1.02; // 2% phi moi truong
    }

    @Override
    public String getTypeName() { return "Do Uong"; }
    
    @Override
    public String getExtraInfo() { return String.valueOf(volumeLiter); }
}