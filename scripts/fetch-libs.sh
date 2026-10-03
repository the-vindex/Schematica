#!/usr/bin/env bash
# Install third-party compile-only mod jars from the local e36 instance into a project-local Maven
# repository under local-maven/ (git-ignored; NOT libs/: ForgeGradle 2 puts every file in libs/ on the compile classpath). The upstream Lunatrius ivy repo is gone. Never commit these jars.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="${E36_MODS:-/mnt/minecraft-e36/mods}"
REPO="$ROOT/local-maven"
install() { # jar groupId artifactId version classifier
  local dir="$REPO/${2//.//}/$3/$4"
  mkdir -p "$dir"
  cp "$SRC/$1" "$dir/$3-$4${5:+-$5}.jar"
  printf '<?xml version="1.0" encoding="UTF-8"?>\n<project xmlns="http://maven.apache.org/POM/4.0.0"><modelVersion>4.0.0</modelVersion><groupId>%s</groupId><artifactId>%s</artifactId><version>%s</version></project>\n' "$2" "$3" "$4" > "$dir/$3-$4.pom"
  echo "installed $2:$3:$4${5:+:$5}"
}
install LunatriusCore-1.12.2-1.2.0.42-universal.jar com.github.lunatrius LunatriusCore 1.12.2-1.2.0.42 universal
