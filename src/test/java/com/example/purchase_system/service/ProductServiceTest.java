package com.example.purchase_system.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.purchase_system.entity.Product;
import com.example.purchase_system.repository.ProductRepository;

public class ProductServiceTest {

    // 測試建立商品
    @Test
    void testCreateProduct() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        Product pro = proSer.createProduct(
            "65536",
            "NeoGranzon"
        );

        Assertions.assertEquals(1, pro.getId());
        Assertions.assertEquals("65536", pro.getBarcode());
        Assertions.assertEquals("NeoGranzon", pro.getName());
    }

    // 測試重複 barcode
    @Test
    void testDuplicateBarcode() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        proSer.createProduct(
            "65536",
            "NeoGranzon"
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proSer.createProduct(
                "65536",
                "Granzon"
            )
        );
    }

    // 測試沒有 barcode 的商品可以建立
    @Test
    void testCreateProductWithoutBarcode() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        Product pro1 = proSer.createProduct(
            null,
            "紅茶"
        );

        Product pro2 = proSer.createProduct(
            null,
            "綠茶"
        );

        Assertions.assertNull(pro1.getBarcode());
        Assertions.assertNull(pro2.getBarcode());

        Assertions.assertEquals("紅茶", pro1.getName());
        Assertions.assertEquals("綠茶", pro2.getName());
    }

    // 測試不合法名稱
    @Test
    void testInvalidName() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proSer.createProduct(
                "65536",
                null
            )
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proSer.createProduct(
                "65537",
                ""
            )
        );

        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> proSer.createProduct(
                "65538",
                "   "
            )
        );
    }

    // 測試 barcode 搜尋
    @Test
    void testFindByBarcode() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        proSer.createProduct(
            "65536",
            "NeoGranzon"
        );

        Assertions.assertTrue(
            proSer.findByBarcode("65536").isPresent()
        );

        Assertions.assertEquals(
            "NeoGranzon",
            proSer.findByBarcode("65536")
                  .get()
                  .getName()
        );

        Assertions.assertTrue(
            proSer.findByBarcode("65535").isEmpty()
        );

        Assertions.assertTrue(
            proSer.findByBarcode("").isEmpty()
        );

        Assertions.assertTrue(
            proSer.findByBarcode(null).isEmpty()
        );
    }

    // 測試模糊名稱搜尋
    @Test
    void testFindByName() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        proSer.createProduct(null, "紅茶");
        proSer.createProduct(null, "無糖紅茶");
        proSer.createProduct(null, "綠茶");
        proSer.createProduct(null, "咖啡");

        List<Product> result =
            proSer.findByName("紅茶");

        Assertions.assertEquals(2, result.size());

        for (Product pro : result) {
            Assertions.assertTrue(
                pro.getName().contains("紅茶")
            );
        }

        Assertions.assertTrue(
            proSer.findByName("Granzon").isEmpty()
        );

        Assertions.assertTrue(
            proSer.findByName(null).isEmpty()
        );

        Assertions.assertTrue(
            proSer.findByName("").isEmpty()
        );

        Assertions.assertTrue(
            proSer.findByName("   ").isEmpty()
        );
    }

    // 測試 ID 搜尋
    @Test
    void testFindById() {
        ProductRepository proRep = new ProductRepository();
        ProductService proSer = new ProductService(proRep);

        proSer.createProduct(
            "65536",
            "NeoGranzon"
        );

        Optional<Product> result =
            proSer.findById(1);

        Assertions.assertTrue(result.isPresent());

        Assertions.assertEquals(
            "NeoGranzon",
            result.get().getName()
        );

        Assertions.assertTrue(
            proSer.findById(65535).isEmpty()
        );
    }
}