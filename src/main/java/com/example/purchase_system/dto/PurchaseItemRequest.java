package com.example.purchase_system.dto;

public class PurchaseItemRequest {
    private Integer productId;
    private String barcode;
    private String productName;
    private int quantity;
    private int purchasePrice;

    public PurchaseItemRequest() {}
    
    public PurchaseItemRequest(Integer productId, String barcode, String productName
                                , int quantity, int purchasePrice) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        
    }

    public Integer getProductId() {return productId;}
    public String getBarcode() {return barcode;}
    public String getProductName() {return productName;}
    public int getQuantity() {return quantity;}
    public int getPurchasePrice() {return purchasePrice;}
}