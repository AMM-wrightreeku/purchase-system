package com.example.purchase_system.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.PurchaseItem;

public class PurchaseItemRepositoryTest {
    @Test 
    void testSave() {
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        // → id 是否從 1 開始
        Assertions.assertEquals(1, pItemRep.getNextId());
        
        PurchaseItem pItem = pItemRep.save(6, 5, 5, 35);
        // → 新增一筆後 id = 2 開始
        Assertions.assertEquals(2, pItemRep.getNextId());
        // → purchaseId 正確
        Assertions.assertEquals(6, pItem.getPurchaseId());
        // → productId 正確
        Assertions.assertEquals(5, pItem.getProductId());
        // → quantity 正確
        Assertions.assertEquals(5, pItem.getQuantity());
        // → purchasePrice 正確
        Assertions.assertEquals(35, pItem.getPurchasePrice());
    }

    @Test
    void testInvalidQuantity() {
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        // → quantity = 0 → Exception
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> pItemRep.save(6, 5, 0, 35));
        // → quantity = -1 → Exception
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> pItemRep.save(6, 5, -1, 35));
    }

    @Test
    void testInvalidPrice() {
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        // → purchasePrice = -1 → Exception
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> pItemRep.save(6, 5, 5, -1));
        // → purchasePrice = 0 → 可以成功
        PurchaseItem pItem = pItemRep.save(6, 5, 5, 0);
        Assertions.assertEquals(0, pItem.getPurchasePrice());
    }

    @Test
    void testFindById() {
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        pItemRep.save(6, 5, 5, 35);
        // → 存在 → isPresent()
        Assertions.assertTrue(pItemRep.findById(1).isPresent());
        // → 不存在 → isEmpty()
        Assertions.assertTrue(pItemRep.findById(999).isEmpty());
    }

    @Test 
    void testFindSameProduct() {
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        pItemRep.save(1, 2, 1, 100);
        pItemRep.save(1, 2, 1, 150);
        pItemRep.save(2, 2, 1, 100);

        // case1 exist , 1, 2, 100
        PurchaseItem testPItem = pItemRep.findSameProduct(1, 2, 100).get();
        Assertions.assertEquals(1, testPItem.getPurchaseId());
        Assertions.assertEquals(2, testPItem.getProductId());
        Assertions.assertEquals(100, testPItem.getPurchasePrice());
        // case2 empty , 1, 2, 999
        Assertions.assertTrue(pItemRep.findSameProduct(1, 2, 999).isEmpty());
        // case3 empty , 999, 2, 100
        Assertions.assertTrue(pItemRep.findSameProduct(999, 2, 100).isEmpty());
    }
}
