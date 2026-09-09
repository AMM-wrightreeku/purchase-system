package com.example.purchase_system.entity;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int productId;
    private int qty;
    private int purchasePrice;

    public PurchaseItem(int id, int purchaseId, int productId, int qty, int purchasePrice) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.qty = qty;
        this.purchasePrice = purchasePrice;
    }

    public int getId() {
        return id;
    }
    public int getPurchaseId() {
        return purchaseId;
    }
    public int getProductId() {
        return productId;
    }
    public int getQuantity() {
        return qty;
    }
    public int getPurchasePrice() {
        return purchasePrice;
    }
}
