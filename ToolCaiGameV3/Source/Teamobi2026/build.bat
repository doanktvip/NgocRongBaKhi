@echo off
title Auto Build Server Teamobi 2026
cd /d "%~dp0"
chcp 65001 >nul

echo =======================================================
echo          HE THONG TU DONG BIEN DICH SERVER
echo =======================================================
echo.

set "JAVAC_CMD="
set "JAR_CMD="

if exist "C:\Program Files\Java\jdk-21.0.11\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\jdk-21.0.11\bin\javac.exe"
    set "JAR_CMD=C:\Program Files\Java\jdk-21.0.11\bin\jar.exe"
) else if exist "C:\Program Files\Java\latest\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\latest\bin\javac.exe"
    set "JAR_CMD=C:\Program Files\Java\latest\bin\jar.exe"
) else if exist "C:\Program Files\Java\jdk-17\bin\javac.exe" (
    set "JAVAC_CMD=C:\Program Files\Java\jdk-17\bin\javac.exe"
    set "JAR_CMD=C:\Program Files\Java\jdk-17\bin\jar.exe"
) else if defined JAVA_HOME (
    set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
    set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
) else (
    where javac >nul 2>&1
    if not errorlevel 1 (
        set "JAVAC_CMD=javac"
        set "JAR_CMD=jar"
    ) else (
        echo [LOI] Khong tim thay trinh bien dich javac!
        echo Vui long cai dat JDK hoac them JDK vao bien moi truong PATH.
        pause
        exit /b 1
    )
)

if not exist "build\classes" mkdir "build\classes"

echo [1/3] Dang quet toan bo file ma nguon trong thu muc src...
dir /s /b src\*.java > "%TEMP%\teamobi_sources.txt" 2>nul

echo [2/3] Dang bien dich ma nguon Java (javac)...
"%JAVAC_CMD%" -encoding UTF-8 -cp "build\classes;lib\*" -d build\classes @"%TEMP%\teamobi_sources.txt"
if errorlevel 1 (
    echo.
    echo =======================================================
    echo [THAT BAI] Co loi trong qua trinh bien dich ma nguon!
    echo Vui long sua lai cac loi code thong bao o tren.
    echo =======================================================
    del "%TEMP%\teamobi_sources.txt" >nul 2>&1
    pause
    exit /b 1
)

del "%TEMP%\teamobi_sources.txt" >nul 2>&1

echo [3/3] Dang dong goi cap nhat vao Teamobi2026.jar...
"%JAR_CMD%" uf Teamobi2026.jar -C build\classes .
if errorlevel 1 (
    echo.
    echo =======================================================
    echo [THAT BAI] Khong the cap nhat file Teamobi2026.jar!
    echo Hay dam bao rang Server dang TAT truoc khi build.
    echo =======================================================
    pause
    exit /b 1
)

echo.
echo =======================================================
echo [THANH CONG] Da bien dich va cap nhat Teamobi2026.jar!
echo Dang tu dong khoi dong Server (run.bat)...
echo =======================================================
echo.
call run.bat

