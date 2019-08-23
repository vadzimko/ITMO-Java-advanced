package ru.ifmo.rain.badyaev.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IterativeParallelism implements ListIP {

    private ParallelMapper parallelMapper;

    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    @Override
    public <T> T maximum(int threads, List<? extends T> elements, Comparator<? super T> comparator) throws InterruptedException {
        Function<Stream<? extends T>, T> func = s -> s.max(comparator).orElse(null);
        return process(threads, elements, func, func);
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> elements, Comparator<? super T> comparator) throws InterruptedException {
        Function<Stream<? extends T>, T> func = s -> s.min(comparator).orElse(null);
        return process(threads, elements, func, func);
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements, s -> s.allMatch(predicate), s -> s.allMatch(value -> value));
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements, s -> s.anyMatch(predicate), s -> s.anyMatch(value -> value));
    }

    @Override
    public String join(int threads, List<?> elements) throws InterruptedException {
        return process(threads, elements,
                s -> s.map(Object::toString).collect(Collectors.joining()),
                s -> s.collect(Collectors.joining())
        );
    }

    @Override
    public <T>
    List<T> filter(int threads, List<? extends T> elements, Predicate<? super T> predicate) throws InterruptedException {
        return process(threads, elements,
                s -> s.filter(predicate).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList())
        );
    }

    @Override
    public <T, U>
    List<U> map(int threads, List<? extends T> elements, Function<? super T, ? extends U> mapper) throws InterruptedException {
        return process(threads, elements,
                s -> s.map(mapper).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList())
        );
    }

    public <T, R> R process(int threads, final List<? extends T> elements,
                            final Function<Stream<? extends T>, R> mapper,
                            final Function<? super Stream<R>, R> collector) throws InterruptedException {

        if (threads < 1) {
            throw new IllegalArgumentException("Threads number must be positive");
        }
        if (parallelMapper != null) {
            return collector.apply(parallelMapper.map(mapper, split(threads, elements)).stream());
        }

        List<Stream<? extends T>> groups = split(threads, elements);
        List<Worker<T, R>> workers = new ArrayList<>();
        for (Stream<? extends T> group : groups) {
            workers.add(new Worker<>(mapper, group));
        }

        List<R> result = new ArrayList<>();
        for (Worker<T, R> worker : workers) {
            result.add(worker.getResult());
        }

        return collector.apply(result.stream());
    }

    public static <T> List<Stream<? extends T>> split(final int parts, final List<? extends T> elements) {
        int elementsSize = elements.size();
        int batchSize = elementsSize / parts;
        List<Stream<? extends T>> groupedElements = new ArrayList<>();

        int index = 0;
        int tailSize = elementsSize - batchSize * parts;
        while (index < elementsSize) {
            int tempBatchSize = batchSize;
            if (tailSize > 0) {
                tempBatchSize++;
                tailSize--;
            }

            int lastElementIndex = Math.min(elementsSize, index + tempBatchSize);
            if (index != lastElementIndex) {
                groupedElements.add(elements.subList(index, lastElementIndex).stream());
            }
            index += tempBatchSize;
        }

        return groupedElements;
    }
}