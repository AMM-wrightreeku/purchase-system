package com.example.purchase_system.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.purchase_system.dto.PurchaseItemRequest;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;

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
    public PurchaseItem addPurchaseItem(int purchaseId, int productId, int quantity, int totalPrice) {
        if(purchaseRepository.findById(purchaseId).isEmpty()) throw new IllegalArgumentException("Purchase does not exist.");
        if(productRepository.findById(productId).isEmpty()) throw new IllegalArgumentException("Product does not exist.");
        return pItemRepository.save(purchaseId, productId, quantity, totalPrice);
    }
    // Request Check
    private void checkRequest(PurchaseRequest request) {
        if(request == null) {throw new IllegalArgumentException("Request is null.");}
        if(request.getSupplierId() == null) {throw new IllegalArgumentException("Supplier is null.");}
        if(request.getPurchaseDate() == null) {throw new IllegalArgumentException("Date is null.");}
        if(request.getItems() == null || request.getItems().isEmpty()) {throw new IllegalArgumentException("No Purchase Item.");}
    }
    // Request Data Check
    private void checkRequestData(PurchaseRequest request) {
        // Supplier
        if(supplierRepository.findById(request.getSupplierId()).isEmpty()) {
            throw new IllegalArgumentException("Supplier does not exist.");
        }
        // PurchaseItem
        for(PurchaseItemRequest item : request.getItems()) {
            if(item == null ) {throw new IllegalArgumentException("Purchase Item is null.");}
            if(item.getQuantity() < 1) {throw new IllegalArgumentException(item.getProductName() + " quantity invalid.");}
            if(item.getTotalPrice() < 0) {throw new IllegalArgumentException(item.getProductName() + " price invalid.");}
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
    }
    // 補完 purchaseItem 的所有 productId
    private List<Integer> getOrCreateProductIds(PurchaseRequest request) {
        List<Integer> productIds = new ArrayList<>();
        for(int i = 0; i < request.getItems().size(); i++) {
            PurchaseItemRequest currentItem = request.getItems().get(i);
            Integer productId = currentItem.getProductId();

            if(productId == null) {
                for(int j = 0; j < i; j++) {
                    if(isSameProduct(currentItem, request.getItems().get(j))) {
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
        return productIds;
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
    // 新增完整訂單
    public Purchase createFullPurchase(PurchaseRequest request) {
        // check
        checkRequest(request);
        checkRequestData(request);
        // fill up data
        List<Integer> productIds = getOrCreateProductIds(request);
        // 開始建構進貨訂單
        Purchase purchase = purchaseRepository.save(request.getSupplierId(), request.getPurchaseDate(), request.getNote());
        for(int i = 0; i < request.getItems().size(); i++) {
            pItemRepository.save(purchase.getId()
                                , productIds.get(i)
                                , request.getItems().get(i).getQuantity()
                                , request.getItems().get(i).getTotalPrice());
        }
        return purchase;
    }
}
