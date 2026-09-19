package com.example.purchase_system.dto;

import java.time.LocalDate;


public class PurchaseSearchRequest {
    private Integer supplierId;
    private LocalDate startDate;
    private LocalDate endDate;

    public PurchaseSearchRequest() {}
    public PurchaseSearchRequest(Integer supplierId
                , LocalDate startDate, LocalDate endDate) {
        this.supplierId = supplierId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Integer getSupplierId() {return supplierId;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate;}

    public void setSupplierId(Integer supplierId) {this.supplierId = supplierId;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}
}
