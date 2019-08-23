package ru.ifmo.rain.badyaev.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final List<E> data;
    private NavigableSet<E> descendingArraySet = null;
    private final Comparator<? super E> comparator;
    private final String UNSUPPORTED_OPERATION_MESSAGE = "Not applicable for immutable object";

    public ArraySet() {
        data = Collections.emptyList();
        comparator = null;
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        TreeSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);

        data = new ArrayList<>(set);
        this.comparator = comparator;
    }

    private ArraySet(List<E> descendingCollection, Comparator<? super E> descendingComparator, NavigableSet<E> descendingSet) {
        this.data = descendingCollection;
        this.comparator = descendingComparator;
        this.descendingArraySet = descendingSet;
    }

    private ArraySet(List<E> data, Comparator<? super E> comparator) {
        this.data = data;
        this.comparator = comparator;
    }

    private int findElementIndex(E e, int ifEqual, int ifNotEqual) {
        int index = Collections.binarySearch(data, e, comparator);
        return index >= 0 ? index + ifEqual : -(index + 1) + ifNotEqual;
    }

    private E getByIndex(int index) {
        return index >= 0 && index < size() ? data.get(index) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }

    @Override
    public E lower(E e) {
        return getByIndex(findElementIndex(e, -1, -1));
    }

    @Override
    public E floor(E e) {
        return getByIndex(findElementIndex(e, 0, -1));
    }

    @Override
    public E ceiling(E e) {
        return getByIndex(findElementIndex(e, 0, 0));
    }

    @Override
    public E higher(E e) {
        return getByIndex(findElementIndex(e, 1, 0));
    }

    @Override
    public E pollFirst() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(data).iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        if (descendingArraySet == null) {
            List<E> dataCopy = new ArrayList<>(data);
            Collections.reverse(dataCopy);

            descendingArraySet = new ArraySet<>(
                    dataCopy,
                    Collections.reverseOrder(comparator),
                    this
            );
        }

        return descendingArraySet;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        int fromIndex = findElementIndex(fromElement, fromInclusive ? 0 : 1, 0);
        int toIndex = findElementIndex(toElement, toInclusive ? 0 : -1, -1);

        if (fromIndex >= size() || toIndex < 0 || fromIndex > toIndex) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        return new ArraySet<>(data.subList(fromIndex, toIndex + 1), comparator);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return subSet(getByIndex(0), true, toElement, inclusive);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return subSet(fromElement, inclusive, getByIndex(size() - 1), true);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArraySet is empty and cannot return first element");
        }

        return getByIndex(0);
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArraySet is empty and cannot return last element");
        }

        return getByIndex(size() - 1);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}