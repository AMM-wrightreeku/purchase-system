package com.example.purchase_system.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.purchase_system.entity.Supplier;
import com.example.purchase_system.util.CsvFileUtil;
import com.example.purchase_system.util.CsvStatusType;

@Repository
public class SupplierRepository {
    private Map<Integer, Supplier> suppliers;
    private int maxId = 0;

    public SupplierRepository() {
        this(true);
    }
    public SupplierRepository(boolean loadCsv) {
        suppliers = new LinkedHashMap<>();
        if(loadCsv) {
            loadSupplierFromCsv();
        }
    }

    public Supplier create(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be blank.");
        if (findByExactName(name).isPresent()) throw new IllegalArgumentException("The supplier name already exists");
        int id = this.maxId + 1;
        Supplier sup = new Supplier(id, name);
        saveSupplierToCsv(sup, CsvStatusType.CREATE);
        suppliers.put(id, sup);
        this.maxId = id;
        return sup;
    }

    public Supplier update(int id, String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("Name cannot be blank.");
        if (!findById(id).isPresent()) throw new IllegalArgumentException("The supplier does not exists");
        // 檢查唯一姓名
        Optional<Supplier> existName = findByExactName(newName);
        if (existName.isPresent() && existName.get().getId() != id) throw new IllegalArgumentException("The supplier name already exists");
        Supplier newSup = new Supplier(id, newName);
        saveSupplierToCsv(newSup, CsvStatusType.UPDATE);
        suppliers.put(id, newSup);
        return newSup;
    }

    public void delete(int id) {
        if (!findById(id).isPresent()) throw new IllegalArgumentException("The supplier does not exists");
        String lastName = suppliers.get(id).getName();
        Supplier delSup = new Supplier(id, lastName);
        saveSupplierToCsv(delSup, CsvStatusType.DELETE);
        suppliers.remove(id);
    }

    public Optional<Supplier> findById(int id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public Optional<Supplier> findByExactName(String name) {
        if(name == null || name.isBlank()) return Optional.empty();

        for (Supplier sup : suppliers.values()) {
            if (sup.getName() != null && name.equals(sup.getName())) {
                return Optional.of(sup);
            }
        }

        return Optional.empty();
    }
    
    public List<Supplier> findByName(String name) {
        List<Supplier> result = new ArrayList<>();
        if(name == null || name.isBlank()) return result;

        for (Supplier sup : suppliers.values()) {
            if (sup.getName().contains(name)) {
                result.add(sup);
            }
        }

        return result;
    }
    // 儲存 .csv
    private void saveSupplierToCsv(Supplier supplier, CsvStatusType type) {
        Path path = Path.of("data"
                            , "supplierList.csv");
        try {
            String line = type.getStatus() + ","
                        + supplier.getId() + ","
                        + supplier.getName()
                        + System.lineSeparator();
            Files.writeString(path, line, StandardCharsets.UTF_8
                                , StandardOpenOption.APPEND
            );
        } catch(IOException e) {
            throw new IllegalStateException("Cannot save supplierList.csv", e);
        }
    }
    // 讀取 .csv
    private void loadSupplierFromCsv() {
        try {
            List<String> lines = CsvFileUtil.loadDocument("data"
                                                        , "supplierList.csv"
                                                        , "STATUS,ID,NAME");
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.isBlank()) {continue;}
                String[] columns = line.split(",",3); // csv 靠 , 分隔屬性
                if(columns.length < 3) {throw new IllegalStateException(
                "supplierList.csv format error at line " + (i+1));
                }

                String status = columns[0].trim();
                int id = Integer.parseInt(columns[1].trim());
                String name = columns[2].trim();
                Supplier supplier = new Supplier(id, name);
                
                if(CsvStatusType.CREATE.getStatus().equals(status)
                    && !suppliers.containsKey(id)
                    && id > maxId) {
                        suppliers.put(id, supplier);
                        this.maxId = id;
                } else if(CsvStatusType.UPDATE.getStatus().equals(status)
                    && suppliers.containsKey(id)) {
                        suppliers.put(id, supplier);
                } else if(CsvStatusType.DELETE.getStatus().equals(status)
                    && suppliers.containsKey(id)) {
                        suppliers.remove(id);
                } else {
                    throw new IllegalStateException("supplierList.csv format error at line " + (i+1));
                }
            }          
        } catch (IOException e) {
            throw new IllegalStateException(
                "Cannot read supplierList.csv",
                e
            );
        }
    }

    public List<Supplier> findAll() {
        return new ArrayList<>(suppliers.values());
    }
}