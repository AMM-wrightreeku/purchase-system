package com.example.purchase_system.dto;

public class SupplierRequest {
    private Integer id;
    private String name;

    public SupplierRequest() {}
    public SupplierRequest(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public SupplierRequest(Integer id) {
        this(id, null);
    }

    public SupplierRequest(String name) {
        this(null, name);
    }

    public Integer getId() {return id;}
    public String getName() {return name;}
}
