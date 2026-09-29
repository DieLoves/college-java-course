package lesson02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Task8 {
    public static void main(String[] args) {
        int[] numbers = new int[15];
        Random random = new Random();

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(100) + 1;
        }

        List<Integer> numberList = new ArrayList<>();

        for (int number : numbers) {
            numberList.add(number);
        }

        Collections.sort(numberList);

        int numberIndex = Collections.binarySearch(numberList, 42);
        int sum = 0;

        for (int number : numberList) {
            sum += number;
        }

        System.out.println("Отсортированный список: " + numberList);

        if (numberIndex >= 0) {
            System.out.println("Число 42 находится по индексу: " + numberIndex);
        } else {
            System.out.println("Числа 42 нет в списке");
        }

        System.out.println("Минимум: " + Collections.min(numberList));
        System.out.println("Максимум: " + Collections.max(numberList));
        System.out.println("Сумма: " + sum);
    }
}
