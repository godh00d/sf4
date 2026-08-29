@echo off
setlocal
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0Install-SF4Angel.ps1" %*
if errorlevel 1 (
    echo.
    echo Installation failed. Read the error above before closing this window.
) else (
    echo.
    echo Installation complete.
)
pause
