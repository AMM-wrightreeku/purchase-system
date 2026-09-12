package com.example.purchase_system.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Supplier;
import com.example.purchase_system.repository.SupplierRepository;

public class SupplierServiceTest {
    // createSupplier
    // 1.create success
    // 2. id / name is correct
    @Test 
    void testCreate() {
        SupplierRepository supRep = new SupplierRepository(false);
        SupplierService supSer = new SupplierService(supRep);
        supSer.createSupplier("BANDAI"); // id 1, BANDAI
        supSer.createSupplier("namco"); // id 2, namco
        Assertions.assertEquals("BANDAI", supSer.findById(1).get().getName());
        Assertions.assertEquals("namco", supSer.findById(2).get().getName());
    }

    // duplicate name
    // 1. build duplicate name
    // 2. IllegalArgumentException
    @Test 
    void testDuplicateName() {
        SupplierRepository supRep = new SupplierRepository(false);
        SupplierService supSer = new SupplierService(supRep);
        supSer.createSupplier("BANDAI");
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> supSer.createSupplier("BANDAI"));
    }

    // findById
    // 1. id exists -> isPresent()
    // 2. not exists -> isEmpty()
    @Test 
    void testFindById() {
        SupplierRepository supRep = new SupplierRepository(false);
        SupplierService supSer = new SupplierService(supRep);
        supSer.createSupplier("BANDAI");
        Assertions.assertTrue(supSer.findById(1).isPresent());
        Assertions.assertTrue(supSer.findById(999).isEmpty());
    }

    // findByName
    // 1. multi result
    // 2. cannot found -> empty
    // 3. null / "" / " " -> empty
    @Test 
    void testFindByName() {
        SupplierRepository supRep = new SupplierRepository(false);
        SupplierService supSer = new SupplierService(supRep);
        supSer.createSupplier("BANDAI");
        supSer.createSupplier("BANDAI NAMCO");
        supSer.createSupplier("namco");

        boolean result = false;
        for(Supplier sup : supSer.findByName("BANDAI")) {
            if(sup.getName().contains("BANDAI")) result = true;
            else {
                result = false;
                break;
            }
        }
        Assertions.assertTrue(result);
        Assertions.assertEquals(2, supSer.findByName("BANDAI").size());

        Assertions.assertTrue(supSer.findByName("巨岡").isEmpty());
        Assertions.assertTrue(supSer.findByName(null).isEmpty());
        Assertions.assertTrue(supSer.findByName("").isEmpty());
        Assertions.assertTrue(supSer.findByName(" ").isEmpty());
    }
}
