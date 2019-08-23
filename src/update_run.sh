#!/usr/bin/env bash

old=1
cd ../java-advanced-2019/ && git pull https://www.kgeorgiy.info/git/geo/java-advanced-2019.git
cd ../src
cp ../java-advanced-2019/lib/* lib/
cp ../java-advanced-2019/artifacts/* artifacts/

if [[ $# > 0 ]]; then
    bash run_test.sh $1
fi

