package com.example.purchase_system.service;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;


public class PurchaseServiceTest {
    @Test
    void testSupplierExistOrNot() {
        SupplierRepository supRep = new SupplierRepository();
        PurchaseRepository purRep = new PurchaseRepository();
        ProductRepository proRep = new ProductRepository();
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
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
        ProductRepository proRep = new ProductRepository();
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();

        PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
        supRep.save("BANDAI");
        
        // daet null
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> purSer.createPurchase(1, null, "TEST"));
    }

    @Test
    void testPurchaseAndProductExistOrNot() {
        SupplierRepository supRep = new SupplierRepository();
        PurchaseRepository purRep = new PurchaseRepository();
        ProductRepository proRep = new ProductRepository();
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
        supRep.save("NAMCO");
        supRep.save("BANDAI");
        supRep.save("BANDAI NAMCO");
        proRep.save("0079", "Gundam");
        proRep.save(null, "BattleSpirits");
        purSer.createPurchase(3, LocalDate.of(2026, 9, 11), "New Brand");

        // Purchase & Product exist
        PurchaseItem pItem = purSer.addPurchaseItem(1, 2, 6, 1500);
        Assertions.assertEquals(1, pItem.getPurchaseId());  // purchaseId
        Assertions.assertEquals(2, pItem.getProductId());  // productId
        Assertions.assertEquals(6, pItem.getQuantity());  // quantity
        Assertions.assertEquals(1500, pItem.getPurchasePrice());  // price

        // Purchase doesn't exist
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.addPurchaseItem(2, 2, 6, 1500));

        // Product doesn't exist
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.addPurchaseItem(1, 3, 6, 1500));
        
        // quantity & price invaiid
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.addPurchaseItem(1, 2, 0, 1500));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.addPurchaseItem(1, 2, 1, -20));
    }
}
