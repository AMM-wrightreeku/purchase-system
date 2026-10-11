package com.example.purchase_system.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Supplier;

public class SupplierRepositoryTest {
    @Test
    void testSave() {
        SupplierRepository supRep = new SupplierRepository(false);
        Supplier sup1 = supRep.create("Granzon");
        Supplier sup2 = supRep.create("Cyberster");
        Assertions.assertEquals(1, sup1.getId());
        Assertions.assertEquals(2, sup2.getId());
    }

    @Test 
    void testDuplicateName() {
        SupplierRepository supRep = new SupplierRepository(false);
        supRep.create("NeoGranzon");
        Assertions.assertThrows(IllegalArgumentException.class, 
        () -> supRep.create("NeoGranzon"));
    }

    @Test
    void testFindById() {
        SupplierRepository supRep = new SupplierRepository(false);
        supRep.create("NeoGranzon");
        Assertions.assertTrue(supRep.findById(1).isPresent());
    }

    @Test
    void testFindByName() {
        SupplierRepository supRep = new SupplierRepository(false);
        supRep.create("NeoGranzon");
        supRep.create("Granzon");

        boolean result = false;
        for(Supplier sup : supRep.findByName("Granzon")) {
            if(sup.getName().contains("Granzon")) result = true;
            else {
                result = false;
                break; 
            }
        }
        Assertions.assertTrue(result);
    }
}
