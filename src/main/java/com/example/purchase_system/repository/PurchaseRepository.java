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
import com.example.purchase_system.util.ModelStatus;
import com.example.purchase_system.util.CsvFileUtil;
import com.example.purchase_system.util.CsvStatusType;


@Repository
public class PurchaseRepository {
    private Map<Integer, Purchase> purchases;
    private int maxId = 0;

    public PurchaseRepository() {
        purchases = new LinkedHashMap<>();
        loadPurchaseFromCsv();
    }

    public Purchase create(int supplierId, LocalDate date, String note) {
        CsvStatusType type = CsvStatusType.CREATE;
        validCrudParameter(null, supplierId, date, type);
        validCrudData(null, supplierId, date, type);

        int id = this.maxId + 1;

        Purchase purchase = new Purchase(id
                                       , supplierId
                                       , date
                                       , note);
        savePurchaseToCsv(purchase, type);
        purchases.put(id, purchase);
        this.maxId = id;
        return purchase;
    }

    public Purchase create(int supplierId, LocalDate date) {
        return this.create(supplierId, date, null);
    }
    
    public Purchase delete(Integer id) {
        CsvStatusType type = CsvStatusType.DELETE;
        validCrudParameter(id, null, null, type);
        validCrudData(id, null, null, type);

        Purchase delPur = new Purchase(id 
                                      , findById(id).get().getSupplierId()
                                      , findById(id).get().getDate()
                                      , "【進貨單已刪除】" + findById(id).get().getNote());
        savePurchaseToCsv(delPur, type);
        purchases.put(id, delPur);
        return delPur;
    }

    private void validCrudParameter(Integer id, Integer supplierId, LocalDate date, CsvStatusType type) {
        switch(type) {
            case DELETE: 
                if(id == null) {
                    throw new IllegalArgumentException("Id is null");
                }
                break;
                
            case CREATE:
                if(supplierId == null) {
                    throw new IllegalArgumentException("Supplier Id is null.");
                }
                if(date == null) {
                    throw new IllegalArgumentException("Date cannot be blank.");
                }
                break;
            default: throw new IllegalArgumentException("Wrong csv status.");
        }
    }

    private void validCrudData(Integer id, Integer supplierId, LocalDate date, CsvStatusType type) {
        switch(type) {
            case DELETE:
                if(!findById(id).isPresent()) {
                    throw new IllegalArgumentException("Id is wrong.");
                }
                if(findById(id).get().getStatus() == ModelStatus.DOWN) {
                    throw new IllegalArgumentException("Purchase cannot be edited.");
                }
                break;
            case CREATE:
                break;
            default: throw new IllegalArgumentException("Wrong csv data status, please check.");
        }
    }

    private void savePurchaseToCsv(Purchase purchase, CsvStatusType type) {
        Path path = Path.of("data", "purchaseList.csv");
        try {
            String line = type.getStatus() + ","
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
            List<String> lines = CsvFileUtil.loadDocument("data"
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
                int id = Integer.parseInt(columns[1].trim());
                int supplierId = Integer.parseInt(columns[2].trim());
                LocalDate date = LocalDate.parse(columns[3].trim());
                String note = columns[4];
                Purchase purchase = new Purchase(id, supplierId, date, note);

                if(CsvStatusType.CREATE.getStatus().equals(status)
                    && !purchases.containsKey(id)
                    && id > maxId) {
                        purchases.put(id, purchase);
                        this.maxId = id;
                } else if(CsvStatusType.DELETE.getStatus().equals(status)
                    && purchases.containsKey(id)
                    && purchases.get(id).getStatus().equals(ModelStatus.ACTIVE)) {
                        purchase.setStatus(ModelStatus.DOWN);
                        purchases.put(id, purchase);
                } else {
                    throw new IllegalStateException("purchaseList.csv format error at line " + (i+1));                }
            }
        } catch(IOException e) {
            throw new IllegalStateException("Cannot read purchaseList.csv", e);
        }
    }

    public Optional<Purchase> findById(int id) {
        return Optional.ofNullable(purchases.get(id));
    }

    public List<Purchase> searchPurchases(Integer supplierId, LocalDate startDate, LocalDate endDate) {
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
