function shutdownSystem() {
    if(!confirm("確定要關閉進貨系統嗎？")) {
        return;
    }
    fetch("/shutdown", {
        method: "POST"
    })
    .then(() => {
        document.body.innerHTML = `
            <div class="shutdown-message">
                <h1>進貨系統正在關閉</h1>
                <p>系統已停止執行，現在可以關閉此頁面。</p>
            </div> `;
    })
}