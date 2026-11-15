@echo off
cd /d "%~dp0"
chcp 65001 >nul

set "JAVAC_CMD="
set "JAR_CMD="

for /d %%I in ("C:\Program Files\Java\jdk-21*") do (
    if exist "%%I\bin\javac.exe" (
        set "JAVAC_CMD=%%I\bin\javac.exe"
        set "JAR_CMD=%%I\bin\jar.exe"
    )
)

if not defined JAVAC_CMD (
    if exist "C:\Program Files\Java\latest\bin\javac.exe" (
        set "JAVAC_CMD=C:\Program Files\Java\latest\bin\javac.exe"
        set "JAR_CMD=C:\Program Files\Java\latest\bin\jar.exe"
    ) else if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\javac.exe" (
            set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
            set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
        )
    )
)

if not defined JAVAC_CMD (
    where javac >nul 2>&1
    if not errorlevel 1 (
        set "JAVAC_CMD=javac"
        set "JAR_CMD=jar"
    ) else (
        echo [ERROR] Khong tim thay trinh bien dich javac JDK 21!
        exit /b 1
    )
)

if not exist "build\classes" mkdir "build\classes"

dir /s /b src\*.java > "%TEMP%\nrobakhi_compile_check.txt" 2>nul

"%JAVAC_CMD%" -encoding UTF-8 -cp "build\classes;lib\*" -d build\classes @"%TEMP%\nrobakhi_compile_check.txt"
if errorlevel 1 (
    del "%TEMP%\nrobakhi_compile_check.txt" >nul 2>&1
    echo [COMPILE_FAILED] Co loi bien dich!
    exit /b 1
)

del "%TEMP%\nrobakhi_compile_check.txt" >nul 2>&1

"%JAR_CMD%" uf NroBaKhi.jar -C build\classes .
if errorlevel 1 (
    echo [PACK_JAR_FAILED] Khong the cap nhat NroBaKhi.jar!
    exit /b 1
)

echo [COMPILE_SUCCESS] Da bien dich va dong goi thanh cong NroBaKhi.jar!
exit /b 0
