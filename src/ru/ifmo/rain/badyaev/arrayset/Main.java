package ru.ifmo.rain.badyaev.arrayset;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Integer[] a = {328810891, 933586480, 1781530601};
        List<Integer> list = new ArrayList<>(Arrays.asList(a));
//        list.add(1);
//        list.add(3);
//        list.add(5);
        ArraySet<Integer> set = new ArraySet<>(list);
//        System.out.println(set.lower(2));
//        System.out.println(set.toString());
//        System.out.println(set.subSet(933586480, 328810891));
//        System.out.println(set.toString());
        NavigableSet<Integer> descendingSet = set.descendingSet();
        System.out.println(descendingSet.descendingSet() == set);
    }

}
