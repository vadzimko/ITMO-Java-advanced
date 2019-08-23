package ru.ifmo.rain.badyaev.concurrent;

import java.util.function.Function;
import java.util.stream.Stream;

public class Worker<T, R> {
    private R result;
    private final Thread thread;

    Worker(final Function<Stream<? extends T>, R> map, Stream<? extends T> elements) {
        this.thread = new Thread(() -> result = map.apply(elements));
        this.thread.start();
    }

    public R getResult() throws InterruptedException {
        thread.join();

        return result;
    }
}