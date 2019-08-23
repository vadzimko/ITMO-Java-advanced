#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/arrayset/ArraySet.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.arrayset NavigableSet ru.ifmo.rain.badyaev.arrayset.ArraySet
rm ru/ifmo/rain/badyaev/arrayset/*.class