#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/mapper/ParallelMapperImpl.java
javac ru/ifmo/rain/badyaev/concurrent/IterativeParallelism.java

java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.mapper list ru.ifmo.rain.badyaev.mapper.ParallelMapperImpl,ru.ifmo.rain.badyaev.concurrent.IterativeParallelism

rm ru/ifmo/rain/badyaev/mapper/*.class
rm ru/ifmo/rain/badyaev/concurrent/*.class