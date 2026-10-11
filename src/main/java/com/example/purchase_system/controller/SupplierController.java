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

    @PostMapping("/create")
    public Supplier createSupplier(@RequestBody SupplierRequest request) {
        return supplierService.createSupplier(request.getName());
    }

    @PostMapping("/update")
    public Supplier updateSupplier(@RequestBody SupplierRequest request) {
        Integer id = request.getId();

        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        return supplierService.updateSupplier(request.getId(), request.getName());
    }

    @PostMapping("/delete")
    public Supplier deleteSupplier(@RequestBody SupplierRequest request) {
        Integer id = request.getId();

        if (id == null || id < 1) {
            throw new IllegalArgumentException("Invalid supplier ID.");
        }

        return supplierService.deleteSupplier(request.getId());
    }


    @GetMapping
    public List<Supplier> returnAllSuppliers() {
        return supplierService.findAll();
    }
}
