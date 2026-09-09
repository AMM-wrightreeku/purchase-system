package com.example.purchase_system.entity;

import java.time.LocalDate;

public class Purchase {
    private int id;
    private Supplier sup;
    private LocalDate date;
    private String note;

    public Purchase(int id, Suplier sup, LocalDate date, String note) {
        this.id = id;
        this.sup = sup;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }
    public Supplier getSupplier() {
        return sup;
    }
    public LocalDate getDate() {
        return date;
    }
    public String getNote() {
        return note;
    }
}
