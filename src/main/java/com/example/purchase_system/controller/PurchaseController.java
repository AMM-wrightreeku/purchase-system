package com.example.purchase_system.controller;

import com.example.purchase_system.service.PurchaseService;
import com.example.purchase_system.dto.PurchaseDetailResponse;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.dto.PurchaseSearchRequest;
import com.example.purchase_system.entity.Purchase;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {
    private PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public Purchase createPurchase(@RequestBody PurchaseRequest request) {
        return purchaseService.createFullPurchase(request);
    }

    @GetMapping
    public List<Purchase> search(PurchaseSearchRequest request) {
        return purchaseService.search(request);
    }
 
    @GetMapping("/{purchaseId}")
    public PurchaseDetailResponse getPurchaseDetail(@PathVariable int purchaseId) {
        return purchaseService.getPurchaseDetail(purchaseId);
    }
}
