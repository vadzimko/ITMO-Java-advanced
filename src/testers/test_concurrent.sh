#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/concurrent/IterativeParallelism.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.concurrent list ru.ifmo.rain.badyaev.concurrent.IterativeParallelism 9V
rm ru/ifmo/rain/badyaev/concurrent/*.class