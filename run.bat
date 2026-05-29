@echo off
setlocal
cd /d "%~dp0"

set "H2_JAR=lib\h2.jar"
set "BUILD_DIR=build\classes"
set "SOURCES_FILE=build\sources.txt"
set "MAIN_CLASS=Main"

if /I "%~1"=="test" (
  set "MAIN_CLASS=test.unit.DbSmokeTest"
)

if not exist "lib" mkdir "lib"
if not exist "%H2_JAR%" (
  echo Downloading H2 database driver...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar' -OutFile 'lib/h2.jar'"
  if errorlevel 1 exit /b 1
)

if not exist "build" mkdir "build"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"
for /r "src" %%f in (*.java) do echo %%f>>"%SOURCES_FILE%"

echo Compiling source...
javac -encoding UTF-8 -d "%BUILD_DIR%" -cp "%H2_JAR%" @"%SOURCES_FILE%"
if errorlevel 1 exit /b 1

java -cp "%BUILD_DIR%;%H2_JAR%" %MAIN_CLASS%
