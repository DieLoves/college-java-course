package lesson02;

import java.util.ArrayList;

public class Task1 {
    public static void main(String[] args) {
        ArrayList<String> cities = new ArrayList<>();

        cities.add("Караганда");
        cities.add("Астана");
        cities.add("Алматы");
        cities.add("Шымкент");
        cities.add("Павлодар");
        cities.add("Костанай");

        System.out.println("Список до изменений:");
        printList(cities);

        cities.remove(2);
        cities.add("Актобе");
        cities.add("Тараз");

        System.out.println("Список после изменений:");
        printList(cities);
    }

    private static void printList(ArrayList<String> cities) {
        for (int i = 0; i < cities.size(); i++) {
            System.out.println(cities.get(i));
        }
    }
}
