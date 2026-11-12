@echo off
title Game Server NroBaKhi
cd /d "%~dp0"

set "JAVA_CMD=java"
for /d %%I in ("C:\Program Files\Java\jdk-21*") do (
    if exist "%%I\bin\java.exe" (
        set "JAVA_CMD=%%I\bin\java.exe"
    )
)

if "%JAVA_CMD%"=="java" (
    if exist "C:\Program Files\Java\latest\bin\java.exe" (
        set "JAVA_CMD=C:\Program Files\Java\latest\bin\java.exe"
    ) else if defined JAVA_HOME (
        if exist "%JAVA_HOME%\bin\java.exe" (
            set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        )
    )
)

"%JAVA_CMD%" -server -Dfile.encoding=UTF-8 -jar NroBaKhi.jar
pause
