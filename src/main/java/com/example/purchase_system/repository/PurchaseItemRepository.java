package com.example.purchase_system.repository;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;


import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.util.IdGenerator;
import com.example.purchase_system.util.CsvFileUtil;

@Repository
public class PurchaseItemRepository {
    private Map<Integer, PurchaseItem> items;

    public PurchaseItemRepository() {
        items = new LinkedHashMap<>();
        loadPurchaseItemFromCsv();
    }

    public PurchaseItem save(int purchaseId, int productId, int quantity, int totalPrice) {
        // check
        if(quantity < 1) throw new IllegalArgumentException("Invalid quantity.");
        if(totalPrice < 0) throw new IllegalArgumentException("Invalid price.");
        // main motion
        // 呼叫 util 直接找 ID
        int newId = IdGenerator.getNextId(items.keySet());
        PurchaseItem purItem = new PurchaseItem(newId, purchaseId, productId, quantity, totalPrice);
        savePurchaseItemToCsv(purItem);
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

    private void savePurchaseItemToCsv(PurchaseItem item) {
        Path path = Path.of("data", "purchaseItemList.csv");
        try {
            String line = "CREATE,"
                            + item.getId() + ","
                            + item.getPurchaseId() + ","
                            + item.getProductId() + ","
                            + item.getQuantity() + ","
                            + item.getTotalPrice()
                            + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8
                                , StandardOpenOption.APPEND);
        } catch(IOException e) {
            throw new IllegalStateException("Cannot save purchaseItemList.csv", e);
        }
    }

    private void loadPurchaseItemFromCsv() {
        try {
            List<String> lines = CsvFileUtil.loadLines("data"
                                                        , "purchaseItemList.csv"
                                                        , "STATUS,ID,PURCHASE_ID,PRODUCT_ID,QUANTITY,TOTAL_PRICE");
            for(int i = 1; i < lines.size(); i++) {
                String line =lines.get(i);
                if(line.isBlank()) {continue;}
                String[] columns = line.split(",", 6);
                if(columns.length < 6) {throw new IllegalArgumentException(
                    "purchaseItemList.csv format error at line " + (i+1));
                }
                String status = columns[0].trim();
                if(!"CREATE".equals(status)) {continue;}
                int id = Integer.parseInt(columns[1].trim());
                int purchaseId = Integer.parseInt(columns[2].trim());
                int productId = Integer.parseInt(columns[3].trim());
                int qty = Integer.parseInt(columns[4].trim());
                int price = Integer.parseInt(columns[5].trim());
                PurchaseItem item = new PurchaseItem(id, purchaseId, productId, qty, price);
                items.put(id, item);
            } 
        } catch(IOException e) {
            throw new IllegalStateException("Cannot read purchaseItemList.csv", e);
        }
    }
    
}
