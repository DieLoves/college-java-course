package lesson02;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Task2 {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 5, 3, 8, 5, 1, 8);

        Set<Integer> uniqueNumbersSet = new LinkedHashSet<>();
        uniqueNumbersSet.addAll(numbers);

        List<Integer> uniqueNumbers = new ArrayList<>(uniqueNumbersSet);

        System.out.println("Исходный список: " + numbers);
        System.out.println("Список без дубликатов: " + uniqueNumbers);
    }
}
