package com.example.purchase_system.entity;

import java.time.LocalDate;

public class Purchase {
    private int id;
    private int supplierId;
    private LocalDate date;
    private String note;

    public Purchase(int id, int supplierId, LocalDate date, String note) {
        this.id = id;
        this.supplierId = supplierId;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }
    public int getSupplierId() {
        return supplierId;
    }
    public LocalDate getDate() {
        return date;
    }
    public String getNote() {
        return note;
    }
}
