package com.example.purchase_system.entity;

import com.example.purchase_system.util.ModelStatus;

public class Product {
    private int id;
    private String barcode;
    private String name;
    private ModelStatus status;

    public Product(int id, String barcode, String name, ModelStatus status) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.status = status;
    }

    public Product(int id, String barcode, String name) {
        this(id, barcode, name, ModelStatus.ACTIVE);
    }

    public int getId() {return id;}
    public String getBarcode() {return barcode;}
    public String getName() {return name;}
    public ModelStatus getStatus() {return status;}
    public void setStatus(ModelStatus status) {this.status = status;}
}