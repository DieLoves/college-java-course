package lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Task7 {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>(Arrays.asList(
                new Student("Виктор", 20),
                new Student("Анна", 18),
                new Student("Борис", 20),
                new Student("Диана", 19),
                new Student("Егор", 18)
        ));

        students.sort(Comparator.comparingInt(Student::getAge)
                .thenComparing(Student::getName));

        for (Student student : students) {
            System.out.println(student);
        }
    }

    private static class Student {
        private final String name;
        private final int age;

        public Student(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return String.format("%s | %d", name, age);
        }
    }
}
