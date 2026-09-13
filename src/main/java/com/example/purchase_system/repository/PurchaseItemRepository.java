package com.example.purchase_system.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.util.IdGenerator;

@Repository
public class PurchaseItemRepository {
    private Map<Integer, PurchaseItem> items;

    public PurchaseItemRepository() {
        items = new LinkedHashMap<>();
    }

    public PurchaseItem save(int purchaseId, int productId, int quantity, int purchasePrice) {
        // check
        if(quantity < 1) throw new IllegalArgumentException("Invalid quantity.");
        if(purchasePrice < 0) throw new IllegalArgumentException("Invalid price.");
        // main motion
        // 呼叫 util 直接找 ID
        int newId = IdGenerator.getNextId(items.keySet());
        PurchaseItem purItem = new PurchaseItem(newId, purchaseId, productId, quantity, purchasePrice);
        items.put(newId, purItem);
        return purItem;
    }

    public Optional<PurchaseItem> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    public Optional<PurchaseItem> findSameProduct(int purchaseId, int productId, int purchasePrice) {
        for(PurchaseItem item : items.values()) {
            if(purchaseId == item.getPurchaseId() &&
               productId == item.getProductId() &&
               purchasePrice == item.getPurchasePrice()) {
                return Optional.of(item);
               }
        }
        return Optional.empty();
    }
}
