package com.example.purchase_system.entity;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int productId;
    private int quantity;
    private int totalPrice;

    public PurchaseItem(int id, int purchaseId, int productId, int quantity, int totalPrice) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {return id;}
    public int getPurchaseId() {return purchaseId;}
    public int getProductId() {return productId;}
    public int getQuantity() {return quantity;}
    public int getTotalPrice() {return totalPrice;}
}
