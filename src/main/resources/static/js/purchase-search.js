let currentPurchases = [];

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
            currentPurchases = purchases;

            const resultDiv = document.getElementById("searchResult");
            document.getElementById("purchaseList").innerHTML = "";
            document.getElementById("purchaseDetail").innerHTML = "";
            
            resultDiv.innerHTML = "<h2>查詢結果</h2>";
            
            if(purchases.length === 0) {
                resultDiv.innerHTML += "<p>沒有符合條件的進貨紀錄</p>"
            }

            const supplierSummary = new Map();
            let grandQuantity = 0;
            let grandPrice = 0;
            
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

                    grandQuantity += item.quantity;
                    grandPrice += item.totalPrice;
                });
            });

            const resultList = document.getElementById("purchaseList");
            resultList.innerHTML = `
                <h2>查詢總表</h2>
                <div class="summary-table">
                    <div class="summary-row summary-header">
                        <span>供應商</span>
                        <span>總數量</span>
                        <span>總價格</span>
                    </div>
                </div>
                `;
            const summaryTable = resultList.querySelector(".summary-table");

            supplierSummary.forEach(summary => {
                summaryTable.innerHTML += `
                    <div class="summary-row">
                        <span>${summary.supplierName}</span>
                        <span>${summary.totalQuantity.toLocaleString()}</span>
                        <span>${summary.totalPrice.toLocaleString()}</span>
                    </div>
                `;
            });

            summaryTable.innerHTML += `
                <div class="summary-row summary-total">
                    <span>全部合計</span>
                    <span>${grandQuantity.toLocaleString()}</span>
                    <span>${grandPrice.toLocaleString()}</span>
                </div>
            `;

            purchases.sort((a, b) => {
                return (b.date > a.date) ? 1 : -1
            });

            purchases.forEach(purchase => {
                const orderTotal = purchase.items.reduce((sum, item) => sum + item.totalPrice,0);
                const previewItems = purchase.items.slice(0, 3);
                const previewHtml = previewItems.map(item => `
                    <div class="purchase-preview-row">
                        <span>${item.name}</span>
                        <span>${item.quantity}</span>
                        <span>${item.totalPrice.toLocaleString()}</span>
                    </div>
                `).join("");

                resultDiv.innerHTML += `
                    <div class="purchase-result">
                        <div class="purchase-result-info">
                            <p>
                                日期：${purchase.date}<br>
                                供應商：${purchase.supplierName}<br>
                                備註：${purchase.note ?? ""}
                            </p>
                            <button onclick="showPurchaseDetail(${purchase.id})">
                                查看明細
                            </button>
                        </div>
                        <div class="purchase-result-summary">
                            <div class="purchase-order-total">
                                訂單金額：
                                <strong>${orderTotal.toLocaleString()}</strong>
                            </div>
                            <div class="purchase-preview-header">
                                <span>商品名稱</span>
                                <span>數量</span>
                                <span>金額</span>
                            </div>
                            ${previewHtml}
                            ${purchase.items.length > 3
                                ? `<div class="purchase-more">
                                    其餘 ${purchase.items.length - 3} 筆商品，請查看明細
                                </div>`
                                : ""}
                        </div>
                    </div>
                `;
            });
        })
        .catch(error => {
            const resultDiv = document.getElementById("searchResult");
            resultDiv.innerHTML = "<p>查詢失敗</p>";
            console.error(error);
        })

})

function showPurchaseDetail(purchaseId) {
    const purchase = currentPurchases.find(
        purchase => purchase.id === purchaseId
    );

    if(!purchase) {
        console.error("找不到進貨單" + purchaseId);
        return;
    }

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
                <div class="purchase-item-name">
                    ${item.name}
                </div>
                <p>
                    條碼：${item.barcode ?? ""}<br>
                    數量：${item.quantity}<br>
                    進價：${item.totalPrice.toLocaleString()}
                </p>
            </div>
        `;
    });
    document.getElementById("purchaseDialog").showModal();
}

document.getElementById("closeDetailButton")
    .addEventListener("click", function() {
        document.getElementById("purchaseDialog").close();
    });