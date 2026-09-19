package com.example.purchase_system.dto;

public class ProductRequest {
    private String barcode;
    private String name;

    public ProductRequest() {}
    public ProductRequest(String barcode, String name) {
        this.barcode = barcode;
        this.name = name;
    }

    public String getBarcode() {return barcode;}
    public String getName() {return name;}
}
