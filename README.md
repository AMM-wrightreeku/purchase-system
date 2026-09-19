# Purchase System

## 1. 專案目的
這是朋友委託製作的進貨登錄系統，現在主要處理進貨資料管理的部分。

## 2. 目前功能
- 可以從 CSV 讀取既有廠商資料。
- 可以建立進貨訂單、商品及進貨商品資料。
<!-- 目前新增資料僅保存在記憶體中，尚未檔案持久化。 -->
- Product, Purchase, PurchaseItem 已支援 CSV 持久化：
  - 新增 Product 時寫入 CSV
  - 程式啟動時從 CSV 讀取 Product 資料
  - 程式重新起動後可以繼續使用既有資料及接續 ID

## 3. 系統架構
Browser / JavaScript
↓
HTTP Request / DTO
↓
Controller
↓
Service
↓
Repository
↓
CSV / Map
------------
Repository Operation:

Supplier
→ supplierList.csv + Map

Product
→ productList.csv + Map

Purchase
→ purchaseLit.csv + Map

PurchaseItem
→ purchaseItemList.csv + Map
------------
資料儲存以 CSV 為主，先行建立 'StorageType' 待擴充
- CSV
- MYSQL

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
- Product 的 barcode 可以為空
- 已存在的 Product 使用 productId 為主要辨識，
  同 productId 會忽略 barcode, name 的比較

## 5 持久化

Product
STATUS,ID,BARCODE,NAME
- 目前採用 APPEND 新增紀錄

- Repository 啟動流程：
ProductRepository
↓
檢查 productList.csv
↓
不存在 → 建立 data 目錄及 CSV
↓ 
讀取 CREATE 紀錄
↓
建立 Product
↓
還原至 Map

- 新增 Product 流程：
ProductRepository.save(...)
↓
建立 Product
↓
寫入 CSV
↓
寫入成功
↓
更新 Map
↓
return Product

CSV 持久化成功才會將新增資料視為 Repository 儲存成功

## 6. 建立一筆進貨單的流程
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

## 7. 2026/09/27 預定最低可用版本目標

<!-- 1. Purchase 可以持久化儲存，讓實際使用者開始保存進貨資料。 -->
---完成進度---
2. Purchase 可以查詢持久化資料，最低支援：
   - 依廠商查詢
   - 依日期查詢
3. Product 獨立頁面：
   - 新增 Product
   - 顯示全部 Product
4. Supplier 獨立頁面：
   - 新增 Supplier
   - 顯示全部 Supplier
5. Purchase 獨立頁面：
   - 新增 Purchase
   - 查詢 Purchase

## 8. 後續開發

### 持久化
- Purchase CSV 持久化。
- PurchaseItem CSV 持久化。
- Supplier 新增資料的持久化。
- 未來評估 MySQL 等其他資料儲存方式。
- 視需要將 Repository 介面化並抽象實作，使 Service 不依賴實際儲存技術。

### CSV
目前實作
- CREATE

未來預計加入
- UPDATE
- DELETE

目前未知的 STATUS 先忽略，僅判讀CREATE

未來重構時：
- UPDATE / DELETE replay。
- 未知 STATUS 改為嚴格驗證並在資料異常時停止載入。
- 保存歷史最大 ID，避免 DELETE 後重新使用舊 ID。
- 舊資料轉移／整理機制。
- 將歷史紀錄整理成目前狀態，改善大量歷史資料造成的啟動讀取效率。

### 後端
- Purchase 依廠商查詢。
- Purchase 依日期查詢。
- rollback / transaction 機制，避免建立完整 Purchase 過程中產生部分持久化資料。
- CSV 格式及特殊字元處理。

### 前端
- Product 獨立管理頁面。
- Supplier 獨立管理頁面。
- Purchase 新增／查詢頁面。
- 暫存商品修改、刪除。
- 最終確認畫面顯示進貨總數量及進貨總價。
- 查到既有商品後保留 productId，後續進貨使用既有 Product。

### 測試
為壓縮 2026/09/27 最低可用版本的開發時間，目前暫緩 Test。

最低可用版本完成後：
- 重構 PurchaseServiceTest。
- 配合 PurchaseItem 不自動合併的新規則更新測試。
- 補充 CSV persistence 測試。
- 補充重新啟動後資料還原相關測試。
- 逐步補齊 Repository / Service 測試。