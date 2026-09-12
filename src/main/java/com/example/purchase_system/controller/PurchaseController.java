package com.example.purchase_system.controller;

import com.example.purchase_system.service.PurchaseService;
import com.example.purchase_system.dto.PurchaseRequest;
import com.example.purchase_system.entity.Purchase;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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

    
    
}
