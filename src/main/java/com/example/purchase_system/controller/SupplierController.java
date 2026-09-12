package com.example.purchase_system.controller;

import com.example.purchase_system.service.SupplierService;
import com.example.purchase_system.dto.SupplierRequest;
import com.example.purchase_system.entity.Supplier;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public Supplier createSupplier(@RequestBody SupplierRequest request) {
        return supplierService.createSupplier(request.getName());
    }

    @GetMapping
    public List<Supplier> returnAllSuppliers() {
        return supplierService.findAll();
    }
}
