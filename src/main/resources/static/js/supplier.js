function loadSuppliers() {
    fetch("/suppliers")
    .then(response => response.json())
    .then(suppliers => {
        const supplierList = document.getElementById("supplierList");
        supplierList.innerHTML = "<h2>廠商列表</h2>";

        suppliers.forEach(supplier => {
            supplierList.innerHTML += `
                <div class="supplier">
                    <p>
                        ID：${supplier.id}<br>
                        廠商：${supplier.name}
                    </p>
                </div>`;
        })
    })
}
loadSuppliers();

document.getElementById('createButton').addEventListener("click", function() {
    const name = document.getElementById("name").value;
    const request = {
        name: name
    };
    
    fetch("/suppliers", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("新增廠商失敗");
        }
        return response.json();
    })
    .then(() => {
        document.getElementById("name").value = "";
        loadSuppliers();
    })
    .catch(error => {
        alert("新增廠商失敗");
        console.log(error);
    })
})