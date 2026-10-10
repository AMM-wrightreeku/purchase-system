fetch("/suppliers")
        .then(function(response) {
            return response.json();
        })
        .then(function(suppliers) {

            const supplierSelect =
                document.getElementById("supplierId");

            for(const supplier of suppliers) {

                supplierSelect.innerHTML +=
                    "<option value='" +
                    supplier.id +
                    "'>" +
                    supplier.name +
                    "</option>";
            }
        });


        // purchase items
        const items = [];
        let products = [];
        fetch("/products")
            .then(response => response.json())
            .then(data => {
                products = data;
            });

        const addItemButton =document.getElementById("addItemButton");

        addItemButton.addEventListener(
            "click",
            function() {

                // barcode
                const barcode =document.getElementById("barcode").value;
                // product name
                const productName =document.getElementById("productName").value;
                
                let existingProduct = null;

                if(barcode.trim() !== "") {
                    existingProduct = products.find(product =>
                        product.barcode === barcode
                    )
                }
                
                const productId = existingProduct ? existingProduct.id : null;
                
                if(productId === null && productName.trim() === "") {
                    alert("請輸入商品名稱");
                    return;
                }

                // quantity
                const quantity =
                    Number(
                        document.getElementById("quantity").value
                    );

                if(
                    !Number.isFinite(quantity) ||
                    quantity < 1
                ) {
                    alert("商品數量錯誤");
                    return;
                }


                // totlaPrice
                const totalPriceText =
                    document.getElementById("totalPrice").value;

                if(totalPriceText.trim() == "") {
                    alert("請輸入價格");
                    return;
                }

                const totalPrice =
                    Number(totalPriceText);

                if(
                    !Number.isFinite(totalPrice) ||
                    totalPrice < 0
                ) {
                    alert("價格錯誤");
                    return;
                }


                const item = {

                    productId: productId,

                    barcode: barcode,

                    productName: productName,

                    quantity: quantity,

                    totalPrice: totalPrice
                };

                items.push(item);

                const itemList = document.getElementById("itemList");
                itemList.innerHTML = `
                    <div class="item-title">
                        <span>條碼</span>
                        <span>商品</span>
                        <span>數量</span>
                        <span>進貨價格</span>
                    </div>`;
                for(const currentItem of items) {
                    itemList.innerHTML += `
                        <div class="item-row">
                            <span>${currentItem.barcode}</span>
                            <span>${currentItem.productName}</span>
                            <span>${currentItem.quantity}</span>
                            <span>${currentItem.totalPrice}</span>
                        </div>`;
                }
            }
        );

        // preview item list
        const previewButton =
            document.getElementById("previewButton");


        previewButton.addEventListener(
            "click",
            function() {


                if(items.length < 1) {

                    alert("商品清單不能空白");

                    return;
                }


                const supplierSelect =
                    document.getElementById("supplierId");


                const supplierId =
                    supplierSelect.value;


                if(supplierId.trim() == "") {

                    alert("廠商不能空白");

                    return;
                }


                const supplierName =

                    supplierSelect
                    .options[
                        supplierSelect.selectedIndex
                    ]
                    .text;


                const purchaseDate =

                    document.getElementById(
                        "purchaseDate"
                    ).value;


                if(purchaseDate == "") {

                    alert("日期不能空白");

                    return;
                }


                const note =

                    document.getElementById(
                        "note"
                    ).value;


                const previewArea =

                    document.getElementById(
                        "previewArea"
                    );


                previewArea.innerHTML =
                    "供應商: " +supplierName +"<br>" +
                    "日期: " +purchaseDate +"<br>" +
                    "備註: " +note +"<br><br>";
                previewArea.innerHTML += `
                    <div class="preview-title">
                        <span>條碼</span>
                        <span>商品</span>
                        <span>數量</span>
                        <span>進貨價格</span>
                        <span>單價</span>
                    </div>`;
                let allQty = 0;
                let allPrice = 0;
                for(const currentItem of items) {
                    const unitPrice = currentItem.totalPrice / currentItem.quantity;
                    previewArea.innerHTML +=`
                        <div class="preview-row">
                            <span>${currentItem.barcode}</span>
                            <span>${currentItem.productName}</span>
                            <span>${currentItem.quantity}</span>
                            <span>${currentItem.totalPrice}</span>
                            <span>${unitPrice.toFixed(2)}</span>
                        </div>`;
                    allQty += currentItem.quantity;
                    allPrice += currentItem.totalPrice;
                }
                previewArea.innerHTML +=`
                    <br>
                    <div class="preview-total">
                        <span class="total-quantity">${allQty}<br>進貨總量</span>
                        <span class="total-price">${allPrice}<br>總價</span>
                    </div>        
                    <button id='saveButton'>確認儲存</button>`;
                    const saveButton =

                    document.getElementById(
                        "saveButton"
                    );


                saveButton.addEventListener(
                    "click",
                    function() {


                        const purchaseRequest = {

                            supplierId:
                                Number(supplierId),

                            purchaseDate:
                                purchaseDate,

                            note:
                                note,

                            items:
                                items
                        };

                        console.log(JSON.stringify(purchaseRequest));

                        fetch("/purchases", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/json"
                                },
                                body: JSON.stringify(purchaseRequest)
                            }
                        )
                        .then(function(response) {
                            if(!response.ok) {
                                throw new Error("儲存失敗");
                            }
                            return response.json();
                        })
                        .then(function(data) {
                            console.log(data);
                            alert("進貨資料儲存成功");  
                            // clear all
                            items.length = 0;
                            document.getElementById("barcode").value = "";
                            document.getElementById("productName").value = "";
                            document.getElementById("quantity").value = "";
                            document.getElementById("totalPrice").value = "";
                            document.getElementById("note").value = "";
                            document.getElementById("itemList").innerHTML ="尚未加入商品";
                            document.getElementById("previewArea").innerHTML ="尚未產生確認內容";}
                        )
                        .catch(function(error) {
                            console.error(error);
                            alert("進貨儲存失敗");
                        });
                    }
                );
            }
        );