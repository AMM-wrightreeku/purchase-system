package com.example.purchase_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import com.example.purchase_system.dto.PurchaseItemRequest;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.entity.Product;
import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.entity.Supplier;
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

    @Test 
    void testCreateFullPurchase() {
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

        PurchaseItemRequest item = new PurchaseItemRequest(
            1, "0079", "Gundam", 3, 100);
        PurchaseRequest request = new PurchaseRequest(
            2, LocalDate.of(2026, 9, 12), "TEST", List.of(item));
            Purchase result = purSer.createFullPurchase(request);

            // Correct product and supplier
            Product product = proRep.findById(1).get();
            Supplier supplier = supRep.findById(2).get();

            Assertions.assertEquals(supplier.getId(), result.getSupplierId());
            Assertions.assertEquals(LocalDate.of(2026, 9, 12),  result.getDate());
            Assertions.assertEquals("TEST", result.getNote());

            // TEST purchase build
            Optional<PurchaseItem> savedItem = pItemRep.findSameProduct(
                result.getId(), product.getId(), 100
            );
            Assertions.assertTrue(savedItem.isPresent());
            Assertions.assertEquals(3, savedItem.get().getQuantity());
    }

    @Test
    void testCreateFullPurchaseWithNewProduct() {
        SupplierRepository supRep = new SupplierRepository();
        PurchaseRepository purRep = new PurchaseRepository();
        ProductRepository proRep = new ProductRepository();
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
        Supplier sup1 = supRep.save("NAMCO");
        Supplier sup2 = supRep.save("BANDAI");
        Supplier sup3 = supRep.save("BANDAI NAMCO");
        Product pro1 = proRep.save("0079", "Gundam");
        Product pro2 = proRep.save(null, "BattleSpirits");
        // 再來測試的應該是從 id = 3 的 product 開始
        PurchaseItemRequest item = new PurchaseItemRequest(
            null, "0083", "GP03D", 2, 150);
        PurchaseRequest request = new PurchaseRequest(
            sup2.getId(), LocalDate.of(2026, 9, 12), "URAGI", List.of(item));
        Purchase result = purSer.createFullPurchase(request);
        Product newPro = proRep.findById(3).get();

        Assertions.assertEquals("0083", newPro.getBarcode());
        Assertions.assertEquals("GP03D", newPro.getName());

        Optional<PurchaseItem> savedItem = pItemRep.findSameProduct(
            result.getId(), newPro.getId(), 150
        );
        Assertions.assertTrue(savedItem.isPresent());
        Assertions.assertEquals(2, savedItem.get().getQuantity());

        // no id, but barcode exist
        PurchaseItemRequest nItem1 = new PurchaseItemRequest(
            null, "0083", "GP03", 2, 200);
        PurchaseRequest nRequest1 = new PurchaseRequest(
            sup3.getId(), LocalDate.of(2026, 9, 12), "AAAA", List.of(nItem1));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.createFullPurchase(nRequest1));
        // no product id
        PurchaseItemRequest nItem2 = new PurchaseItemRequest(
            999, "0093", "UNICORN", 9, 99);
        PurchaseRequest nRequest2 = new PurchaseRequest(
            sup3.getId(), LocalDate.of(2026, 9, 12), "UNICOOOO", List.of(nItem2));
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.createFullPurchase(nRequest2));
    }

    @Test
    void testTheSameProduct() {
        SupplierRepository supRep = new SupplierRepository();
        PurchaseRepository purRep = new PurchaseRepository();
        ProductRepository proRep = new ProductRepository();
        PurchaseItemRepository pItemRep = new PurchaseItemRepository();
        PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
        Supplier sup1 = supRep.save("NAMCO");
        Supplier sup2 = supRep.save("BANDAI");
        Supplier sup3 = supRep.save("BANDAI NAMCO");
        Product pro1 = proRep.save("0079", "Gundam");
        Product pro2 = proRep.save(null, "BattleSpirits");

        PurchaseItemRequest item1 = new PurchaseItemRequest(
            pro1.getId(), "0079", "Gundam", 2, 100);
        PurchaseItemRequest item2 = new PurchaseItemRequest(
            pro1.getId(), "0079", "Gundam", 3, 100);
        PurchaseItemRequest item3 = new PurchaseItemRequest(
            pro1.getId(), "0079", "Gundam", 10, 150);
        PurchaseItemRequest item4 = new PurchaseItemRequest(
            pro2.getId(), "0079", "Gundam", 10, 150);
        // no id 合併, pro ID=3
        PurchaseItemRequest item5 = new PurchaseItemRequest(
            null, "0080", "Gundam", 12, 200);
        PurchaseItemRequest item6 = new PurchaseItemRequest(
            null, "0080", "Gundam", 12, 200);
        // no id, barcode 合併26, 有 barcode 不合併, proID=4 and 5
        PurchaseItemRequest item7 = new PurchaseItemRequest(
            null, null, "Gundam", 13, 250);
        PurchaseItemRequest item8 = new PurchaseItemRequest(
            null, null, "Gundam", 13, 250);
            PurchaseItemRequest item9 = new PurchaseItemRequest(
            null, "0083", "Gundam", 13, 250);

        PurchaseRequest request = new PurchaseRequest(
            sup2.getId(), LocalDate.of(2026, 9, 12), "URAGI"
            , List.of(item1, item2, item3, item4, item5, item6, item7, item8, item9));
    
        // 執行後應該只有一筆而且 qty=5
        Purchase result = purSer.createFullPurchase(request);
        Optional<PurchaseItem> savedItem100 = pItemRep.findSameProduct(
            result.getId(), pro1.getId(), 100);
        Assertions.assertTrue(savedItem100.isPresent());
        Assertions.assertEquals(5, savedItem100.get().getQuantity());
        // 執行後應該只有一筆而且 qty=10
        Optional<PurchaseItem> savedItem150 = pItemRep.findSameProduct(
            result.getId(), pro1.getId(), 150);
        Assertions.assertTrue(savedItem150.isPresent());
        Assertions.assertEquals(10, savedItem150.get().getQuantity());
        // item 3 and 4 不能合併
        Optional<PurchaseItem> savedItem1502 = pItemRep.findSameProduct(
            result.getId(), pro2.getId(), 150);
        Assertions.assertTrue(savedItem1502.isPresent());
        Assertions.assertEquals(10, savedItem1502.get().getQuantity());
        // 5 and 6
        Optional<PurchaseItem> savedItem200 = pItemRep.findSameProduct(
            result.getId(), 3, 200);
        Assertions.assertTrue(savedItem200.isPresent());
        Assertions.assertEquals(24, savedItem200.get().getQuantity());
        //7, 8, 9
        Optional<PurchaseItem> savedItem250 = pItemRep.findSameProduct(
            result.getId(), 4, 250);
        Optional<PurchaseItem> savedItem2502 = pItemRep.findSameProduct(
            result.getId(), 5, 250);
        Assertions.assertTrue(savedItem250.isPresent());
        Assertions.assertTrue(savedItem2502.isPresent());
        Assertions.assertEquals(26, savedItem250.get().getQuantity());
        Assertions.assertEquals(13, savedItem2502.get().getQuantity());   
    }
}
