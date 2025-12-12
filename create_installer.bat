@echo off
echo ==========================================
echo      InventarioHB2 Installer Builder
echo ==========================================

set "JAVA_HOME=C:\Program Files\Java\jdk-25"
if not exist "%JAVA_HOME%" (
    echo [ERROR] JAVA_HOME not found at %JAVA_HOME%
    exit /b 1
)
set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
set "JPACKAGE_CMD=%JAVA_HOME%\bin\jpackage.exe"

echo [1/6] Cleaning up previous build...
if exist dist rmdir /s /q dist
if exist installer rmdir /s /q installer
if exist sources.txt del sources.txt

echo [2/6] Preparing directories...
mkdir dist
mkdir dist\lib
mkdir installer

echo [3/6] Copying libraries...
copy lib\*.jar dist\lib\ >nul

echo [4/6] Compiling source code...
:: Create list of valid Java files (ignoring non-java files in src)
dir /s /b src\*.java > sources.txt
"%JAVAC_CMD%" -d bin -cp "lib\*" @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed!
    rem pause
    exit /b %errorlevel%
)

echo [5/6] Creating JAR file...
"%JAR_CMD%" cfm dist\InventarioHB2.jar MANIFEST.MF -C bin .
if %errorlevel% neq 0 (
    echo [ERROR] JAR creation failed!
    rem pause
    exit /b %errorlevel%
)

echo [6/6] Generating Installer via jpackage...
"%JPACKAGE_CMD%" ^
  --name InventarioHB2 ^
  --input dist ^
  --main-jar InventarioHB2.jar ^
  --main-class view.MainApp ^
  --type exe ^
  --app-version 1.0.2 ^
  --dest installer ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut 

if %errorlevel% neq 0 (
    echo.
    echo [WARNING] jpackage failed to create EXE. WiX Toolset might be missing.
    echo.
    echo Trying to create app-image...
    "%JPACKAGE_CMD%" ^
      --name InventarioHB2 ^
      --input dist ^
      --main-jar InventarioHB2.jar ^
      --main-class view.MainApp ^
      --type app-image ^
      --app-version 1.0.2 ^
      --dest installer
      
    if %errorlevel% neq 0 (
        echo [ERROR] Failed to create app-image.
        rem pause
        exit /b %errorlevel%
    ) else (
        echo [SUCCESS] Portable app-image created in 'installer/InventarioHB2'.
    )
) else (
    echo [SUCCESS] Installer created successfully in 'installer/' folder!
)

echo.
echo Build process finished.
del sources.txt
rem pause
