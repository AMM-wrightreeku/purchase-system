package com.example.purchase_system.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.purchase_system.entity.Supplier;

public class SupplierRepository {

    private Map<Integer, Supplier> suppliers;

    public SupplierRepository() {
        suppliers = new LinkedHashMap<>();
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
}