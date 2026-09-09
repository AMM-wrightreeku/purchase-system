package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;

import com.example.purchase_system.entity.Product;
import com.example.purchase_system.repository.ProductRepository;

public class ProductService {
    private ProductRepository productRep;

    public ProductService(ProductRepository productRep) {
        this.productRep = productRep;
    }

    // 創建商品
    public Product createProduct(String barcode, String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        if(barcode != null && productRep.findByBarcode(barcode).isPresent()) {
            throw new IllegalArgumentException("Barcode already exists");
        } else {
            return productRep.save(barcode, name);
        }
    }

    // ID 找商品
    public Optional<Product> findById(int id) {
        return productRep.findById(id);
    }

    // 條碼找商品
    public Optional<Product> findByBarcode(String barcode) {
        return productRep.findByBarcode(barcode);
    }

    // 名稱找商品
    public List<Product> findByName(String name) {
        return productRep.findByName(name);
    }

}
