package lesson02;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Task3 {
    public static void main(String[] args) {
        Set<String> courseA = new HashSet<>(Arrays.asList("Анна", "Борис", "Виктор", "Диана"));
        Set<String> courseB = new HashSet<>(Arrays.asList("Борис", "Диана", "Егор", "Жанна"));

        Set<String> intersection = new HashSet<>(courseA);
        intersection.retainAll(courseB);

        Set<String> union = new HashSet<>(courseA);
        union.addAll(courseB);

        Set<String> onlyCourseA = new HashSet<>(courseA);
        onlyCourseA.removeAll(courseB);

        System.out.println("Курс А: " + courseA);
        System.out.println("Курс Б: " + courseB);
        System.out.println("Оба курса: " + intersection);
        System.out.println("Хотя бы один курс: " + union);
        System.out.println("Только курс А: " + onlyCourseA);
    }
}
