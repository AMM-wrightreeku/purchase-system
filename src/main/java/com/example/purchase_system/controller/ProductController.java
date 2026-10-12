package com.example.purchase_system.controller;

import java.util.List;

import com.example.purchase_system.entity.Product;
import com.example.purchase_system.service.ProductService;
import com.example.purchase_system.dto.ProductRequest;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/products")
public class ProductController { 
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request.getBarcode(), request.getName());
    }

    @PostMapping("/update")
    public Product updateProduct(@RequestBody ProductRequest request) {
        Integer id = request.getId();

        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }
        return productService.updateProduct(request.getId(), request.getBarcode(), request.getName());
    }

    @PostMapping("delete")
    public Product deleteProduct(@RequestBody ProductRequest request) {
        return productService.deleteProduct(request.getId());
    }

}
