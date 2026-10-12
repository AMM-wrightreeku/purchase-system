package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


import com.example.purchase_system.entity.Product;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.util.ModelStatus;

@Service
public class ProductService {
    private final ProductRepository productRep;

    public ProductService(ProductRepository productRep) {
        this.productRep = productRep;
    }

    public Product createProduct(String barcode, String name) {
        if(barcode != null && productRep.findByBarcode(barcode).isPresent()) 
            throw new IllegalArgumentException("Barcode already exists");
        if(name == null || name.isBlank()) 
            throw new IllegalArgumentException("Name cannot be blank.");

        return productRep.create(barcode, name);
    }

    public Product updateProduct(Integer id, String barcode, String name) {
        if(id == null || !productRep.findById(id).isPresent()) {
            throw new IllegalArgumentException("Id is wrong.");
        }
        if(productRep.findById(id).get().getStatus() == ModelStatus.DOWN) {
            throw new IllegalArgumentException("Product cannot be edited.");
        }
        if(barcode != null && productRep.findByBarcode(barcode).isPresent()) {
            if(!productRep.findByBarcode(barcode).get().getBarcode().equals(barcode)) {
                throw new IllegalArgumentException("Barcode already exists");
            }
        }
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        

        return productRep.update(id, barcode, name);
    }

    public Product deleteProduct(Integer id) {
        if(id == null || !productRep.findById(id).isPresent()) {
            throw new IllegalArgumentException("Id is wrong.");
        }
        if(productRep.findById(id).get().getStatus() == ModelStatus.DOWN) {
            throw new IllegalArgumentException("Product cannot be edited.");
        }

        return productRep.delete(id);
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
