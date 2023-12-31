package Classes.core;

import Classes.entity.Player;
import Classes.entity.Character;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public final class Utility {

    private static final String CHOOSE_MESSAGE = "Choose an option :";
    public static final String PRESS_ENTER_MESSAGE = Color.RED.getColor() + "Press enter to continue...." + Color.RESET.getColor();

    public enum Color {
        RED("\u001B[31m"),
        RESET("\u001B[0m"),
        GREEN("\u001B[32m"),
        WHITE("\u001B[37m"),
        YELLOW("\u001B[33m"),
        CYAN("\u001B[36m"),
        MAGENTA("\u001B[35m"),
        BLUE("\u001B[34m"),
        BLACK("\u001B[30m");

        private final String color;

        public String getColor() {
            return color;
        }

        Color(String s) {
            this.color = s;
        }
    }

    private Utility() {
    }

    public static void clearTerminal() {
        System.out.flush();
        System.out.print("\033[H\033[2J");
    }

    public static void waitForEnter(Scanner sc) {
        slowPrint(Utility.PRESS_ENTER_MESSAGE, 15);
        sc.nextLine();
    }

    public static void slowPrint(String s, int delay) {
        int textSpeedDelay = (int) (Data.getGameSpeed() * delay);
        for (char c : s.toCharArray()) {
            System.out.print(c);
            try {
                TimeUnit.MILLISECONDS.sleep(textSpeedDelay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static int printOptionsMenu(String[] options, Scanner sc) {
        return optionsMenu(options, sc, false, getStringAsTitle(CHOOSE_MESSAGE) + "\n");
    }

    public static int printOptionsMenu(String[] options, Scanner sc, boolean back) {
        return optionsMenu(options, sc, back, getStringAsTitle(CHOOSE_MESSAGE) + "\n");
    }

    public static int printOptionsMenu(String[] options, Scanner sc, boolean back, String message) {
        return optionsMenu(options, sc, back, message + "\n" + getStringAsTitle(CHOOSE_MESSAGE) + "\n");
    }


    private static int optionsMenu(String[] options, Scanner sc, boolean back, String Message) {
        int choice = 0, n = options.length;
        boolean valid = false;

        System.out.println();

        while (!valid) {
            clearTerminal();
            System.out.println(Message);
            for (int i = 0; i < n; i++)
                System.out.println((i + 1) + ". " + options[i]);

            if (back)
                System.out.println((n + 1) + ". back");

            System.out.printf("\nEnter a valid option (%d to %d) : ", 1, back ? n + 1 : n);
            try {
                choice = sc.nextInt();

                if (back && choice == n + 1) {
                    sc.nextLine();
                    return -1;
                }

                if (choice > 0 && choice <= n)
                    valid = true;

                else {
                    sc.nextLine();
                    System.out.printf(Color.RED.getColor() + "Invalid choice! Please enter a number between 1 and %d!\n" + Color.RESET.getColor(), back ? n + 1 : n);
                    waitForEnter(sc);
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.print(Color.RED.getColor() + "Invalid input! Please enter a number only!\n" + Color.RESET.getColor());
                waitForEnter(sc);

            }
        }
        sc.nextLine();
        return choice;
    }

    public static String getStringAsTitle(String msg) {

        return "=".repeat(msg.length()) +
                "\n" +
                msg +
                "\n" +
                "=".repeat(msg.length());
    }
}