package com.example.purchase_system.repository;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
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

    public PurchaseItem save(int purchaseId, int productId, int quantity, int totalPrice) {
        // check
        if(quantity < 1) throw new IllegalArgumentException("Invalid quantity.");
        if(totalPrice < 0) throw new IllegalArgumentException("Invalid price.");
        // main motion
        // 呼叫 util 直接找 ID
        int newId = IdGenerator.getNextId(items.keySet());
        PurchaseItem purItem = new PurchaseItem(newId, purchaseId, productId, quantity, totalPrice);
        items.put(newId, purItem);
        return purItem;
    }

    public Optional<PurchaseItem> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }

    public Optional<PurchaseItem> findSameProduct(int purchaseId, int productId, int totalPrice) {
        for(PurchaseItem item : items.values()) {
            if(purchaseId == item.getPurchaseId() &&
               productId == item.getProductId() &&
               totalPrice == item.getTotalPrice()) {
                return Optional.of(item);
               }
        }
        return Optional.empty();
    }

    // find by purchaseId
    public List<PurchaseItem> findByPurchaseId(int purchaseId) {
        List<PurchaseItem> result = new ArrayList<>();

        for(PurchaseItem item : items.values()) {
            if(item.getPurchaseId() == purchaseId) {
                result.add(item);
            }
        }
        return result;
    }   
}
