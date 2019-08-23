#!/usr/bin/env bash

path=ru/ifmo/rain/badyaev/implementor

echo "Compiling"
javac ${path}/JarImplementor.java

echo "Packing"
jar cfe jar_implementor.jar ru.ifmo.rain.badyaev.implementor.JarImplementor ${path} info/kgeorgiy/java/advanced/implementor

echo "Done!"