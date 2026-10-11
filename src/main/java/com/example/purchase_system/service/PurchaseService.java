package com.example.purchase_system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.purchase_system.dto.PurchaseItemRequest;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.dto.PurchaseSearchRequest;
import com.example.purchase_system.dto.PurchaseDetailResponse;
import com.example.purchase_system.dto.PurchaseItemDetailResponse;
import com.example.purchase_system.entity.Product;
import com.example.purchase_system.entity.Purchase;
import com.example.purchase_system.entity.PurchaseItem;
import com.example.purchase_system.entity.Supplier;
import com.example.purchase_system.repository.ProductRepository;
import com.example.purchase_system.repository.PurchaseItemRepository;
import com.example.purchase_system.repository.PurchaseRepository;
import com.example.purchase_system.repository.SupplierRepository;

@Service
public class PurchaseService {
    private final SupplierRepository supplierRepository;
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final PurchaseItemRepository pItemRepository;

    public PurchaseService(SupplierRepository supplierRepository,
                           PurchaseRepository purchaseRepository,
                           ProductRepository productRepository,
                           PurchaseItemRepository pItemRepository) {
        this.supplierRepository = supplierRepository;
        this.purchaseRepository = purchaseRepository;
        this.productRepository = productRepository;
        this.pItemRepository = pItemRepository;
    }

    // 即使已經靠好幾個方法簡化這個方法了，但還是有點長？
    public Purchase createPurchase(PurchaseRequest request) {
        checkRequest(request);
        checkRequestData(request);
        List<Integer> productIds = getOrCreateProductIds(request);

        Purchase purchase = purchaseRepository.save(request.getSupplierId(), request.getPurchaseDate(), request.getNote());
        for(int i = 0; i < request.getItems().size(); i++) {
            pItemRepository.save(purchase.getId()
                                , productIds.get(i)
                                , request.getItems().get(i).getQuantity()
                                , request.getItems().get(i).getTotalPrice());
        }
        return purchase;
    }
    
    // 名稱重新命名?
    // 現在這個 java 有 200 行 CODE，這個方法有必要為了閱讀性分出去嗎？
    private void checkRequest(PurchaseRequest request) {
        if(request == null) {
            throw new IllegalArgumentException("Request is null.");}
        if(request.getSupplierId() == null) {
            throw new IllegalArgumentException("Supplier is null.");}
        if(request.getPurchaseDate() == null) {
            throw new IllegalArgumentException("Date is null.");}
        if(request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("No Purchase Item.");}
    }
    // 同上
    // 名稱重新命名?
    // 現在這個 java 有 200 行 CODE，這個方法有必要為了閱讀性分出去嗎？
    private void checkRequestData(PurchaseRequest request) {
        // Supplier
        if(supplierRepository.findById(request.getSupplierId()).isEmpty()) {
            throw new IllegalArgumentException("Supplier does not exist.");
        }
        // PurchaseItem
        for(PurchaseItemRequest item : request.getItems()) {
            if(item == null ) {
                throw new IllegalArgumentException("Purchase Item is null.");}
            if(item.getQuantity() < 1) {
                throw new IllegalArgumentException(item.getProductName() + " quantity invalid.");}
            if(item.getTotalPrice() < 0) {
                throw new IllegalArgumentException(item.getProductName() + " price invalid.");}
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
    // 同前面　２　個，而且這幾個方法都只是設計來驗證 createFullPurchase 一個方法用而已
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
    // 這個方法有點長，而且內容不是很直觀，每一段都要想一下為什麼運作
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

    public List<PurchaseDetailResponse> searchPurchases(PurchaseSearchRequest request) {
        if(request == null) {
            throw new IllegalArgumentException("Request is null.");}
        if(request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("startDate cannot be later than endDate.");
        }

        List<Purchase> purchases = purchaseRepository.searchPurchases(
            request.getSupplierId()
          , request.getStartDate()
          , request.getEndDate()
        );

        List<PurchaseDetailResponse> responses = new ArrayList<>();

        for(Purchase purchase : purchases) {
            responses.add(getPurchaseDetail(purchase.getId()));
        }

        return responses;
    }
   
    public PurchaseDetailResponse getPurchaseDetail(int purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase does not exist."));
        
        int supplierId = purchase.getSupplierId();
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier does not exist."));
        
        List<PurchaseItem> purchaseItems = pItemRepository.findByPurchaseId(purchaseId);
        
        return new PurchaseDetailResponse(
            purchaseId
          , supplierId
          , supplier.getName()
          , purchase.getDate()
          , purchase.getNote()
          , toItemDetailResponses(purchaseItems)
        );
    }

    private List<PurchaseItemDetailResponse> toItemDetailResponses(List<PurchaseItem> pItems) {
        List<PurchaseItemDetailResponse> items = new ArrayList<>();

        for(PurchaseItem pItem : pItems) {
            int productId = pItem.getProductId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product does not exist."));
            PurchaseItemDetailResponse item = new PurchaseItemDetailResponse(
                pItem.getId()
              , productId
              , product.getBarcode()
              , product.getName()
              , pItem.getQuantity()
              , pItem.getTotalPrice()
            );
            items.add(item);
        }

        return items;
    }
}
