package lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task6 {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("Анна", "Караганда"),
                new Student("Борис", "Астана"),
                new Student("Виктор", "Караганда"),
                new Student("Диана", "Алматы"),
                new Student("Егор", "Астана")
        );

        Map<String, List<Student>> studentsByCity = new HashMap<>();

        for (Student student : students) {
            studentsByCity
                    .computeIfAbsent(student.getCity(), city -> new ArrayList<>())
                    .add(student);
        }

        for (Map.Entry<String, List<Student>> entry : studentsByCity.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static class Student {
        private final String name;
        private final String city;

        public Student(String name, String city) {
            this.name = name;
            this.city = city;
        }

        public String getCity() {
            return city;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
