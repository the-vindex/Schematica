#!/usr/bin/env bash
# Copy third-party compile-only mod jars from the local e36 instance into a project-local Maven
# repository under libs/maven (git-ignored). Unimined only remaps module dependencies, not plain files.
# Never commit these jars.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="${E36_MODS:-/mnt/minecraft-e36/mods}"
REPO="$ROOT/libs/maven"
install() { # jar artifactId version
  local dir="$REPO/local/$2/$3"
  mkdir -p "$dir"
  cp "$SRC/$1" "$dir/$2-$3.jar"
  cat > "$dir/$2-$3.pom" <<POM
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion>
<groupId>local</groupId><artifactId>$2</artifactId><version>$3</version></project>
POM
  echo "installed local:$2:$3"
}
install LunatriusCore-1.12.2-1.2.0.42-universal.jar lunatriuscore 1.2.0.42
install DankNull-1.12.2-1.7.91.jar danknull 1.7.91
install p455w0rdslib-1.12.2-2.3.161.jar p455w0rdslib 2.3.161
