package com.example.purchase_system.repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.example.purchase_system.entity.Purchase;

public class PurchaseRepository {
    private Map<Integer, Purchase> purchases;

    public PurchaseRepository() {
        purchases = new LinkedHashMap<>();
    }

    // 產生 id
    public int getNextId() {
        int maxId = 0;
        for(int id : purchases.keySet()) {
            if(id > maxId) maxId = id;
        }
        return maxId +1;
    }
    
    public Purchase save(int supplierId, LocalDate date, String note) {
        if(date == null) throw new IllegalArgumentException("Date cannot be blank.");
        int newId = getNextId();
        Purchase purchase = new Purchase(newId, supplierId, date, note);
        purchases.put(newId, purchase);
        return purchase;
    }

    public Purchase save(int supplierId, LocalDate date) {
        return this.save(supplierId, date, null);
    }

    public Optional<Purchase> findById(int id) {
        return Optional.ofNullable(purchases.get(id));
    }

}
