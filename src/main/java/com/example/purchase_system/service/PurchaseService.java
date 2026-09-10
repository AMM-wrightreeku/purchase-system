package com.example.purchase_system.service;

import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;
import com.example.purchase_system.entity.Purchase;

import java.time.LocalDate;

public class PurchaseService {
    private SupplierRepository supplierRepository;
    private PurchaseRepository purchaseRepository;

    public PurchaseService(SupplierRepository supplierRepository,
                           PurchaseRepository purchaseRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public Purchase createPurchase(int supplierId, LocalDate date, String note) {
        // 1. 先搜尋是否為既有廠商，如無，則拋出例外給 user 決定處理
        if(supplierRepository.findById(supplierId).isEmpty()) throw new IllegalArgumentException("Supplier doesn't exist.");
        // 3. 存在，建立進貨資料
        return purchaseRepository.save(supplierId, date, note);
        
    }
}
