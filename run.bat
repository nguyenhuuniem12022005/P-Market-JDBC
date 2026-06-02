@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

set "H2_JAR=lib\h2.jar"
set "JUNIT_JAR=lib\junit-4.13.2.jar"
set "HAMCREST_JAR=lib\hamcrest-core-1.3.jar"
set "BUILD_DIR=build\classes"
set "SOURCES_FILE=build\sources.txt"
set "MAIN_CLASS=Main"
set "MAIN_ARGS="
set "JUNIT_TESTS=test.unit.AccountDaoTest test.unit.AccountStatDaoTest test.unit.CategoryDaoTest test.unit.ChatRoomDaoTest test.unit.ChatRoomMemberDaoTest test.unit.ImageDaoTest test.unit.MessageDaoTest test.unit.NotificationDaoTest test.unit.PostDaoTest test.unit.PostStatDaoTest test.unit.ReportDaoTest test.unit.ReportEvidenceDaoTest test.unit.UserNotificationDaoTest"

if /I "%~1"=="test" (
  set "MAIN_CLASS=test.VerboseTestRunner"
  set "MAIN_ARGS=%JUNIT_TESTS%"
)
if /I "%~1"=="junit" (
  set "MAIN_CLASS=test.VerboseTestRunner"
  set "MAIN_ARGS=%JUNIT_TESTS%"
  if not "%~2"=="" (
    set "TEST_NAME=%~2"
    echo %~2 | findstr /R "\." >nul
    if errorlevel 1 (
      set "TEST_NAME=test.unit.%~2"
    )
    set "MAIN_ARGS=!TEST_NAME!"
  )
)

if not exist "lib" mkdir "lib"
if not exist "%H2_JAR%" (
  echo Downloading H2 database driver...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar' -OutFile 'lib/h2.jar'"
  if errorlevel 1 exit /b 1
)
if not exist "%JUNIT_JAR%" (
  echo Downloading JUnit...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar' -OutFile 'lib/junit-4.13.2.jar'"
  if errorlevel 1 exit /b 1
)
if not exist "%HAMCREST_JAR%" (
  echo Downloading Hamcrest...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar' -OutFile 'lib/hamcrest-core-1.3.jar'"
  if errorlevel 1 exit /b 1
)

if /I "%~1"=="initdb" (
  echo Resetting H2 database from SQL files...
  if exist "data" rmdir /s /q "data"
  mkdir "data"
  java -cp "%H2_JAR%" org.h2.tools.RunScript -url "jdbc:h2:file:./data/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH" -user sa -script "database/schema.sql"
  if errorlevel 1 exit /b 1
  java -cp "%H2_JAR%" org.h2.tools.RunScript -url "jdbc:h2:file:./data/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH" -user sa -script "database/seed.sql"
  if errorlevel 1 exit /b 1
  echo Database initialized successfully.
  exit /b 0
)

if not exist "build" mkdir "build"
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"

if exist "%SOURCES_FILE%" del "%SOURCES_FILE%"
for /r "src" %%f in (*.java) do echo %%f>>"%SOURCES_FILE%"

echo Compiling source...
javac -encoding UTF-8 -d "%BUILD_DIR%" -cp "%H2_JAR%;%JUNIT_JAR%;%HAMCREST_JAR%" @"%SOURCES_FILE%"
if errorlevel 1 exit /b 1

java -cp "%BUILD_DIR%;%H2_JAR%;%JUNIT_JAR%;%HAMCREST_JAR%" %MAIN_CLASS% %MAIN_ARGS%
