@echo off
title TCET Project Management System - Java Full Stack
echo ==================================================================
echo   TCET Mumbai - Industrial Program Mini-Project
echo   Full Stack Development using Java - Project Management System
echo ==================================================================
echo.

if not exist bin mkdir bin
if not exist data mkdir data

echo [*] Compiling Java backend classes...
javac -encoding UTF-8 -d bin src\com\tcet\pms\model\*.java src\com\tcet\pms\repository\*.java src\com\tcet\pms\service\*.java src\com\tcet\pms\util\*.java src\com\tcet\pms\controller\*.java src\com\tcet\pms\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [!] Compilation failed! Please ensure JDK is installed and in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo [+] Compilation successful!
echo [*] Launching Project Management System on http://localhost:8080 ...
echo.

start "" http://localhost:8080

java -cp bin com.tcet.pms.Main 8080

pause
