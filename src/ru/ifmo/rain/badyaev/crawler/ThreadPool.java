package ru.ifmo.rain.badyaev.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    private final ExecutorService executorService;

    public ThreadPool(int threadCount) {
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void addTask(Runnable task) {
        synchronized (executorService) {
            executorService.submit(task);
        }
    }

    public void close() {
        executorService.shutdown();
    }
}
