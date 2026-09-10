package com.example.purchase_system.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Purchase;

public class PurchaseRepositoryTest {
    // 1. testSave
    @Test 
    void testSave() {
        PurchaseRepository purRep = new PurchaseRepository();
        LocalDate date = LocalDate.of(2026, 9, 10);
        Purchase pur = purRep.save(1, date, "SuperRobotWars");
        Assertions.assertEquals(1, pur.getId());
        Assertions.assertEquals(1, pur.getSupplierId());
        Assertions.assertEquals(LocalDate.of(2026, 9, 10), pur.getDate());
        Assertions.assertEquals("SuperRobotWars", pur.getNote());
    }
    // 2. testSaveWithoutNote
    @Test
    void testSaveWithoutNote() {
        // 此處註解程式碼有錯
        PurchaseRepository purRep = new PurchaseRepository();
        Purchase pur = purRep.save(1, LocalDate.of(2026, 9, 10));
        Assertions.assertNull(pur.getNote());
    }
    // 3. testFindById
    @Test
    void testFindById() {
        PurchaseRepository purRep = new PurchaseRepository();
        purRep.save(1, LocalDate.of(2026, 9, 10));
        Assertions.assertTrue(purRep.findById(1).isPresent());
        Assertions.assertFalse(purRep.findById(999).isPresent());
    }
}