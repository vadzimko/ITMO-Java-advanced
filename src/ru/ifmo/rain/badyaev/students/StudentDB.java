package ru.ifmo.rain.badyaev.students;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentGroupQuery {

    private Stream<Entry<String, List<Student>>> getGroupsStream(Collection<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(Student::getGroup, TreeMap::new, Collectors.toList()))
                .entrySet()
                .stream();
    }

    private List<Group> getSortedGroups(Collection<Student> students, UnaryOperator<List<Student>> sortFunction) {
        return getGroupsStream(students)
                .map(group -> new Group(group.getKey(), sortFunction.apply(group.getValue())))
                .collect(Collectors.toList());
    }

    private String getLargestGroup(Collection<Student> students, Function<List<Student>, Integer> measurer) {
        return getGroupsStream(students)
                .max(Comparator
                        .comparing((Entry<String, List<Student>> group) -> measurer.apply(group.getValue()))
                        .thenComparing(Entry::getKey, Collections.reverseOrder())
                )
                .map(Entry::getKey)
                .orElse("");
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return getSortedGroups(students, this::sortStudentsByName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return getSortedGroups(students, this::sortStudentsById);
    }

    @Override
    public String getLargestGroup(Collection<Student> students) {
        return getLargestGroup(students, List::size);
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return getLargestGroup(students, list -> getDistinctFirstNames(list).size());
    }

    private static final Comparator<Student> STUDENT_COMPARATOR = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId);

    private List<Student> toList(Stream<Student> stream) {
        return stream.collect(Collectors.toList());
    }

    private List<Student> sortedList(Collection<Student> stream, Comparator<Student> comparator) {
        return toList(sort(stream, comparator));
    }

    private List<String> mappedList(List<Student> students, Function<Student, String> mapFunction) {
        return map(students, mapFunction).collect(Collectors.toList());
    }

    private Stream<String> map(List<Student> students, Function<Student, String> mapFunction) {
        return students.stream().map(mapFunction);
    }

    private Stream<Student> sort(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return mappedList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return mappedList(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return mappedList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mappedList(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return map(students, Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream()
                .min(Comparator.comparing(Student::getId))
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortedList(students, Student::compareTo);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortedList(students, STUDENT_COMPARATOR);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return find(students, Student::getFirstName, name);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return find(students, Student::getLastName, name);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return find(students, Student::getGroup, group);
    }

    private List<Student> find(Collection<Student> students,
                               Function<Student, String> infoGetter,
                               String info) {

        return find(students, infoGetter, info, Collectors.toList());
    }

    private <T> T find(Collection<Student> students,
                       Function<Student, String> infoGetter,
                       String info,
                       Collector<Student, ?, T> collector) {

        return students.stream()
                .filter(student -> infoGetter.apply(student).equals(info))
                .sorted(STUDENT_COMPARATOR).collect(collector);
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return find(students,
                Student::getGroup,
                group,
                Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo))
        );
    }
}