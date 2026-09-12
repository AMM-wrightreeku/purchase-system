package com.example.purchase_system.dto;

public class SupplierRequest {
    private String name;

    public SupplierRequest() {}

    public SupplierRequest(String name) {
        this.name = name;
    }

    public String getName() {return name;}
}
