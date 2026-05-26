@echo off
cd /d "%~dp0"
if not exist "lib\h2.jar" (
  echo Downloading H2 database driver...
  mkdir lib 2>nul
  powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar' -OutFile 'lib\h2.jar'"
)
if not exist "build\classes\Main.class" (
  echo Compiling...
  mkdir build\classes 2>nul
  for /r src %%f in (*.java) do set JAVAFILES=!JAVAFILES! "%%f"
  javac -encoding UTF-8 -d build/classes -cp lib\h2.jar src\dao\*.java src\model\*.java src\view\user\*.java src\view\post\*.java src\view\chat\*.java src\view\report\*.java src\view\stat\*.java src\view\notification\*.java src\test\unit\*.java src\Main.java
)
java -cp "build/classes;lib/h2.jar" Main
