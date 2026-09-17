package com.example.purchase_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private SupplierRepository supRep;
    private PurchaseRepository purRep;
    private ProductRepository proRep;
    private PurchaseItemRepository pItemRep;
    private PurchaseService purSer;
    private List<PurchaseItemRequest> items;
    @BeforeEach 
    void setUp() {
        supRep = new SupplierRepository(false);
        purRep = new PurchaseRepository();
        proRep = new ProductRepository();
        pItemRep = new PurchaseItemRepository();
        purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
        items = List.of(new PurchaseItemRequest(9, "65536", "NeoGranzon", 3, 90000));

    }
    @Test
    void createRequestCheckNullRequest() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.createFullPurchase(null));
    }
    @Test
    void createRequestCheckNullSupplierId() {
        PurchaseRequest requestNullSupplierId = new PurchaseRequest(null, LocalDate.of(2026, 9, 17), "TEST1", items);
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> purSer.createFullPurchase(requestNullSupplierId));
    }
    @Test
    void createRequestCheckNullDate() {
    PurchaseRequest requestNullDate = new PurchaseRequest(3, null, "TEST2", items);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> purSer.createFullPurchase(requestNullDate));
  
    }
    @Test
    void createRequestCheckNullItems() {
    PurchaseRequest requestNullItems = new PurchaseRequest(3, LocalDate.of(2026, 9, 17), "TEST3", null);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> purSer.createFullPurchase(requestNullItems));
    }
    @Test
    void createRequestCheckEmptyItems() {
        PurchaseRequest requestEmptyItems = new PurchaseRequest(3, LocalDate.of(2026, 9, 17), "TEST4", List.of());
        Assertions.assertThrows(IllegalArgumentException.class,
        () -> purSer.createFullPurchase(requestEmptyItems));
    }


    // @Test
    // void testSupplierExistOrNot() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     supRep.save("BANDAI");
    //     supRep.save("BANDAI NAMCO");
    //     Purchase pur = purSer.createPurchase(2, LocalDate.of(2026, 9, 11), "TEST");
        
    //     // check
    //     Assertions.assertEquals(2, pur.getSupplierId());
    //     Assertions.assertEquals(LocalDate.of(2026, 9, 11), pur.getDate());
    //     Assertions.assertEquals("TEST", pur.getNote());

    //     // supplierId doesn't exist
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //     () -> purSer.createPurchase(3, LocalDate.of(2026, 9, 11), null));
    // }

    // @Test
    // void testDate() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();

    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     supRep.save("BANDAI");
        
    //     // daet null
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //     () -> purSer.createPurchase(1, null, "TEST"));
    // }

    // @Test
    // void testPurchaseAndProductExistOrNot() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     supRep.save("NAMCO");
    //     supRep.save("BANDAI");
    //     supRep.save("BANDAI NAMCO");
    //     proRep.save("0079", "Gundam");
    //     proRep.save(null, "BattleSpirits");
    //     purSer.createPurchase(3, LocalDate.of(2026, 9, 11), "New Brand");

    //     // Purchase & Product exist
    //     PurchaseItem pItem = purSer.addPurchaseItem(1, 2, 6, 1500);
    //     Assertions.assertEquals(1, pItem.getPurchaseId());  // purchaseId
    //     Assertions.assertEquals(2, pItem.getProductId());  // productId
    //     Assertions.assertEquals(6, pItem.getQuantity());  // quantity
    //     Assertions.assertEquals(1500, pItem.getTotalPrice());  // price

    //     // Purchase doesn't exist
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.addPurchaseItem(2, 2, 6, 1500));

    //     // Product doesn't exist
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.addPurchaseItem(1, 3, 6, 1500));
        
    //     // quantity & price invaiid
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.addPurchaseItem(1, 2, 0, 1500));
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.addPurchaseItem(1, 2, 1, -20));
    // }

    // @Test 
    // void testCreateFullPurchase() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     supRep.save("NAMCO");
    //     supRep.save("BANDAI");
    //     supRep.save("BANDAI NAMCO");
    //     proRep.save("0079", "Gundam");
    //     proRep.save(null, "BattleSpirits");

    //     PurchaseItemRequest item = new PurchaseItemRequest(
    //         1, "0079", "Gundam", 3, 100);
    //     PurchaseRequest request = new PurchaseRequest(
    //         2, LocalDate.of(2026, 9, 12), "TEST", List.of(item));
    //         Purchase result = purSer.createFullPurchase(request);

    //         // Correct product and supplier
    //         Product product = proRep.findById(1).get();
    //         Supplier supplier = supRep.findById(2).get();

    //         Assertions.assertEquals(supplier.getId(), result.getSupplierId());
    //         Assertions.assertEquals(LocalDate.of(2026, 9, 12),  result.getDate());
    //         Assertions.assertEquals("TEST", result.getNote());

    //         // TEST purchase build
    //         Optional<PurchaseItem> savedItem = pItemRep.findSameProduct(
    //             result.getId(), product.getId(), 100
    //         );
    //         Assertions.assertTrue(savedItem.isPresent());
    //         Assertions.assertEquals(3, savedItem.get().getQuantity());
    // }

    // @Test
    // void testCreateFullPurchaseWithNewProduct() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     Supplier sup1 = supRep.save("NAMCO");
    //     Supplier sup2 = supRep.save("BANDAI");
    //     Supplier sup3 = supRep.save("BANDAI NAMCO");
    //     Product pro1 = proRep.save("0079", "Gundam");
    //     Product pro2 = proRep.save(null, "BattleSpirits");
    //     // 再來測試的應該是從 id = 3 的 product 開始
    //     PurchaseItemRequest item = new PurchaseItemRequest(
    //         null, "0083", "GP03D", 2, 150);
    //     PurchaseRequest request = new PurchaseRequest(
    //         sup2.getId(), LocalDate.of(2026, 9, 12), "URAGI", List.of(item));
    //     Purchase result = purSer.createFullPurchase(request);
    //     Product newPro = proRep.findById(3).get();

    //     Assertions.assertEquals("0083", newPro.getBarcode());
    //     Assertions.assertEquals("GP03D", newPro.getName());

    //     Optional<PurchaseItem> savedItem = pItemRep.findSameProduct(
    //         result.getId(), newPro.getId(), 150
    //     );
    //     Assertions.assertTrue(savedItem.isPresent());
    //     Assertions.assertEquals(2, savedItem.get().getQuantity());

    //     // no id, but barcode exist
    //     PurchaseItemRequest nItem1 = new PurchaseItemRequest(
    //         null, "0083", "GP03", 2, 200);
    //     PurchaseRequest nRequest1 = new PurchaseRequest(
    //         sup3.getId(), LocalDate.of(2026, 9, 12), "AAAA", List.of(nItem1));
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.createFullPurchase(nRequest1));
    //     // no product id
    //     PurchaseItemRequest nItem2 = new PurchaseItemRequest(
    //         999, "0093", "UNICORN", 9, 99);
    //     PurchaseRequest nRequest2 = new PurchaseRequest(
    //         sup3.getId(), LocalDate.of(2026, 9, 12), "UNICOOOO", List.of(nItem2));
    //     Assertions.assertThrows(IllegalArgumentException.class,
    //         () -> purSer.createFullPurchase(nRequest2));
    // }

    // @Test
    // void testTheSameProduct() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     Supplier sup1 = supRep.save("NAMCO");
    //     Supplier sup2 = supRep.save("BANDAI");
    //     Supplier sup3 = supRep.save("BANDAI NAMCO");
    //     Product pro1 = proRep.save("0079", "Gundam");
    //     Product pro2 = proRep.save(null, "BattleSpirits");

    //     PurchaseItemRequest item1 = new PurchaseItemRequest(
    //         pro1.getId(), "0079", "Gundam", 2, 100);
    //     PurchaseItemRequest item2 = new PurchaseItemRequest(
    //         pro1.getId(), "0079", "Gundam", 3, 100);
    //     PurchaseItemRequest item3 = new PurchaseItemRequest(
    //         pro1.getId(), "0079", "Gundam", 10, 150);
    //     PurchaseItemRequest item4 = new PurchaseItemRequest(
    //         pro2.getId(), "0079", "Gundam", 10, 150);
    //     // no id 合併, pro ID=3
    //     PurchaseItemRequest item5 = new PurchaseItemRequest(
    //         null, "0080", "Gundam", 12, 200);
    //     PurchaseItemRequest item6 = new PurchaseItemRequest(
    //         null, "0080", "Gundam", 12, 200);
    //     // no id, barcode 合併26, 有 barcode 不合併, proID=4 and 5
    //     PurchaseItemRequest item7 = new PurchaseItemRequest(
    //         null, null, "Gundam", 13, 250);
    //     PurchaseItemRequest item8 = new PurchaseItemRequest(
    //         null, null, "Gundam", 13, 250);
    //     PurchaseItemRequest item9 = new PurchaseItemRequest(
    //         null, "0083", "Gundam", 13, 250);

    //     PurchaseRequest request = new PurchaseRequest(
    //         sup2.getId(), LocalDate.of(2026, 9, 12), "URAGI"
    //         , List.of(item1, item2, item3, item4, item5, item6, item7, item8, item9));
    //     Purchase result = purSer.createFullPurchase(request);

    //     List<PurchaseItem> savedItems =
    //         pItemRep.findByPurchaseId(result.getId());

    //     Assertions.assertEquals(9, savedItems.size());

    //     Assertions.assertEquals(pro1.getId(), savedItems.get(0).getProductId());
    //     Assertions.assertEquals(2, savedItems.get(0).getQuantity());

    //     Assertions.assertEquals(pro1.getId(), savedItems.get(1).getProductId());
    //     Assertions.assertEquals(3, savedItems.get(1).getQuantity());  
    // }
    
    // @Test
    // void testSameProduct() {
    //     SupplierRepository supRep = new SupplierRepository(false);
    //     PurchaseRepository purRep = new PurchaseRepository();
    //     ProductRepository proRep = new ProductRepository();
    //     PurchaseItemRepository pItemRep = new PurchaseItemRepository();
    //     PurchaseService purSer = new PurchaseService(supRep, purRep, proRep, pItemRep);
    //     // Supplier sup1 = supRep.save("NAMCO");
    //     Supplier sup2 = supRep.save("BANDAI");
    //     // Supplier sup3 = supRep.save("BANDAI NAMCO");
    //     // Product pro1 = proRep.save("0079", "Gundam");
    //     // Product pro2 = proRep.save(null, "BattleSpirits");
    //     PurchaseItemRequest item1 = new PurchaseItemRequest(
    //         null, "888","Gundam" , 1, 100);
    //     PurchaseItemRequest item2 = new PurchaseItemRequest(
    //         null, "888","Gundam" , 1, 200);
    //     PurchaseRequest request = new PurchaseRequest(
    //         sup2.getId(), LocalDate.of(2026, 9, 12), "AAAA", List.of(item1, item2));
    //     Purchase result = purSer.createFullPurchase(request);

    //     assertNotNull(result);

    //     Product savedProduct = proRep.findByBarcode("888").orElseThrow();
    //     assertEquals("Gundam", savedProduct.getName());

    //     PurchaseItem savedItem1 = pItemRep.findSameProduct(
    //         result.getId(),
    //         savedProduct.getId(),
    //         100
    //     ).orElseThrow();

    //     PurchaseItem savedItem2 = pItemRep.findSameProduct(
    //         result.getId(),
    //         savedProduct.getId(),
    //         200
    //     ).orElseThrow();

    //     assertEquals(savedProduct.getId(), savedItem1.getProductId());
    //     assertEquals(savedProduct.getId(), savedItem2.getProductId());

    //     assertEquals(1, savedItem1.getQuantity());
    //     assertEquals(1, savedItem2.getQuantity());

    //     assertEquals(100, savedItem1.getTotalPrice());
    //     assertEquals(200, savedItem2.getTotalPrice());
    // }
}
