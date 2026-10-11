package com.example.purchase_system.util;

public enum ModelStatus {
    ACTIVE("ACTIVE"),
    DOWN("DOWN");

    private final String status;

    private ModelStatus(String status) {
        this.status = status;
    }

    public String getStatus() {return status;}

}
