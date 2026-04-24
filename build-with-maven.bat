@echo off
setlocal

echo ============================================
echo   Code Snippet Manager - Auto Build
echo ============================================
echo.

:: Check if Maven is already available
where mvn >nul 2>&1
if %errorlevel% == 0 (
    echo Maven found in system PATH
    goto :build
)

:: Check for local Maven
if exist ".mvn\apache-maven-3.9.6\bin\mvn.cmd" (
    echo Using local Maven installation
    set "PATH=%CD%\.mvn\apache-maven-3.9.6\bin;%PATH%"
    goto :build
)

:: Download Maven
echo Maven not found. Downloading Apache Maven 3.9.6...
echo This is a one-time setup.
echo.

if not exist ".mvn" mkdir .mvn

echo Downloading... (this may take a minute)
powershell -Command "Invoke-WebRequest -Uri 'https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip' -OutFile '.mvn\maven.zip' -UseBasicParsing"

if not exist ".mvn\maven.zip" (
    echo Failed to download Maven automatically.
    echo.
    echo Please install Maven manually:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to: C:\apache-maven-3.9.6
    echo 3. Add C:\apache-maven-3.9.6\bin to your PATH
    pause
    exit /b 1
)

echo Extracting...
powershell -Command "Expand-Archive -Path '.mvn\maven.zip' -DestinationPath '.mvn' -Force"
move ".mvn\apache-maven-3.9.6" ".mvn\apache-maven-3.9.6" >nul 2>&1
del ".mvn\maven.zip"

set "PATH=%CD%\.mvn\apache-maven-3.9.6\bin;%PATH%"
echo Maven setup complete!
echo.

:build
echo Building project...
call mvn clean package -q

if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED
    echo Check the error messages above
    pause
    exit /b 1
)

echo.
echo ============================================
echo   BUILD SUCCESSFUL!
echo ============================================
echo.
echo JAR file created: target\code-snippet-manager-1.0-SNAPSHOT.jar
echo.
set /p runnow=Do you want to run it now? (Y/N): 

if /i "%runnow%"=="Y" (
    echo.
    echo Starting Code Snippet Manager...
    java -jar target\code-snippet-manager-1.0-SNAPSHOT.jar
)

pause
