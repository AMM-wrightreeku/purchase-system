package com.example.purchase_system.service;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.entity.Purchase;


public class PurchaseServiceTest {
@Test
void testSupplierExistOrNot() {
    SupplierRepository supRep = new SupplierRepository();
    PurchaseRepository purRep = new PurchaseRepository();
    ProductRepository pro = new ProductRepository();
    PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    PurchaseService purSer = new PurchaseService(supRep, purRep, pro, pItemRep);
    supRep.save("BANDAI");
    supRep.save("BANDAI NAMCO");
    Purchase pur = purSer.createPurchase(2, LocalDate.of(2026, 9, 11), "TEST");
    
    // check
    Assertions.assertEquals(2, pur.getSupplierId());
    Assertions.assertEquals(LocalDate.of(2026, 9, 11), pur.getDate());
    Assertions.assertEquals("TEST", pur.getNote());

    // supplierId doesn't exist
    Assertions.assertThrows(IllegalArgumentException.class,
    () -> purSer.createPurchase(3, LocalDate.of(2026, 9, 11), null));
}

@Test
void testDate() {
    SupplierRepository supRep = new SupplierRepository();
    PurchaseRepository purRep = new PurchaseRepository();
    ProductRepository pro = new ProductRepository();
    PurchaseItemRepository pItemRep = new PurchaseItemRepository();

    PurchaseService purSer = new PurchaseService(supRep, purRep, pro, pItemRep);
    supRep.save("BANDAI");
    
    // daet null
    Assertions.assertThrows(IllegalArgumentException.class,
    () -> purSer.createPurchase(1, null, "TEST"));
    }
}
