package com.example.purchase_system.dto;

import java.time.LocalDate;
import java.util.List;

public class PurchaseRequest {
    private Integer supplierId;
    private LocalDate purchaseDate;
    private String note;
    private List<PurchaseItemRequest> items;

    public PurchaseRequest(Integer supplierId, LocalDate purchaseDate, String note
                            , List<PurchaseItemRequest> items) {
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.note = note;
        this.items = items;
    }

    public Integer getSupplierId() {return supplierId;}
    public LocalDate getPurchaseDate() {return purchaseDate;}
    public String getNote() {return note;}
    public List<PurchaseItemRequest> getItems() {return items;}
}
