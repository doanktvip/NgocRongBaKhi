@echo off
title Game Server Teamobi 2026
cd /d "%~dp0"

set "JAVA_CMD=java"
if exist "C:\Program Files\Java\jdk-21.0.11\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Java\jdk-21.0.11\bin\java.exe"
) else if exist "C:\Program Files\Java\latest\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Java\latest\bin\java.exe"
)

"%JAVA_CMD%" -server -Dfile.encoding=UTF-8 -jar Teamobi2026.jar
pause
