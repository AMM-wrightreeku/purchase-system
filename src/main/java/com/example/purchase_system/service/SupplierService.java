package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.purchase_system.entity.Supplier;
import com.example.purchase_system.repository.SupplierRepository;

@Service
public class SupplierService {
    private final SupplierRepository supRep;

    public SupplierService(SupplierRepository supRep) {
        this.supRep = supRep;
    }

    public Supplier createSupplier(String name) {
        if(name == null || name.isBlank()) 
            throw new IllegalArgumentException("Name cannot be blank.");
        if(supRep.findByExactName(name).isPresent()) 
            throw new IllegalArgumentException("Name already exists.");
        
        return supRep.save(name);
    }

    public Optional<Supplier> findById(int id) {
        return supRep.findById(id);
    }

    public List<Supplier> findByName(String name) {
        return supRep.findByName(name);
    }

    public List<Supplier> findAll() {
        return supRep.findAll();
    }
}
