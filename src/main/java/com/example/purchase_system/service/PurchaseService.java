package com.example.purchase_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.purchase_system.dto.PurchaseItemRequest;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;

import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    private SupplierRepository supplierRepository;
    private PurchaseRepository purchaseRepository;
    private ProductRepository productRepository;
    private PurchaseItemRepository pItemRepository;

    public PurchaseService(SupplierRepository supplierRepository,
                           PurchaseRepository purchaseRepository,
                           ProductRepository productRepository,
                           PurchaseItemRepository pItemRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.pItemRepository = pItemRepository;
    }
    // 新增進貨訂單資訊
    public Purchase createPurchase(int supplierId, LocalDate date, String note) {
        // 1. 先搜尋是否為既有廠商，如無，則拋出例外給 user 決定處理
        if(supplierRepository.findById(supplierId).isEmpty()) throw new IllegalArgumentException("Supplier doesn't exist.");
        // 3. 存在，建立進貨資料
        return purchaseRepository.save(supplierId, date, note);   
    }
    // 新增進貨商品資訊
    public PurchaseItem addPurchaseItem(int purchaseId, int productId, int quantity, int purchasePrice) {
        if(purchaseRepository.findById(purchaseId).isEmpty()) throw new IllegalArgumentException("Purchase does not exist.");
        if(productRepository.findById(productId).isEmpty()) throw new IllegalArgumentException("Product does not exist.");
        return pItemRepository.save(purchaseId, productId, quantity, purchasePrice);
    }
    // 新增完整訂單
    public Purchase createFullPurchase(PurchaseRequest request) {
        // Request Check
        if(request == null) {throw new IllegalArgumentException("Request is null.");}
        if(request.getSupplierId() == null) {throw new IllegalArgumentException("Supplier is null.");}
        if(request.getPurchaseDate() == null) {throw new IllegalArgumentException("Date is null.");}
        if(request.getItems() == null || request.getItems().isEmpty()) {throw new IllegalArgumentException("No Purchase Item.");}
        // Request data Check
        // Supplier
        if(supplierRepository.findById(request.getSupplierId()).isEmpty()) {
            throw new IllegalArgumentException("Supplier does not exist.");
        }
        // PurchaseItem
        for(PurchaseItemRequest item : request.getItems()) {
            if(item == null ) {throw new IllegalArgumentException("Purchase Item is null.");}
            if(item.getQuantity() < 1) {throw new IllegalArgumentException(item.getProductName() + " quantity invalid.");}
            if(item.getPurchasePrice() < 0) {throw new IllegalArgumentException(item.getProductName() + " price invalid.");}
            // Product ID Check            
            if(item.getProductId() != null) {
                if(productRepository.findById(item.getProductId()).isEmpty()) {
                    throw new IllegalArgumentException("Product does not exist.");
                }
            } else {
                if(item.getProductName() == null || item.getProductName().isBlank()) {throw new IllegalArgumentException("Product Name cannot be blank.");}
                if(item.getBarcode() != null && !item.getBarcode().isBlank()
                    && productRepository.findByBarcode(item.getBarcode()).isPresent()) {
                    throw new IllegalArgumentException("Product Barcode already exists.");
                }
            }
        }
        
        // 前方 check 結束，開始進行資料處理
        List<PurchaseItemRequest> mergedItems = request.getItems();
        if(request.getItems().size() >= 2) mergedItems = mergeSameItems(request.getItems());
        // product ID 補完程序
        // List<Integer> productIds = new ArrayList<>();
        // for(PurchaseItemRequest purItemRep : mergedItems) {
        //     boolean proIdExist = purItemRep.getProductId() != null;
        //     Integer newId;
        //     if(proIdExist) newId = purItemRep.getProductId();
        //     else {
        //         newId = productRepository.save(purItemRep.getBarcode(), purItemRep.getProductName()).getId();
        //     }
        //     productIds.add(newId);
        // }
        List<Integer> productIds = new ArrayList<>();

        for(int i = 0; i < mergedItems.size(); i++) {

            PurchaseItemRequest currentItem = mergedItems.get(i);
            Integer productId = currentItem.getProductId();

            if(productId == null) {

                for(int j = 0; j < i; j++) {

                    if(isSameProduct(currentItem, mergedItems.get(j))) {
                        productId = productIds.get(j);
                        break;
                    }
                }

                if(productId == null) {
                    productId = productRepository.save(
                        currentItem.getBarcode(),
                        currentItem.getProductName()
                    ).getId();
                }
            }

            productIds.add(productId);
        }
        // 至此，productIds[i] = mergedItems[i] 的 id（空缺補完）
        // 開始建構進貨訂單
        Purchase purchase = purchaseRepository.save(request.getSupplierId(), request.getPurchaseDate(), request.getNote());
        for(int i = 0; i < mergedItems.size(); i++) {
            pItemRepository.save(purchase.getId()
                                , productIds.get(i)
                                , mergedItems.get(i).getQuantity()
                                , mergedItems.get(i).getPurchasePrice());
        }
        return purchase;
    }

    // 這個方法只使用在 傳進來的 items.size() >=2 的狀況，<=1的狀況不需要重新整理沒有意義
    // 所以方法只會建立在 成員 2 個以上來使用
    private List<PurchaseItemRequest> mergeSameItems(List<PurchaseItemRequest> items) {
        List<PurchaseItemRequest> mergedItems = new ArrayList<>();
        mergedItems.add(items.get(0));  // 第一個不用比直接進去

        for(int i = 1 ; i < items.size() ; i++) {
            // 相同則加入一個，以及合計
            boolean found = false;
            for(int j = 0; j < mergedItems.size(); j++) {
                if(isSameItem(mergedItems.get(j), items.get(i))) {
                    PurchaseItemRequest newer = new PurchaseItemRequest(
                        mergedItems.get(j).getProductId(), mergedItems.get(j).getBarcode()
                        , mergedItems.get(j).getProductName()
                        , mergedItems.get(j).getQuantity()+items.get(i).getQuantity() // 數量相加
                        , mergedItems.get(j).getPurchasePrice());
                    mergedItems.set(j, newer);
                    found = true;
                    break;
                }
            }
            if(!found) {
                mergedItems.add(items.get(i));
            }
        }
        return mergedItems;
    }

    // same product
    private boolean isSameProduct(PurchaseItemRequest itemA, PurchaseItemRequest itemB) {
        boolean aWithId = itemA.getProductId() != null;
        boolean bWithId = itemB.getProductId() != null;

        if(aWithId || bWithId) {
            if(!aWithId || !bWithId) {
                return false;
            }

            return itemA.getProductId().equals(itemB.getProductId());
        }

        boolean aWithCode =
            itemA.getBarcode() != null &&
            !itemA.getBarcode().isBlank();

        boolean bWithCode =
            itemB.getBarcode() != null &&
            !itemB.getBarcode().isBlank();

        if(aWithCode || bWithCode) {
            if(!aWithCode || !bWithCode) {
                return false;
            }

            return itemA.getBarcode().equals(itemB.getBarcode());
        }

        return itemA.getProductName().equals(itemB.getProductName());
    }



    // request item 是否相同？
    private boolean isSameItem(PurchaseItemRequest itemA, PurchaseItemRequest itemB) {
        // 最先比較 價格不同, false
        if(itemA.getPurchasePrice() != itemB.getPurchasePrice()) return false;
        // 既有商品, 兩者任一有id
        // id 相同則相同，不同則不同
        // id 一方有一方無，視作不同商品, 因為無法推測 user 是故意還是不小心造成類似資訊發生，最後丟回給 user 檢查
        boolean aWithId = itemA.getProductId() != null;
        boolean bWithId = itemB.getProductId() != null;
        if(aWithId || bWithId) {
            if(!aWithId || !bWithId) return false; // 一方是null
            return itemA.getProductId().equals(itemB.getProductId()); // ID 比較結果
        }
        // 基於上述 ID 判斷，以下皆為判斷皆無 ID 的判別
        // barcode 為第二判定基準，因為命名可能因 user 而定，但條碼不一定
        // 先判定是否最少有一條碼
        boolean aWithCode = itemA.getBarcode() != null && !itemA.getBarcode().isBlank();
        boolean bWithCode = itemB.getBarcode() != null && !itemB.getBarcode().isBlank();
        if(aWithCode || bWithCode) {
                if(!aWithCode || !bWithCode) return false; // 跟 id 一樣，一方有一方無當作不同送回
                return itemA.getBarcode().equals(itemB.getBarcode()); // A, B 比較結果
        }
        // 第三判定標準, name = 兩者的 id, barcode 皆無或皆為空白才到這
        // name 不能為 null 是基本規則，但為求確保還是在這做確認擋住
        boolean aWithName = itemA.getProductName() != null && !itemA.getProductName().isBlank();
        boolean bWithName = itemB.getProductName() != null && !itemB.getProductName().isBlank();
        if(!aWithName || !bWithName) return false; // 有一邊是不合法名字
        return itemA.getProductName().equals(itemB.getProductName());
    }

}
