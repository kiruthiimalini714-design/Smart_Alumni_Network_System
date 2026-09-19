@echo off
setlocal enabledelayedexpansion
title Smart Alumni Network System - Launcher

echo ================================================================================
echo    SMART ALUMNI NETWORK SYSTEM - SATHYABAMA (SIST CSE AI)
echo    One-Click Windows Full-Stack Execution Launcher
echo ================================================================================
echo.

:: 1. Verify Java Availability
where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 'javac' was not found in your PATH!
    echo Please make sure Java JDK 17+ or 21+ or 26+ is installed and configured in PATH.
    echo.
    pause
    exit /b 1
)

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 'java' runtime was not found in your PATH!
    echo.
    pause
    exit /b 1
)

echo [OK] Java compiler and runtime detected.
javac -version
echo.

:: 2. Check for MySQL Connector JAR
if not exist "lib\mysql-connector-j.jar" (
    echo [ERROR] MySQL Connector JAR not found at lib\mysql-connector-j.jar!
    echo Please make sure the JAR file is in the lib/ folder.
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL Connector JAR found in lib\mysql-connector-j.jar.
echo.

:: 3. Create bin directory if not present
if not exist "bin" mkdir bin

:: 4. Compile Java Source Files
echo [BUILD] Compiling Java backend classes...
dir /s /b src\*.java > sources.txt
javac -cp "lib\mysql-connector-j.jar;." -d bin @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please review the error messages above.
    pause
    exit /b 1
)
echo [OK] All Java classes compiled successfully into bin/!
echo.

:: 5. Launch Browser after small delay in background
start "" cmd /c "timeout /t 2 >nul & start http://localhost:8080"

:: 6. Launch Backend Server
echo [SERVER] Starting Java SE HttpServer on http://localhost:8080...
echo [INFO] Press Ctrl+C in this window to stop the server.
echo.

java -cp "bin;lib\mysql-connector-j.jar;." Main

pause
