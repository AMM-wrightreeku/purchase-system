package com.example.purchase_system.entity;

import com.example.purchase_system.util.ModelStatus;

public class Supplier {
    private int id;
    private String name;
    private ModelStatus status;

    public Supplier(int id, String name) {
        this(id, name, ModelStatus.ACTIVE);
    }

    public Supplier(int id, String name, ModelStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public ModelStatus getStatus() {return status;}
    public void setStatus(ModelStatus status) {this.status = status;}
}