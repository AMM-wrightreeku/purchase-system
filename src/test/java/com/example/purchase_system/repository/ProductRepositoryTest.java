package com.example.purchase_system.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Product;

public class ProductRepositoryTest {

    // 測試 save：ID 應依序產生
    @Test
    void testSave() {
        ProductRepository proRep = new ProductRepository();

        Product pro1 = proRep.save("65536", "NeoGranzon");
        Product pro2 = proRep.save("65537", "Granzon");

        Assertions.assertEquals(1, pro1.getId());
        Assertions.assertEquals(2, pro2.getId());

        Assertions.assertEquals("NeoGranzon", pro1.getName());
        Assertions.assertEquals("Granzon", pro2.getName());
    }

    // 測試重複 barcode 不可新增
    @Test
    void testDuplicateBarcode() {
        ProductRepository proRep = new ProductRepository();

        proRep.save("65536", "NeoGranzon");

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proRep.save("65536", "Granzon")
        );
    }

    // 測試名稱不可為 null / blank
    @Test
    void testInvalidName() {
        ProductRepository proRep = new ProductRepository();

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proRep.save("65536", null)
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proRep.save("65537", "")
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proRep.save("65538", "   ")
        );
    }

    // 測試沒有 barcode 的商品可以建立
    @Test
    void testNullBarcode() {
        ProductRepository proRep = new ProductRepository();

        Product pro1 = proRep.save(null, "紅茶");
        Product pro2 = proRep.save(null, "綠茶");

        Assertions.assertNull(pro1.getBarcode());
        Assertions.assertNull(pro2.getBarcode());

        Assertions.assertEquals(1, pro1.getId());
        Assertions.assertEquals(2, pro2.getId());
    }

    // 測試用 ID 搜尋
    @Test
    void testFindById() {
        ProductRepository proRep = new ProductRepository();

        proRep.save("65536", "NeoGranzon");

        Optional<Product> result1 = proRep.findById(1);
        Optional<Product> result2 = proRep.findById(65535);

        Assertions.assertTrue(result1.isPresent());
        Assertions.assertEquals("NeoGranzon", result1.get().getName());

        Assertions.assertTrue(result2.isEmpty());
    }

    // 測試 barcode 搜尋
    @Test
    void testFindByBarcode() {
        ProductRepository proRep = new ProductRepository();

        proRep.save("65536", "NeoGranzon");
        proRep.save(null, "Granzon");

        Assertions.assertTrue(
            proRep.findByBarcode("65536").isPresent()
        );

        Assertions.assertEquals(
            "NeoGranzon",
            proRep.findByBarcode("65536").get().getName()
        );

        Assertions.assertTrue(
            proRep.findByBarcode("65535").isEmpty()
        );

        Assertions.assertTrue(
            proRep.findByBarcode("").isEmpty()
        );

        Assertions.assertTrue(
            proRep.findByBarcode(null).isEmpty()
        );
    }

    // 測試模糊名稱搜尋
    @Test
    void testFindByName() {
        ProductRepository proRep = new ProductRepository();

        proRep.save(null, "紅茶");
        proRep.save(null, "無糖紅茶");
        proRep.save(null, "綠茶");
        proRep.save(null, "咖啡");

        List<Product> result1 = proRep.findByName("紅茶");

        Assertions.assertEquals(2, result1.size());

        for (Product pro : result1) {
            Assertions.assertTrue(
                pro.getName().contains("紅茶")
            );
        }

        Assertions.assertTrue(
            proRep.findByName("Granzon").isEmpty()
        );

        Assertions.assertTrue(
            proRep.findByName(null).isEmpty()
        );

        Assertions.assertTrue(
            proRep.findByName("").isEmpty()
        );

        Assertions.assertTrue(
            proRep.findByName("   ").isEmpty()
        );
    }
}