#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/walk/RecursiveWalk.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.walk RecursiveWalk ru.ifmo.rain.badyaev.walk.RecursiveWalk
rm ru/ifmo/rain/badyaev/walk/*.class