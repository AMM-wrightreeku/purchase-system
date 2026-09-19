package com.example.purchase_system.repository;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.util.IdGenerator;
import com.example.purchase_system.util.CsvFileUtil;


@Repository
public class PurchaseRepository {
    private Map<Integer, Purchase> purchases;

    public PurchaseRepository() {
        purchases = new LinkedHashMap<>();
        loadPurchaseFromCsv();
    }

    public Purchase save(int supplierId, LocalDate date, String note) {
        // check
        if(date == null) throw new IllegalArgumentException("Date cannot be blank.");
        // main motion
        // 呼叫 util 直接找 ID
        int newId = IdGenerator.getNextId(purchases.keySet());
        Purchase purchase = new Purchase(newId, supplierId, date, note);
        savePurchaseToCsv(purchase);
        purchases.put(newId, purchase);
        return purchase;
    }

    public Purchase save(int supplierId, LocalDate date) {
        return this.save(supplierId, date, null);
    }

    public Optional<Purchase> findById(int id) {
        return Optional.ofNullable(purchases.get(id));
    }

    private void savePurchaseToCsv(Purchase purchase) {
        Path path = Path.of("data", "purchaseList.csv");
        try {
            String line = "CREATE,"
                            + purchase.getId() + ","
                            + purchase.getSupplierId() + ","
                            + purchase.getDate() + ","
                            + (((purchase.getNote()) == null) ? "" : purchase.getNote())
                            + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8
                                , StandardOpenOption.APPEND);
        } catch(IOException e) {
            throw new IllegalStateException("Cannot save purchaseList.csv", e);
        }
    }

    private void loadPurchaseFromCsv() {
        try {
            List<String> lines = CsvFileUtil.loadLines("data"
                                                    , "purchaseList.csv"
                                                    , "STATUS,ID,SUPPLIER_ID,DATE,NOTE");
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.isBlank()) {continue;}
                String[] columns = line.split(",", 5);
                if(columns.length < 5) {throw new IllegalArgumentException(
                    "purchaseList.csv format error at line " + (i+1));
                }
                String status = columns[0].trim();
                if(!"CREATE".equals(status)) {continue;}
                int id = Integer.parseInt(columns[1].trim());
                int supplierId = Integer.parseInt(columns[2].trim());
                LocalDate date = LocalDate.parse(columns[3].trim());
                String note = columns[4];
                Purchase purchase = new Purchase(id, supplierId, date, note);
                purchases.put(id, purchase);
            }
        } catch(IOException e) {
            throw new IllegalStateException("Cannot read purchaseList.csv", e);
        }
    }

    public List<Purchase> search(Integer supplierId, LocalDate startDate, LocalDate endDate) {
        List<Purchase> result = new ArrayList<>();
        for(Purchase purchase : purchases.values()) {
            if(supplierId != null && supplierId != purchase.getSupplierId()) {continue;}
            if(startDate != null && startDate.isAfter(purchase.getDate())) {continue;}
            if(endDate != null && endDate.isBefore(purchase.getDate())) {continue;}
            result.add(purchase);
        }
        return result;
    }
}
