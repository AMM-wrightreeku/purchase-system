package com.example.purchase_system.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import com.example.purchase_system.entity.Product;

public class ProductRepository {

    private Map<Integer, Product> products;

    public ProductRepository() {
        products = new LinkedHashMap<>();
    }

    public int getNextId() {
        int maxId = 0;

        for (int id : products.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }

        return maxId + 1;
    }

    public Product save(String barcode, String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }

        if (barcode != null && findByBarcode(barcode).isPresent()) {
            throw new IllegalArgumentException("Barcode already exists");
        }

        int id = getNextId();

        Product product = new Product(id, barcode, name);

        products.put(id, product);

        return product;
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<Product> findByBarcode(String barcode) {

        for (Product product : products.values()) {
            if (product.getBarcode() != null
                    && product.getBarcode().equals(barcode)) {
                return Optional.of(product);
            }
        }

        return Optional.empty();
    }

    public List<Product> findByName(String name) {

        List<Product> result = new ArrayList<>();

        if (name == null || name.isBlank()) {
            return result;
        }

        for (Product product : products.values()) {
            if (product.getName().contains(name)) {
                result.add(product);
            }
        }

        return result;
    }
}