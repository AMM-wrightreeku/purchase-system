package com.example.purchase_system.service;

import java.time.LocalDate;

import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;

public class PurchaseService {
    private SupplierRepository supplierRepository;
    private PurchaseRepository purchaseRepository;
    private ProductRepository productRepository;
    private PurchaseItemRepository pItemRepository;

    public PurchaseService(SupplierRepository supplierRepository,
                           PurchaseRepository purchaseRepository,
                           ProductRepository productRepository,
                           PurchaseItemRepository pItemRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.pItemRepository = pItemRepository;
    }

    public Purchase createPurchase(int supplierId, LocalDate date, String note) {
        // 1. 先搜尋是否為既有廠商，如無，則拋出例外給 user 決定處理
        if(supplierRepository.findById(supplierId).isEmpty()) throw new IllegalArgumentException("Supplier doesn't exist.");
        // 3. 存在，建立進貨資料
        return purchaseRepository.save(supplierId, date, note);   
    }

    public PurchaseItem addPurchaseItem(int purchaseId, int productId, int quantity, int purchasePrice) {
        if(purchaseRepository.findById(purchaseId).isEmpty()) throw new IllegalArgumentException("Purchase does not exist.");
        if(productRepository.findById(productId).isEmpty()) throw new IllegalArgumentException("Product does not exist.");
        return pItemRepository.save(purchaseId, productId, quantity, purchasePrice);
    }
}
