package com.example.purchase_system.entity;

public class Product {
    private int id;
    private String barcode;
    private String name;

    public Product(int id, String barcode, String name) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }
}