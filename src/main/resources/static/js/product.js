let productData = [];

function loadProducts() {
    fetch("/products") 
    .then(response => response.json())
    .then(products => {
        productData = products;

        const productList = document.getElementById("productList");
        productList.innerHTML = "<h2>商品列表</h2>";

        products.sort((a, b) => {
            if(a.status === b.status) {
                return a.id - b.id;
            }
            return a.status === "ACTIVE" ? -1 : 1 ;
        });

        products.forEach(product => {
            productList.innerHTML += `
                <div class="product">
                    <p>
                        ID：${product.id}<br>
                        條碼：${product.barcode ?? ""}<br>
                        商品名稱：${product.name}
                    </p>
                    ${product.status === "ACTIVE" ? `
                        <button onclick="updateButton(${product.id})")>修改</button>
                        <button onclick="deleteButton(${product.id})")>刪除</button>
                        ` : ""}
                </div>`;
        })
    })
}

function createButton() {
    const barcode = document.getElementById("barcode").value;
    const name = document.getElementById("name").value;

    if(name.trim() === "") {
        alert("商品名稱不可為空白");
        return;
    }

    const request = {
        barcode: barcode,
        name: name
    };

    fetch("/products/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("新增商品失敗");
        }
        return response.json();
    })
    .then(() => {
        document.getElementById("barcode").value = "";
        document.getElementById("name").value = "";
        loadProducts();
    })
    .catch(error => {
        alert("新增商品失敗");
        console.error(error);
    })
}

function updateButton(id) {
    console.log("1");
    const product = productData.find(p => p.id === id);
    if(product === undefined) {return;}
    const oldBarcode = product.barcode;
    const oldName = product.name;

    const newBarcode = prompt("請輸入新的商品條碼", oldBarcode ?? "");
    if(newBarcode === null) {return;}
    const newName = prompt("請輸入新的商品名稱", oldName ?? "");
    if(newName === null) {return;}
    if(newName.trim() === "") {
        alert("商品名稱不可為空白");
        return;
    }
    if(newBarcode === oldBarcode 
    && newName === oldName) {return;}

    const request = {
        id: id,
        barcode: newBarcode,
        name: newName
    };

    fetch("/products/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("修改商品失敗");
        }
        return response.json();
    })
    .then(() => {
        loadProducts();
    })
    .catch(error => {
        alert("修改商品失敗");
        console.error(error);
    })
}

function deleteButton(id) {
    const confirmed = confirm("確定要刪除這個商品嗎？");
    if(!confirmed) {return;}

    const request = {
        id: id
    };
    fetch("/products/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("刪除商品失敗");
        }
        return response.json();
    })
    .then(() => {
        loadProducts();
    })
    .catch(error => {
        alert("刪除商品失敗");
        console.error(error);
    })
}

// main
loadProducts();