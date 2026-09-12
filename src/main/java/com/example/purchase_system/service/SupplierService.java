package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;

import com.example.purchase_system.entity.Supplier;
import com.example.purchase_system.repository.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    private SupplierRepository supRep;

    public SupplierService(SupplierRepository supRep) {
        this.supRep = supRep;
    }

    // 新增供應商
    public Supplier createSupplier(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        
        if(supRep.findByExactName(name).isPresent()) {
            throw new IllegalArgumentException("Name already exists.");
        }
        return supRep.save(name);
    }
    // 找ID
    public Optional<Supplier> findById(int id) {
        return supRep.findById(id);
    }
    // 找名字
    public List<Supplier> findByName(String name) {
        return supRep.findByName(name);
    }
    // findALL
    public List<Supplier> findAll() {
        return supRep.findAll();
    }
}
