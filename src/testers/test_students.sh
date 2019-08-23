#!/usr/bin/env bash

javac ru/ifmo/rain/badyaev/students/StudentDB.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.student StudentGroupQuery ru.ifmo.rain.badyaev.students.StudentDB
rm ru/ifmo/rain/badyaev/students/*.class