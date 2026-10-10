function loadProducts() {
    fetch("/products")
    .then(response => response.json())
    .then(products => {
        const productList = document.getElementById("productList");
        productList.innerHTML = "<h2>商品列表</h2>";

        products.forEach(product => {
            productList.innerHTML += `
                <div class="product">
                    <p>
                        ID：${product.id}<br>
                        條碼：${product.barcode ?? ""}<br>
                        商品名稱：${product.name}
                    </p>
                </div>
        `});
    })
}
loadProducts();

document.getElementById("createButton").addEventListener("click", function() {
    const barcode = document.getElementById("barcode").value;
    const name = document.getElementById("name").value;
    const request = {
        barcode: barcode || null,
        name: name
    };
    fetch("/products", {
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

})