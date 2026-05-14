@echo off
echo ============================================
echo   Starting Event Management System...
echo ============================================

cd /d "%~dp0"

java -cp "out;lib\*" main.Main

if %errorlevel% neq 0 (
    echo.
    echo Application exited with errors.
    pause
)

