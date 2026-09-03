package lesson01;

public class Task4 {
    public static void main(String[] args) {
        int size = 5;

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size * 2 - 1; column++) {

                if (column == row || column == (size * 2 - 2) - row) {
                    System.out.print("*");
                } else {
                    System.out.print(" ");
                }
            }

            System.out.println();
        }
    }
}
