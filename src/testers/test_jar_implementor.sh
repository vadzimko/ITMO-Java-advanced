#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/implementor/JarImplementor.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.implementor jar-class ru.ifmo.rain.badyaev.implementor.JarImplementor
rm ru/ifmo/rain/badyaev/implementor/*.class