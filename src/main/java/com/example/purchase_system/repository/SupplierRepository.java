package com.example.purchase_system.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.purchase_system.entity.Supplier;

import org.springframework.stereotype.Repository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
public class SupplierRepository {

    private Map<Integer, Supplier> suppliers;

    public SupplierRepository() {
        this(true);
    }

    public SupplierRepository(boolean loadCsv) {
        suppliers = new LinkedHashMap<>();
        if(loadCsv) {
            loadSuppliersFromCsv();
        }
    }

    public int getNextId() {

        int maxId = 0;

        for (int id : suppliers.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }

        return maxId + 1;
    }

    public Supplier save(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                "Name cannot be blank."
            );
        }

        if (findByExactName(name).isPresent()) {
            throw new IllegalArgumentException(
                "The supplier name already exists"
            );
        }

        int id = getNextId();

        Supplier sup = new Supplier(id, name);

        suppliers.put(id, sup);

        return sup;
    }

    public Optional<Supplier> findById(int id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public Optional<Supplier> findByExactName(String name) {

        for (Supplier sup : suppliers.values()) {
            if (sup.getName() != null
                    && sup.getName().equals(name)) {
                return Optional.of(sup);
            }
        }

        return Optional.empty();
    }

    public List<Supplier> findByName(String name) {

        List<Supplier> result = new ArrayList<>();

        if (name == null || name.isBlank()) {
            return result;
        }

        for (Supplier sup : suppliers.values()) {
            if (sup.getName().contains(name)) {
                result.add(sup);
            }
        }

        return result;
    }

    private void loadSuppliersFromCsv() {
        Path path = Path.of("data", "supplierList.csv");
        try {
            List<String> lines = Files.readAllLines(
                path,
                StandardCharsets.UTF_8
            );
            for(int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.isBlank()) {continue;}
            String[] columns = line.split(",",2); // csv 靠 , 分隔屬性
            if(columns.length < 2) {throw new IllegalStateException(
                "supplierList.csv format error at line " + (i+1)
                );
            }
            int id = Integer.parseInt(columns[0].trim());
            String name = columns[1].trim();
            Supplier supplier = new Supplier(id, name);
            suppliers.put(id, supplier);
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