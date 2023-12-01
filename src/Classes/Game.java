package Classes;

import Classes.Entity.Character;
import Classes.Entity.Enemy;
import Classes.Entity.Item;
import Classes.Entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.max;

public class Game {
    private static final String WELCOME_MESSAGE = "Greetings, adventurer! The ROGUE REALMS welcomes you!";
    private static final String CHOOSE_MESSAGE = "Choose an option :";

    private final HashMap<String, Move> movesTable;
    private final ArrayList<Item> items;


    enum Color {
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

    private void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printTitle(String msg) {
        for (int i = 0; i < msg.length(); i++)
            System.out.print("=");

        System.out.println("\n" + msg);

        for (int i = 0; i < msg.length(); i++)
            System.out.print("=");

        System.out.println();
    }

    public Game() {
        movesTable = new HashMap<>();
        items = new ArrayList<>();
    }

    public void initialize(Scanner sc) {
        int choice;

        String[] options = new String[]{"Start game", "Exit"};

        while (true) {
            System.out.flush();
            printTitle(WELCOME_MESSAGE);

            choice = optionsMenu(options, sc);

            switch (choice) {
                case 1 -> startGame(sc);
                case 2 -> {
                    System.out.print(Color.MAGENTA.getColor() + "As the games fades to black, your legacy will continue to live in the Rogue Realms, farewell brave adventurer!");
                    System.exit(0);
                }
            }
        }
    }

    private void initMovesTable() {
        this.movesTable.put("Punch", new Move("Punch", 5, 0, 0, 90));
        this.movesTable.put("Kick", new Move("Kick", 7, 0, 1, 75));
        this.movesTable.put("Slap", new Move("Slap", 3, 0, 2, 100));
        this.movesTable.put("Scratch", new Move("Scratch", 3, 0, 0, 95));
        this.movesTable.put("Bite", new Move("Bite", 2, 0, 3, 65));
    }

    private void initItemsTable() {

    }

    private Player initPlayer() {
        ArrayList<Move> moves = new ArrayList<>();

        moves.add(this.movesTable.get("Punch"));
        moves.add(this.movesTable.get("Kick"));
        moves.add(this.movesTable.get("Slap"));

        return new Player("jack", 80, 20, 10, 10, 20, moves);
    }

    private Enemy initEasyEnemy(int playerLvl) {
        ArrayList<Move> moves = new ArrayList<>();
        double lvlMultiplier = playerLvl * 0.15 + 1;

        moves.add(this.movesTable.get("Bite"));
        moves.add(this.movesTable.get("Scratch"));

        return new Enemy("wil", (int) (25 * lvlMultiplier), (int) (10 * lvlMultiplier),
                (int) (5 * lvlMultiplier), (int) (5 * lvlMultiplier), (int) (5 * lvlMultiplier), moves, (int) (5 * lvlMultiplier));

    }

    private Enemy initHardEnemy(int playerLvl) {
        ArrayList<Move> moves = new ArrayList<>();
        double lvlMultiplier = playerLvl * 0.15 + 1;

        moves.add(this.movesTable.get("Punch"));
        moves.add(this.movesTable.get("Kick"));
        moves.add(this.movesTable.get("Slap"));
        moves.add(this.movesTable.get("Scratch"));

        return new Enemy("wil2", (int) (50 * lvlMultiplier), (int) (10 * lvlMultiplier), (int) (10 * lvlMultiplier),
                (int) (10 * lvlMultiplier), (int) (8 * lvlMultiplier), moves, (int) (15 * lvlMultiplier));
    }

    private int optionsMenu(String[] options, Scanner sc) {
        int choice = 0, n = options.length;
        boolean valid = false;

        System.out.println();

        for (int i = 0; i < n; i++)
            System.out.println((i + 1) + ". " + options[i]);

        while (!valid) {
            System.out.printf("\nEnter a valid option (%d to %d) : ", 1, n);
            try {
                choice = sc.nextInt();

                if (choice > 0 && choice <= n)
                    valid = true;

                else {
                    System.err.printf("Invalid choice! Please enter a number between 1 and %d!\n", n);
                }
            } catch (Exception e) {
                System.err.println("Invalid input! Please enter a number only!");
                System.out.println(" ");
                sc.next();
            }
        }
        return choice;
    }

    private void battle(Player player, Enemy enemy, Scanner inputScanner, Random random) {

        clearTerminal();
        int turnCounter = 1, battleCounter = 1, choice = 0;
        String[] playerActions = new String[]{"Fight", "Observe", "Surrender"};

        printTitle(player.getName() + " vs " + enemy.getName());

        while (true) {
            System.out.println("\nIt is now turn " + turnCounter);

            if (turnCounter % 2 != 0) {
                System.out.println("It is your turn!");
                System.out.println("Choose action : ");
                choice = optionsMenu(playerActions, inputScanner);

                switch (choice) {
                    case 1 -> {
                        String[] moveNames = new String[player.moves.size()];
                        for (int i = 0; i < player.moves.size(); i++)
                            moveNames[i] = player.moves.get(i).getName();

                        System.out.println("Moves list : ");
                        choice = optionsMenu(moveNames, inputScanner) - 1;
                        System.out.printf("%s chose to use %s\n", player.getName(), player.moves.get(choice).getName());
                        player.use(enemy, player.moves.get(choice));
                    }
                    case 2 -> {
                        String[] options = new String[]{"Current stats", enemy.getName() + "'s stats"};
                        choice = optionsMenu(options, inputScanner);
                        switch (choice) {
                            case 1 -> {
                            }
                            case 2 -> {
                            }
                        }
                        System.out.println("SOON");
                    }
                }
            } else {
                System.out.println("It is " + enemy.getName() + "'s turn!");
                choice = random.nextInt(enemy.moves.size());
                System.out.printf("%s chose to use %s\n", enemy.getName(), enemy.moves.get(choice).getName());
                enemy.use(player, enemy.moves.get(choice));

            }
            int result = checkIfBattleStatus(player, enemy);

            if (result == 1) {
                int xpGained = enemy.getXpValue();

                System.out.printf("Battle is over! %s won!\n", player.getName());
                player.updateTotalXP(enemy.getXpValue());
                System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP(), player.getLvl() + 1);
                break;
            } else if (result == 2) {
                int xpGained = max(enemy.getXpValue() / 2, 1);
                System.out.printf("Battle is over! %s lost!\n", player.getName());
                player.updateTotalXP(xpGained);
                System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP(), player.getLvl() + 1);
                player.restore();
                break;
            }

            System.out.printf("%s current hp is : %d\n", player.getName(), player.getHp());
            System.out.printf("%s current hp is : %d\n", enemy.getName(), enemy.getHp());

            turnCounter++;

        }
    }

    private int checkIfBattleStatus(Character player, Character enemy) {
        if (player.getHp() == 0)
            return 2;
        else if (enemy.getHp() == 0)
            return 1;

        return 0;
    }

    private void shop(Scanner in, Player player) {
        boolean isInt=false;

        Item healingElixir = new Item("Healing Elixir", "Restores a moderate amount of health", 50);
        Item vitalityDraught = new Item("Vitality Draught", "Restores a small amount of health", 30);
        Item celestialTonic = new Item("Celestial Tonic", "Heals a substantial amount of health", 100);
        System.out.println("First Item : Healing Elixir \n Item Description : Restores a moderate amount of health \n Price : 50$ \n ********************************");
        System.out.println("Second Item : Vitality Draught \n Item Description : Restores a small amount of health \n Price : 30$ \n ********************************");
        System.out.println("Third Item : Celestial Tonic \n Item Description : Heals a substantial amount of health \n Price : 100$ \n ********************************");
        System.out.println("Choose the Item you need : ");

        int x = in.nextInt();
        if(!in.hasNextInt()) System.err.println("Invalid!!");
        switch (x) {
            case 1 -> {
                if (player.getCurrency() >= 50) {
                    player.addItem(healingElixir);
                    player.setCurrency(player.getCurrency() - 50);
                    System.out.println(Color.GREEN.getColor() + "Successfully purchased a Healing Elixir.");
                } else {
                    System.err.println("Sorry! You can't buy a Healing Elixir due to insufficient funds!");
                }
            }
            case 2 -> {
                if (player.getCurrency() >= 30) {
                    player.addItem(vitalityDraught);
                    player.setCurrency(player.getCurrency() - 30);
                    System.out.println(Color.GREEN.getColor() + "Successfully purchased a Vitality Draught.");
                } else {
                    System.err.println("Sorry! You can't buy a Vitality Draught due to insufficient funds!");
                }
            }
            case 3 -> {
                if (player.getCurrency() >= 100) {
                    player.addItem(celestialTonic);
                    player.setCurrency(player.getCurrency() - 100);
                    System.out.println(Color.GREEN.getColor() + "Successfully purchased a Celestial Tonic.");
                } else {
                    System.err.println("Sorry! You can't buy a Celestial Tonic due to insufficient funds!");
                }
            }
            default ->
                    System.err.printf("Sorry! No such item exists with number (%d) , please choose an item between (1-3)\n", x);
        }
    }

    private void printStats(Player player) {

        clearTerminal();
        System.out.println("\n\nPlayer name : " + player.getName());
        System.out.println("Player hp : " + player.getHp() + "/" + player.getMaxHP());
        System.out.println("Player mp : " + player.getMp() + "/" + player.getMaxMP());
        System.out.println("Player str : " + player.getStr());
        System.out.println("Player def : " + player.getDef());
        System.out.println("Player speed : " + player.getSpeed());
        System.out.println("Player level : " + player.getLvl());
        System.out.println("Player total xp : " + player.getTotalXP());
        System.out.println("Player xp till next level : " + player.getXPTillLvl());
    }

    private void startGame(Scanner sc) {
        int choice;
        String[] Options = new String[]{"Battle (easy)", "Battle (hard)", "Shop", "Current stats", "Main menu"};
        Random random = new Random(System.currentTimeMillis());

        initMovesTable();
        Player player = initPlayer();

        while (true) {
            clearTerminal();

            printTitle(CHOOSE_MESSAGE);

            choice = optionsMenu(Options, sc);

            switch (choice) {
                case 1 -> battle(player, this.initEasyEnemy(player.getLvl()), sc, random);
                case 2 -> battle(player, this.initHardEnemy(player.getLvl()), sc, random);
                case 3 -> shop(sc, player);
                case 4 -> printStats(player);
                case 5 -> {
                    return;
                }
            }

        }
    }
}
