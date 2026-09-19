package com.example.purchase_system.dto;

public class PurchaseItemDetailResponse {
    private int id;
    private int productId;
    private String barcode;
    private String name;
    private int quantity;
    private int totalPrice;

    public PurchaseItemDetailResponse(
            int id, int productId, String barcode
            , String name, int quantity, int totalPrice) {
        this.id = id;
        this.productId = productId;
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {return id;}
    public int getProductId() {return productId;}
    public String getBarcode() {return barcode;}
    public String getName() {return name;}
    public int getQuantity() {return quantity;}
    public int getTotalPrice() {return totalPrice;}
}
