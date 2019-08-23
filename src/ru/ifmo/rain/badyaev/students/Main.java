package ru.ifmo.rain.badyaev.students;

import info.kgeorgiy.java.advanced.student.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<>();
        list.add(new Student(2, "Egor", "Nachalnik", "M3238"));
        list.add(new Student(0, "Vadik", "Marmeladik", "M3237"));
        list.add(new Student(3, "Kolik", "Sokolik", "M3234"));
        list.add(new Student(4, "Moks", "Poks", "XZ"));

        StudentDB studentDB = new StudentDB();
        System.out.println(studentDB.getFirstNames(list));
        System.out.println(studentDB.getLastNames(list));
        System.out.println(studentDB.getGroups(list));
        System.out.println(studentDB.getFullNames(list));
        System.out.println(studentDB.getDistinctFirstNames(list));
        System.out.println(studentDB.getMinStudentFirstName(list));
        System.out.println(studentDB.sortStudentsById(list));
        System.out.println(studentDB.sortStudentsByName(list));

        list.add(new Student(7, "Vadik", "Loading", "M3237"));
        list.add(new Student(6, "Lobzik", "Olip", "M3237"));
        System.out.println(studentDB.findStudentsByFirstName(list, "Vadik"));
        System.out.println(studentDB.findStudentNamesByGroup(list, "M3237"));

        System.out.println(studentDB.getGroupsByName(list));
        System.out.println(studentDB.getGroupsById(list));
        System.out.println(studentDB.getLargestGroup(list));
        System.out.println(studentDB.getLargestGroupFirstName(list));

    }
}
