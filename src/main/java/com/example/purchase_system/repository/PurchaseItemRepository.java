package com.example.purchase_system.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.example.purchase_system.entity.PurchaseItem;

public class PurchaseItemRepository {
    private Map<Integer, PurchaseItem> items;

    public PurchaseItemRepository() {
        items = new LinkedHashMap<>();
    }

    public int getNextId() {
        int maxId = 0;
        for(int id : items.keySet()) {
            if(id > maxId) maxId = id;
        }
        return maxId +1;
    }

    public PurchaseItem save(int purchaseId, int productId, int quantity, int purchasePrice) {
        if(quantity < 1) throw new IllegalArgumentException("Invalid quantity.");
        if(purchasePrice < 0) throw new IllegalArgumentException("Invalid price.");
        int newId = getNextId();
        PurchaseItem purItem = new PurchaseItem(newId, purchaseId, productId, quantity, purchasePrice);
        items.put(newId, purItem);
        return purItem;
    }

    public Optional<PurchaseItem> findById(int id) {
        return Optional.ofNullable(items.get(id));
    }
}
