#!/usr/bin/env bash
# Build Schematica with its original toolchain (ForgeGradle 2.3, Gradle wrapper, Java 8) in Docker.
# Output: build/libs/Schematica-1.12.2-1.8.0.169-universal.jar (owned by the calling user).
# The Gradle cache lives in the named volume "schematica-gradle" so rebuilds are fast.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
IMAGE=schematica-build:jdk8
[ -d "$ROOT/local-maven" ] || "$ROOT/scripts/fetch-libs.sh"
docker image inspect "$IMAGE" >/dev/null 2>&1 || docker build -t "$IMAGE" "$ROOT/docker"
docker volume inspect schematica-gradle >/dev/null 2>&1 || docker volume create schematica-gradle >/dev/null
# The volume is created root-owned; hand it to the calling user once.
docker run --rm -v schematica-gradle:/gradle "$IMAGE" chown "$(id -u):$(id -g)" /gradle
docker run --rm \
  -u "$(id -u):$(id -g)" \
  -e HOME=/gradle -e BUILD_NUMBER=169 \
  -v schematica-gradle:/gradle \
  -v "$ROOT":/src \
  "$IMAGE" ./gradlew --no-daemon build "$@"
ls -la "$ROOT/build/libs"
