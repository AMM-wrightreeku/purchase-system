package com.example.purchase_system.dto;

import java.util.List;

import com.example.purchase_system.util.ModelStatus;

import java.time.LocalDate;

public class PurchaseDetailResponse {
    private int id;
    private int supplierId;
    private String supplierName;
    private LocalDate date;
    private String note;
    private List<PurchaseItemDetailResponse> items;
    private ModelStatus status;

    public PurchaseDetailResponse(
            int id, int supplierId, String supplierName
            , LocalDate date, String note, List<PurchaseItemDetailResponse> items, ModelStatus status) {
        this.id = id;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.date = date;
        this.note = note;
        this.items = items;
        this.status = status;
    }

    public int getId() {return id;}
    public int getSupplierId() {return supplierId;}
    public String getSupplierName() {return supplierName;}
    public LocalDate getDate() {return date;}
    public String getNote() {return note;}
    public List<PurchaseItemDetailResponse> getItems() {return items;}
    public ModelStatus getStatus() {return status;}
}