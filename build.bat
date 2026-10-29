@echo off
:: Build script for CakeClient Mod
:: Builds the mod using Fabric Loom Gradle plugin

echo ==========================================
echo CakeClient Mod Builder
echo ==========================================
echo.

:: Use the existing Gradle installation
set GRADLE_CMD=".gradle\wrapper\dists\gradle-9.6.0-bin\42k10rwplmzkhuboz9kdazi7s\gradle-9.6.0\bin\gradle"

echo Using Gradle from: %GRADLE_CMD%
echo.

:: Change to the project directory if needed
cd /d "%~dp0"

echo Building CakeClient for Minecraft 1.21.11...
echo.

%GRADLE_CMD% build --no-daemon %*

if %errorlevel% eq 0 (
    echo.
    echo ==========================================
    echo Build completed successfully!
    echo Output: build/libs/cakeclient-6.9.8+1.21.11.jar
    echo ==========================================
) else (
    echo.
    echo ==========================================
    echo Build FAILED with errors!
    echo ==========================================
    exit /b 1
)
