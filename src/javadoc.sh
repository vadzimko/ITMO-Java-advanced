#!/usr/bin/env bash

georgiy=info/kgeorgiy/java/advanced/implementor/

javadoc -cp artifacts/:lib/ -d ../javadoc -private ru/ifmo/rain/badyaev/implementor/*.java ${georgiy}ImplerException.java ${georgiy}Impler.java ${georgiy}JarImpler.java