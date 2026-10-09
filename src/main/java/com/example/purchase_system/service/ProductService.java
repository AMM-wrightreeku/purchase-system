package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


import com.example.purchase_system.entity.Product;
import com.example.purchase_system.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRep;

    public ProductService(ProductRepository productRep) {
        this.productRep = productRep;
    }

    public Product createProduct(String barcode, String name) {
        if(name == null || name.isBlank()) 
            throw new IllegalArgumentException("Name cannot be blank.");
        if(barcode != null && productRep.findByBarcode(barcode).isPresent()) 
            throw new IllegalArgumentException("Barcode already exists");

        return productRep.save(barcode, name);
    }

    public Optional<Product> findById(int id) {
        return productRep.findById(id);
    }

    public Optional<Product> findByBarcode(String barcode) {
        return productRep.findByBarcode(barcode);
    }

    public List<Product> findByName(String name) {
        return productRep.findByName(name);
    }

    public List<Product> findAll() {
        return productRep.findAll();
    }

}
