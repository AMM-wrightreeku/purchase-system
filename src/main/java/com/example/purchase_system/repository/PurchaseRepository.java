package com.example.purchase_system.repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.util.IdGenerator;

import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepository {
    private Map<Integer, Purchase> purchases;

    public PurchaseRepository() {
        purchases = new LinkedHashMap<>();
    }

    public Purchase save(int supplierId, LocalDate date, String note) {
        if(date == null) throw new IllegalArgumentException("Date cannot be blank.");
        // 呼叫 util 直接找 ID
        int newId = IdGenerator.getNextId(purchases.keySet());
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
