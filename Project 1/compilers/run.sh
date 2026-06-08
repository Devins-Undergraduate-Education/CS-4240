#!/bin/bash

if [[ $# -eq 2 ]]; then
  in_ir="$1"
  out_ir="$2"
  java -cp build TigerOptimizer "$in_ir" "$out_ir"
  exit 0
fi

if [[ $# -eq 0 ]]; then
  echo "No args: optimizing public tests and running interpreter..."
  for prog in quicksort sqrt; do
    in_ir="materials/public_test_cases/$prog/$prog.ir"
    out_ir="materials/public_test_cases/$prog/${prog}_opt.ir"
    java -cp build TigerOptimizer "$in_ir" "$out_ir"
    for i in {0..9}; do
      java -cp build IRInterpreter "$out_ir" < "materials/public_test_cases/$prog/$i.in" > "materials/public_test_cases/$prog/$i.out"
    done
  done
  echo "Done."
  exit 0
fi

echo "Usage: $0 <input.ir> <output.ir>" >&2
exit 1
