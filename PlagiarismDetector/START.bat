@echo off
title Smart Plagiarism Detection System

echo ====================================================
echo   Smart Plagiarism Detection System
echo   PlagCoders - TCS-408 - JAVA-IV-T167
echo ====================================================
echo.

:: Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is NOT installed!
    echo.
    echo Please download and install Java from:
    echo https://adoptium.net
    echo.
    echo Press any key to open the download page...
    pause >nul
    start https://adoptium.net
    exit /b 1
)

:: Check if H2 JAR exists, if not download it
if not exist "lib\h2.jar" (
    echo [SETUP] Downloading H2 database driver...
    echo         This is a one-time download (about 2 MB)
    echo.
    powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar' -OutFile 'lib\h2.jar' -UserAgent 'PlagCoders/1.0' }"
    if not exist "lib\h2.jar" (
        echo.
        echo [ERROR] Auto-download failed. Please download manually:
        echo.
        echo  1. Open this URL in your browser:
        echo     https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar
        echo  2. Save the file as  h2.jar
        echo  3. Put it inside the  lib\  folder
        echo  4. Run START.bat again
        echo.
        pause
        exit /b 1
    )
    echo [OK] H2 driver downloaded successfully!
    echo.
)

echo [OK] Starting application...
java -cp "PlagiarismDetector.jar;lib\h2.jar" com.plagcoders.PlagiarismApp
pause
