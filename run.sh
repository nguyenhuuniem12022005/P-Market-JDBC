#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

H2_JAR="lib/h2.jar"
JUNIT_JAR="lib/junit-4.13.2.jar"
HAMCREST_JAR="lib/hamcrest-core-1.3.jar"
BUILD_DIR="build/classes"
SOURCES_FILE="build/sources.txt"
MAIN_CLASS="Main"
MAIN_ARGS=()
JUNIT_TESTS=(
  test.unit.AccountDaoTest
  test.unit.AccountStatDaoTest
  test.unit.CategoryDaoTest
  test.unit.ChatRoomDaoTest
  test.unit.ChatRoomMemberDaoTest
  test.unit.ImageDaoTest
  test.unit.MessageDaoTest
  test.unit.NotificationDaoTest
  test.unit.PostDaoTest
  test.unit.PostStatDaoTest
  test.unit.ReportDaoTest
  test.unit.ReportEvidenceDaoTest
  test.unit.UserNotificationDaoTest
)

download() {
  local url="$1"
  local output="$2"
  if command -v curl >/dev/null 2>&1; then
    curl -fL "$url" -o "$output"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$output" "$url"
  else
    echo "Need curl or wget to download dependencies." >&2
    exit 1
  fi
}

mkdir -p lib
if [[ ! -f "$H2_JAR" ]]; then
  echo "Downloading H2 database driver..."
  download "https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar" "$H2_JAR"
fi
if [[ ! -f "$JUNIT_JAR" ]]; then
  echo "Downloading JUnit..."
  download "https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar" "$JUNIT_JAR"
fi
if [[ ! -f "$HAMCREST_JAR" ]]; then
  echo "Downloading Hamcrest..."
  download "https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" "$HAMCREST_JAR"
fi

case "${1:-}" in
  test)
    MAIN_CLASS="test.unit.VerboseTestRunner"
    MAIN_ARGS=("${JUNIT_TESTS[@]}")
    ;;
  junit)
    MAIN_CLASS="test.unit.VerboseTestRunner"
    if [[ -n "${2:-}" ]]; then
      if [[ "$2" == *.* ]]; then
        MAIN_ARGS=("$2")
      else
        MAIN_ARGS=("test.unit.$2")
      fi
    else
      MAIN_ARGS=("${JUNIT_TESTS[@]}")
    fi
    ;;
  initdb)
    echo "Resetting H2 database from SQL files..."
    rm -rf data
    mkdir -p data
    java -cp "$H2_JAR" org.h2.tools.RunScript \
      -url "jdbc:h2:file:./data/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH" \
      -user sa \
      -script "database/schema.sql"
    java -cp "$H2_JAR" org.h2.tools.RunScript \
      -url "jdbc:h2:file:./data/pmarket;MODE=MySQL;DEFAULT_NULL_ORDERING=HIGH" \
      -user sa \
      -script "database/seed.sql"
    echo "Database initialized successfully."
    exit 0
    ;;
esac

mkdir -p build
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

find src -name "*.java" | sort > "$SOURCES_FILE"

echo "Compiling source..."
javac -encoding UTF-8 \
  -d "$BUILD_DIR" \
  -cp "$H2_JAR:$JUNIT_JAR:$HAMCREST_JAR" \
  @"$SOURCES_FILE"

java -cp "$BUILD_DIR:$H2_JAR:$JUNIT_JAR:$HAMCREST_JAR" "$MAIN_CLASS" "${MAIN_ARGS[@]}"
