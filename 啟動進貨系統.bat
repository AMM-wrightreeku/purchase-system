@echo off
title 進貨系統 - 關閉 PurchaseSystem 視窗即可結束
cd /d "%~dp0"

start "" "PurchaseSystem.exe"

timeout /t 4 /nobreak >nul

start "" "http://localhost:8080"