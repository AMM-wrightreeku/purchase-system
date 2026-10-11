package com.example.purchase_system.util;

public enum CsvStatusType {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String status;

    private CsvStatusType(String status) {
        this.status = status;
    }

    public String getStatus() {return status;}
}
