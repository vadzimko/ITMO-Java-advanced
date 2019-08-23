#!/usr/bin/env bash

javac -classpath lib/jsoup-1.8.1.jar:. ru/ifmo/rain/badyaev/crawler/WebCrawler.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.crawler hard ru.ifmo.rain.badyaev.crawler.WebCrawler qz
rm ru/ifmo/rain/badyaev/crawler/*.class