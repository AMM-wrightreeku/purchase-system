let supplierData = [];

function loadSuppliers() {
    fetch("/suppliers")
    .then(response => response.json())
    .then(suppliers => {
        supplierData = suppliers;

        const supplierList = document.getElementById("supplierList");
        supplierList.innerHTML = "<h2>廠商列表</h2>";

        suppliers.sort((a, b) => {
            if(a.status === b.status) {
                return a.id - b.id;
            }
            return a.status === "ACTIVE" ? -1 : 1;
        });

        suppliers.forEach(supplier => {
            supplierList.innerHTML += `
                <div class="supplier">
                    <p>
                        ID：${supplier.id}<br>
                        廠商：${supplier.name}
                    </p>
                        ${supplier.status === "ACTIVE" ? `
                            <button onclick="updateButton(${supplier.id})">修改</button>
                            <button onclick="deleteButton(${supplier.id})">刪除</button>
                            ` : ""}
                </div>`;
        })
    })
}

function createButton() {
    const name = document.getElementById("name").value;

    if (name.trim() === "") {
        alert("廠商名稱不可為空白");
        return;
    }

    const request = {
        name: name
    };
    
    fetch("/suppliers/create", {
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
}

function updateButton(id) {
    const supplier = supplierData.find(s => s.id === id);
    if(supplier === undefined)  {return;}
    const oldName = supplier.name;

    const newName = prompt("請輸入新的廠商名稱", oldName);
    if(newName === null) {return;}
    if(newName.trim() === "") {
        alert("廠商名稱不可為空白");
        return;
    }
    if(newName === oldName) {return;}

    const request = {
        id: id,
        name: newName
    };
    
    fetch("/suppliers/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("修改廠商失敗");
        }
        return response.json();
    })
    .then(() => {
        loadSuppliers();
    })
    .catch(error => {
        alert("修改廠商失敗");
        console.log(error);
    })
}

function deleteButton(id) {
    const confirmed = confirm("確定要刪除這個廠商嗎？");
    if (!confirmed) {return;}

    const request = {
        id: id,
    };
    
    fetch("/suppliers/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if(!response.ok) {
            throw new Error("刪除廠商失敗");
        }
        return response.json();
    })
    .then(() => {
        loadSuppliers();
    })
    .catch(error => {
        alert("刪除廠商失敗");
        console.log(error);
    })
}

// main
loadSuppliers();