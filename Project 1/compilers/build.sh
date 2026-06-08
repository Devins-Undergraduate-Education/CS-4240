#!/bin/bash

find materials/src -name "*.java" > materials/sources.txt
javac -d build @materials/sources.txt
echo "Build complete: classes in ./build"