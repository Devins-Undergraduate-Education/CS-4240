#!/bin/bash
set -euo pipefail

JAR="antlr-4.13.2-complete.jar"
OUT_DIR="antlr_generated"
BUILD_DIR="build"

if [ $# -ne 1 ]; then
    echo "Usage: run.sh <source.tiger>"
    exit 1
fi

if [ ! -f "$JAR" ]; then
    echo "Downloading $JAR..."
    curl -O "https://www.antlr.org/download/${JAR}"
fi

rm -rf "$OUT_DIR" "$BUILD_DIR"
mkdir -p "$OUT_DIR" "$BUILD_DIR"

java -Xmx500M -cp "$JAR" org.antlr.v4.Tool -Dlanguage=Java -o "$OUT_DIR" tiger.g4

javac -cp "$JAR:$OUT_DIR" -d "$BUILD_DIR" "$OUT_DIR"/*.java src/*.java

java -cp "$JAR:$OUT_DIR:$BUILD_DIR" Main "$1"
