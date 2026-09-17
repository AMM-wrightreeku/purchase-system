# Purchase System

## 1. 專案目的
這是朋友委託製作的進貨登錄系統，現在主要處理進貨資料管理的部分。

## 2. 目前功能
可以從 CSV 讀取既有廠商資料。
建立進貨訂單、商品及進貨商品資料。
目前新增資料僅保存在記憶體中，尚未檔案持久化。

## 3. 系統架構
Browser / JavaScript
↓
HTTP Request / PurchaseRequest
↓
PurchaseController
↓
PurchaseService
↓
Repositories
        ├ SupplierRepository -> supplierList.csv + Map
        ├ ProductRepository -> Map
        ├ PurchaseItemRepository -> Map
        └ PurchaseRepository -> Map

## 4. 資料模型
Product
｜1    ├ id
｜    ├ name
｜    └ barcode
｜N
PurchaseItem
｜N   ├ id
｜    ├ productId
｜    ├ purchaseId
｜    ├ quantity
｜    └ totalPrice
｜1
Purchase
｜N   ├ id
｜    ├ supplierId
｜    ├ date
｜    └ note
｜1
Supplier
    ├ id
    └ name

資料規則：
- 相同 Product 的多列 PurchaseItem 保持獨立，不自動合併。
- unitPrice 不保存，由 totalPrice / quantity 計算。

## 5. 建立一筆進貨單的流程
Browser
↓
建立 PurchaseRequest
↓
POST
↓
PurchaseController
↓
PurchaseService.createFullPurchase(request)
↓
checkRequest(request)
↓
checkRequestData(request)
↓
getOrCreateProductIds(request)
↓
取得每一列對應的 productId
↓
PurchaseRepository.save(...)
↓
取得新建立的 purchaseId
↓
逐筆處理 request.items
↓
PurchaseItemRepository.save(
    purchaseId,
    productId,
    quantity,
    totalPrice
)
↓
return Purchase

## 6. 尚未完成

### 後端
1. 商品查詢功能
2. rollback 功能，避免造成不完整資料出現

### 前端
1. 商品查詢UI
2. 每列輸入 totalPrice，並計算出 unitPrice
3. 暫存商品可以修改刪除
4. 最終確認畫面顯示進貨總數及進貨總價
5. 查到既有商品後保留 productId，後續進貨可以使用既有 Product

### 資料保存
1. Product / Purchase / PurchaseItem 資料持久化保存

### 測試
1. PurchaseServiceTest 配合「PurchaseItem 不再合併」的新規則修改
2. 所有 Test 要重構
