package com.example.purchase_system.entity;

import java.time.LocalDate;

import com.example.purchase_system.util.ModelStatus;

public class Purchase {
    private int id;
    private int supplierId;
    private LocalDate date;
    private String note;
    private ModelStatus status;

    public Purchase(int id, int supplierId, LocalDate date, String note, ModelStatus status) {
        this.id = id;
        this.supplierId = supplierId;
        this.date = date;
        this.note = note;
        this.status = status;
    }

    public Purchase(int id, int supplierId, LocalDate date, String note) {
        this(id, supplierId, date, note, ModelStatus.ACTIVE);
    }

    public int getId() {return id;}
    public int getSupplierId() {return supplierId;}
    public LocalDate getDate() {return date;}
    public String getNote() {return note;}
    public ModelStatus getStatus() {return status;}
    public void setStatus(ModelStatus status) {this.status = status;}
}
