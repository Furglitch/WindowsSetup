@echo off
setlocal

set "downloadURL=https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe"
set "destinationFolder=%TEMP%"
set "installerName=jreinstaller.exe"

echo Furglitch OS Setup
echo.

net session >nul 2>&1
if %errorlevel% neq 0 (
    echo Please run as administrator...
    pause
    exit /b 1
)

cd /d "%~dp0"

java -version >nul 2>&1
if %errorlevel% neq 0 (

    echo Java not detected...

    echo Downloading the latest JDK21 installer from Oracle...
    powershell -Command "(New-Object System.Net.WebClient).DownloadFile('%downloadUrl%', '%destinationFolder%\%installerName%')"

    if not exist "%destinationFolder%\%installerName%" (
        echo Error: Failed to download the JDK installer.
        exit /b 1
    )

    echo Installing the JDK...
    "%destinationFolder%\%installerName%" /s

    "C:\Program Files\Java\jdk-21\bin\java.exe" -version >nul 2>&1
    set "javaFail=false"
    if %errorlevel% neq 0  ( 
        if %errorlevel% neq 9009 (
            echo JDK installation failed.
            echo Error: %errorLevel%
            pause
            exit /b 1
        )
    )

    echo Java JDK 21 installation successful...
    echo.
    echo Running WindowsSetup.jar...
    start /min cmd /c ""C:\Program Files\Java\jdk-21\bin\java.exe" -jar "%~dp0WindowsSetup.jar""

) else ( 
    echo Java detected.
    echo.
    echo Running WindowsSetup.jar...
    start /min cmd /c "java -jar %~dp0WindowsSetup.jar"
)

timeout 5
endlocal
