package ru.ifmo.rain.badyaev.mapper;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;
    private final Queue<Runnable> tasks;

    public ParallelMapperImpl(int threads) {
        this.threads = new ArrayList<>(threads);
        this.tasks = new LinkedList<>();

        Runnable runnable = () -> {
            try {
                while (!Thread.interrupted()) {
                    Runnable task;
                    synchronized (tasks) {
                        while (tasks.isEmpty()) {
                            tasks.wait();
                        }
                        task = tasks.poll();
                    }
                    if (task != null) {
                        task.run();
                    }
                }
            } catch (InterruptedException ignored) {
            }
        };

        while (threads-- > 0) {
            this.threads.add(new Thread(runnable));
        }
        apply(Thread::start);
    }

    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        int elementsSize = args.size();
        List<R> result = new ArrayList<>(Collections.nCopies(elementsSize, null));

        Counter counter = new Counter(elementsSize);
        for (int index = 0; index < elementsSize; index++) {
            final int elementIndex = index;
            Runnable runnable = () -> {
                result.set(elementIndex, f.apply(args.get(elementIndex)));
                synchronized (counter) {
                    counter.inc();
                }
            };
            synchronized (tasks) {
                tasks.add(runnable);
                tasks.notify();
            }
        }

        synchronized (counter) {
            while (!counter.isReady()) {
                counter.wait();
            }
        }

        return result;
    }

    @Override
    public void close() {
        apply(Thread::interrupt);
        apply(t -> {
            try {
                t.join();
            } catch (InterruptedException ignored) {
            }
        });
    }

    private void apply(Consumer<? super Thread> action) {
        threads.forEach(action);
    }

    private final class Counter {
        private int done;
        private int tasksNumber;
        private boolean isReady = false;

        Counter(int tasksNumber) {
            this.done = 0;
            this.tasksNumber = tasksNumber;
        }

        void inc() {
            if (++done == tasksNumber) {
                isReady = true;
                this.notifyAll();
            }
        }

        private boolean isReady() {
            return isReady;
        }
    }
}