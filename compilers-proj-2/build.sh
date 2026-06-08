#!/bin/bash


set -euo pipefail


mkdir -p bin


javac -d bin $(find src -name "*.java")
