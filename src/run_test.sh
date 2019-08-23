#!/usr/bin/env bash

if [[ $1 -eq 1 ]]; then
    echo "Testing Walk!"
    bash testers/test_walk.sh
fi
if [[ $1 -eq 2 ]]; then
    echo "Testing NavigableSet!"
    bash testers/test_array_set.sh
fi
if [[ $1 -eq 3 ]]; then
    echo "Testing StudentGroupQuery!"
    bash testers/test_students.sh
fi
if [[ $1 -eq 4 ]]; then
    echo "Testing Implementor!"
    bash testers/test_implementor.sh
fi
if [[ $1 -eq 5 ]]; then
    echo "Testing JarImplementor!"
    bash testers/test_jar_implementor.sh
fi
if [[ $1 -eq 6 ]]; then
    echo "Generating javadoc!"
    bash javadoc.sh
    echo "Done!"
    open -a "Google Chrome" ../javadoc/ru/ifmo/rain/badyaev/implementor/JarImplementor.html
fi
if [[ $1 -eq 7 ]]; then
    echo "Testing IterativeParallelism!"
    bash testers/test_concurrent.sh
fi
if [[ $1 -eq 8 ]]; then
    echo "Testing ParallelMapperImpl!"
    bash testers/test_mapper.sh
fi
if [[ $1 -eq 9 ]]; then
    echo "Testing WebCrawler!"
    bash testers/test_crawler.sh
fi