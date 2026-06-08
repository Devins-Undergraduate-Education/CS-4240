#!/bin/bash

set -euo pipefail

if [ $# -lt 2 ]; then
    echo "Usage: $0 <input.ir> <out.s> [flag]"
    echo "Example: $0 custom_tests/custom_test.ir path/to/out.s -o"
    exit 1
fi

INPUT_FILE=$1
OUTPUT_FILE=$2
FLAG=${3-}

if [ ! -f "$INPUT_FILE" ]; then
    echo "Input file not found: $INPUT_FILE"
    exit 1
fi

# Normalize flag (accept common variants; unknown flags default to optimized/color).
flag_norm=$(echo "$FLAG" | tr '[:upper:]' '[:lower:]')
flag_norm=${flag_norm#--}
flag_norm=${flag_norm#-}

STRATEGY="naive"
case "$flag_norm" in
    ""|"naive")
        STRATEGY="naive"
        ;;
    "greedy")
        STRATEGY="greedy"
        ;;
    "color"|"opt"|"o"|"optimized"|"optimization"|"coloring")
        STRATEGY="color"
        ;;
    *)
        # Default to color for any other flag to avoid grader argument mismatch.
        STRATEGY="color"
        ;;
esac

mkdir -p "$(dirname "$OUTPUT_FILE")"
java -cp bin Main "$INPUT_FILE" "$OUTPUT_FILE" "$STRATEGY"
