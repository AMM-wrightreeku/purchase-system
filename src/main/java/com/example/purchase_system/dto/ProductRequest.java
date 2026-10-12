package com.example.purchase_system.dto;

public class ProductRequest {
    private Integer id;
    private String barcode;
    private String name;

    public ProductRequest() {}
    public ProductRequest(Integer id, String barcode, String name) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
    }

    public Integer getId() {return id;}
    public String getBarcode() {return barcode;}
    public String getName() {return name;}
}
