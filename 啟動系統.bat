@echo off
title 進貨系統
cd /d "%~dp0"

start "" "PurchaseSystem.exe"

timeout /t 4 /nobreak >nul

start "" "http://localhost:8086/index.html"

exit