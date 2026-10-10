fetch("/suppliers")
    .then(response => response.json())
    .then(suppliers => {
        const supplierSelect = document.getElementById("supplierId");
        suppliers.forEach(supplier => {
            supplierSelect.innerHTML += `
            <option value="${supplier.id}">
                ${supplier.name}
            </option>`;
        })
    })

document.getElementById("searchButton").addEventListener("click", function() {
    const supplierId = document.getElementById("supplierId").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;

    const parms = new URLSearchParams();
    if(supplierId) {parms.append("supplierId", supplierId);}
    if(startDate) {parms.append("startDate", startDate);}
    if(endDate) {parms.append("endDate", endDate);}

    fetch("/purchases?" + parms.toString())
        .then(response => {
            if(!response.ok) {
                console.error(response.status);
                console.error(response.statusText);
                throw new Error("查詢失敗");
            }
            return response.json();
        })
        .then(purchases => {
            const resultDiv = document.getElementById("searchResult");
            resultDiv.innerHTML = "<h2>查詢結果</h2>";
            
            const resultList = document.getElementById("purchaseList");
            resultList.innerHTML = `
                <div class="search-title">
                    <h2>查詢總表</h2>
                    <span>供應商</span>
                    <span>總數量</span>
                    <span>總價格</span>
                </div>
                `;

            if(purchases.length === 0) {
                resultDiv.innerHTML += "<p>沒有符合條件的進貨紀錄</p>"
            }

            // 這裡我真的不會了
            const supplierSummary = new Map();
            
            purchases.forEach(purchase => {
                const supplierId = purchase.supplierId;
                if(!supplierSummary.has(supplierId)) {
                    supplierSummary.set(supplierId, {
                        supplierName: purchase.supplierName,
                        totalQuantity: 0,
                        totalPrice: 0
                    });
                }
                const summary = supplierSummary.get(supplierId);

                purchase.items.forEach(item => {
                    summary.totalQuantity += item.quantity;
                    summary.totalPrice += item.totalPrice;
                });
            });
            supplierSummary.forEach(summary => {
                resultList.innerHTML += `
                    <div class="search-row">
                        <span>${summary.supplierName}</span>
                        <span>${summary.totalQuantity}</span>
                        <span>${summary.totalPrice}</span>
                    </div>
                `;
            });

            purchases.forEach(purchase => {
                resultDiv.innerHTML += `
                    <div class="purchase-result">
                        <p>
                            日期：${purchase.date}<br>
                            供應商：${purchase.supplierName}<br>
                            備註：${purchase.note ?? ""}
                        </p>
                        <button onclick="showPurchaseDetail(${purchase.id})">
                            查看明細
                        </button>
                    </div>
                    `;
            })
        })
        .catch(error => {
            const resultDiv = document.getElementById("searchResult");
            resultDiv.innerHTML = "<p>查詢失敗</p>";
            console.error(error);
        })

})

function showPurchaseDetail(purchaseId) {
    fetch("/purchases/" + purchaseId)
        .then(response => response.json())
        .then(purchase => {
            const detailDiv = document.getElementById("purchaseDetail");

            detailDiv.innerHTML = `
                <h2>進貨明細</h2>
                <div class="purchase-detail">
                    <p>
                        日期：${purchase.date}<br>
                        供應商：${purchase.supplierName}<br>
                        備註：${purchase.note ?? ""}
                    </p>
                </div>`;
            purchase.items.forEach(item => {
                detailDiv.innerHTML += `
                    <div class="purchaseItem-result">
                        <p>
                            商品名稱：${item.name}<br>
                            條碼：${item.barcode ?? ""}<br>
                            數量：${item.quantity}<br>
                            進價：${item.totalPrice}
                        </p>
                    </div>`;
            })
        })
}