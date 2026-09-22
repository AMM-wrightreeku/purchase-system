# Purchase System

## 1. 專案目的
這是朋友委託製作的進貨登錄系統，現在主要處理進貨資料管理的部分。
目前以本機 CSV 作為資料儲存方式，完成可最低可用版本(MVP)後，再依實際使用需求持續調整功能。

## 2. 目前功能
### 進貨管理
- 建立進貨資料。
- 進貨時可使用既有商品，或建立新商品。
- 可依廠商、日期區間查詢進貨紀錄。
- 可查看單筆進貨紀錄及其商品明細。

### 商品管理
- 新增商品
- 顯示全部商品

### 廠商管理
- 新增廠商
- 顯示全部廠商

### 資料持久化
- Product、Supplier、Purchase、PurchaseItem 皆使用 CSV 持久化。
- 程式啟動時從 CSV 還原資料至記憶體。
- 程式重新起動後可繼續使用既有資料並接續 ID。

## 3. 系統架構
目前採用簡單的分層架構：

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

主要責任：
- Controller：處理 HTTP Request / Response，接受 DTO 並呼叫 Service。
- Service：處理商業邏輯、資料驗證及流程控制。
- Repository：負責資料存取及查詢。
- CSV：目前 MVP 使用的本機持久化方式。

目前 Repository 會將資料保存在記憶體中，並同步寫入 CSV。
程式啟動時會讀取 CSV，重新建立記憶體中的資料。

## 4. 資料模型
### Product
- id
- barcode   - 可以 null
- name      - 必填

### Supplier
- id
- name      - 不可與既有 Supplier 重複。

### Purchase
- id
- supplierId   - 對應 Supplier
- date
- note

一筆 Purchase 代表一次進貨紀錄。

### PurchaseItem
- id
- purchaseId   - 對應 Purchase
- productId    - 對應 Product
- quantity     - 必須大於等於 1
- totalPrice   - 必須大於等於 0

一筆 Purchase 代表某次進貨中的一筆商品明細。
同一 Product 可以在同一筆或不同 Purchase 中出現多次。
系統不會自動合併相同 Product 的 PurchaseItem。

## 5. CSV 持久化
目前 MVP 使用本機 CSV 檔案作為資料持久化方式。

### 儲存方式
Product、Supplier、Purchase、PurchaseItem 各自使用獨立的 CSV 檔案。

新增資料：
1. Repository 將 CREATE 狀態的紀錄寫入 CSV。
2. CSV 寫入成功後，再將資料加入記憶體中的 Map。
   避免 CSV 寫入失敗時，記憶體中卻已存在該筆資料。

### 啟動 Replay
啟動程式會讀取各 CSV 檔案，依照 STATUS 紀錄重新建立記憶體中的資料。
Replay 完成後，會依現有資料接續產生新的 ID，避免程式重新啟動後 ID 從 0 開始。

### CSV 格式
Product        | STATUS,ID,BARCODE,NAME
Supplier       | STATUS,ID,NAME
Purchase       | STATUS,ID,SUPPLIER_ID,DATE,NOTE
PurchaseItem   | STATUS,ID,PURCHASE_ID,PRODUCT_ID,QUANTITY,TOTAL_PRICE

目前僅實作 CREATE。
UPDATE、DELETE 與完整的歷史紀錄處理方式留待後續版本實作。

### 限制
- 尚未處理 CSV 欄位中的特殊字元。
- 尚未實作跨多個 Repository 的 transaction / rollback。
- 未知的 STATUS 目前採忽略處理。

## 6. 商品識別規則
建立 PurchaseItem 時，依照以下順序判斷 Product：
1. 有 productId
   - 以 productId 對應的 Product 為準。
   - barcode、name 不作為商品判斷依據。
   - productId 不存在時視為錯誤。

2. 無 productId，但有 barcode
   - 以 barcode 查詢既有 Product。
   - 找到時取得該 Product 的 productId，並依照 productId 規則處理。
   - 找不到時視為建立新 Product。

3. 無法對應既有 Product
   - 視為建立新 Product。
   - name 必填。
   - barcode 可以為 null。
   - 建立時會自動建立新 productId。

目前前端尚未提供直接選擇或輸入 productId 的功能。

## ７. 後續開發

### 功能
- PurchaseItem 暫存清單支援修改、刪除。
- 顯示進貨數量與總金額。
- Product、Supplier、Purchase、PurchaseItem 的 UPDATE / DELETE。
- Product 直接選擇或以 productId 操作。
- 完善輸入資料的錯誤提示與 HTTP 錯誤處理。

### 資料儲存
- CSV 特殊字元處理。
- UPDATE / DELETE 的 Replay。
- CSV 歷史紀錄整理與壓縮。
- 跨 Repository 的 transaction / rollback。
- Repository 抽象化，未來可切換 CSV / MySQL。

### 測試
- 補充 Service、Repository 等自動化測試。
- 補充各種錯誤與邊界條件測試。

### 待確認需求
- 使用既有 Product 進貨時，畫面應顯示本次輸入的商品名稱，或 Product 中保存的正式名稱。