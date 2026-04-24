@echo off
echo ============================================
echo    Code Snippet Manager - Quick Start
echo ============================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher from: https://adoptium.net/
    pause
    exit /b 1
)

echo Java found!

echo Building project...
mvn clean package -q

if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo Starting Code Snippet Manager...
echo.

java -jar target\code-snippet-manager-1.0-SNAPSHOT.jar

pause
