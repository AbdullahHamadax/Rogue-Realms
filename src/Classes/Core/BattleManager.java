package Classes.Core;

import Classes.Entity.Character;
import Classes.Entity.Enemy;
import Classes.Entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static Classes.Core.Utility.*;
import static java.lang.Math.max;

public class BattleManager {
    String[] playerActions = new String[]{"Fight", "Observe", "Surrender", "Use item"};

    public static String constructString(String s, int width) {
        StringBuilder output = new StringBuilder(" ".repeat(width - 2));
        int start = width / 2 - s.length() / 2;
        for (int i = start, j = 0; j < s.length(); i++, j++)
            output.setCharAt(i, s.charAt(j));

        return output.toString();
    }

    private int checkIfBattleStatus(Character player, Character[] enemies) {
        if (player.getHp() == 0)
            return 1;

        return Arrays.stream(enemies).anyMatch((enemy) -> enemy.getHp() == 0) ? 2 : 0;

    }

    private String printCurrentBattleStats(Player player, Enemy[] enemies, int turn) {

        Optional<Integer> longestName = Arrays.stream(enemies)
                .map(Character::getName)
                .map(String::length)
                .max(Integer::compareTo);

        int width = (int) (max(player.getName().length(), longestName.get()) * 1.5 + 5);
        StringBuilder message = new StringBuilder();

        String[] playerSection = new String[3];
        String[][] enemySection = new String[enemies.length][3];

        message.append("*".repeat(12)).append("\n");
        message.append("Turn : ").append(turn).append("\n");
        message.append("*".repeat(width * enemies.length + 1)).append("\n");

        playerSection[0] = constructString(String.format("%s", player.getName()), width);
        playerSection[1] = constructString(String.format("HP: %d/%d", player.getHp(), player.getMaxHP()), width);
        playerSection[2] = constructString(String.format("SP: %d/%d", player.getMp(), player.getMaxMP()), width);

        for (int i = 0; i < enemies.length; i++) {
            enemySection[i][0] = constructString(String.format("%s", enemies[i].getName()), width);
            enemySection[i][1] = constructString(String.format("HP: %d/%d", enemies[i].getHp(), enemies[i].getMaxHP()), width);
            enemySection[i][2] = constructString(String.format("SP: %d/%d", enemies[i].getMp(), enemies[i].getMaxMP()), width);
        }


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < enemies.length; j++) {
                if (j == enemies.length - 1)
                    message.append(enemySection[j][i]);
                else
                    message.append(enemySection[j][i]).append("||");
            }
            message.append("\n");
        }
        message.append("*".repeat(width * enemies.length + 1));
        message.append("\n");


        for (String s : playerSection)
            message.append(s).append("\n");

        message.append("*".repeat(width + 1));

        return message.toString();
    }

    private void printCharacterStats(Character character, Scanner sc) {
        clearTerminal();
        System.out.println(character.getName());
        // will continue it later too lazy (someone else do it for me)
        sc.nextLine();
        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();
    }

    private void playerTurn(Player player, Scanner sc, Enemy[] enemies, String currentStats) {
        boolean notDone = true;
        int choice;

        while (notDone) {

            choice = optionsMenu(playerActions, sc, false, currentStats);

            boolean event = false;
            switch (choice) {
                case 1 -> event = playerFightAction(player, enemies, sc);
                case 2 -> event = playerObserveAction(player, enemies, sc);
                case 3 -> event = playerSurrenderAction(player, sc);
                case 4 -> System.out.println("Coming soon!!");

            }
            if (event)
                notDone = false;
        }
    }

    //return true to determine end of turn, false in case of back
    private boolean playerFightAction(Player player, Enemy[] enemies, Scanner sc) {

        int choice, target = 0;
        String[] moveNames = player.moves.stream().map(Move::getName).toArray(String[]::new);

        choice = optionsMenu(moveNames, sc, true);
        if (choice == -1)
            return false;

        choice--;

        if (enemies.length > 1)
            target = selectTarget(enemies, sc);

        if (target == -1)
            return false;

        System.out.printf("%s chose to use %s on %s\n", player.getName(), player.moves.get(choice).getName(), enemies[target].getName());
        int value = player.use(enemies[target], player.moves.get(choice));

        if (value == -1)
            System.out.println("It was a total miss!");

        else
            System.out.printf("It dealt %d points of damage\n", value);
        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();
        return true;
    }

    private boolean playerObserveAction(Player player, Enemy[] enemies, Scanner sc) {
        int choice;

        String[] options = new String[1 + enemies.length];
        options[0] = "Current stats";
        for (int i = 1; i < options.length; i++)
            options[i] = enemies[i - 1].getName() + "'s stats";

        choice = optionsMenu(options, sc, true);
        if (choice == -1)
            return false;

        if (choice == 1)
            printCharacterStats(player, sc);

        else
            printCharacterStats(enemies[choice - 2], sc);

        return false;
    }

    private boolean playerSurrenderAction(Player player, Scanner sc) {
        player.setHp(-1);
        System.out.println(Color.MAGENTA.getColor() + "The battle-worn adventurer " + player.getName() + " raises their hands in surrender.");

        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();

        return true;
    }

    private boolean playerUseItemAction(Player player, Scanner sc) {
        return true;
    }


    private int selectTarget(Enemy[] enemies, Scanner sc) {
        String message = "Select ENEMY TO ATTACK!";
        String[] enemyNames = Arrays.stream(enemies).map(Enemy::getName).toArray(String[]::new);


        int choice = optionsMenu(enemyNames, sc, true, message);
        if (choice == -1)
            return -1;

        return choice - 1;

    }

    private void enemyTurn(Enemy enemy, Scanner sc, Player player, Random random, String battleMessage) {
        int choice;

        System.out.print(battleMessage);
        System.out.println();
        System.out.println("It is " + enemy.getName() + "'s turn!");
        choice = random.nextInt(enemy.moves.size());
        System.out.printf("%s chose to use %s\n", enemy.getName(), enemy.moves.get(choice).getName());
        int value = enemy.use(player, enemy.moves.get(choice));
        if (value == -1)
            System.out.println("it was a total miss");

        else
            System.out.printf("it dealt %d points of damage\n", value);

        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();
    }

    public int initiateBattle(Player player, Enemy[] enemies, Scanner sc, Random random) {
        return createBattle(player, enemies, sc, random);
    }

    public int initiateBattle(Player player, Enemy enemy, Scanner sc, Random random) {
        return createBattle(player, new Enemy[]{enemy}, sc, random);
    }

    // fix formatting (still happens in idea but works fine in terminal, will fix tho)
    private int createBattle(Player player, Enemy[] enemies, Scanner sc, Random random) {

        clearTerminal();

        boolean battleOngoing = true;
        Character[] battleChars = new Character[1 + enemies.length];

        Set<Character> defeatedEnemies = new HashSet<>();

        int totalBattleXp = Arrays.stream(enemies).map(Enemy::getXpValue).reduce(0, Integer::sum);

        battleChars[0] = player;
        System.arraycopy(enemies, 0, battleChars, 1, battleChars.length - 1);

        int turnCounter = 1;

        String battleTitle = String.format("%s%s%s vs %s%s%s",
                Color.BLUE.getColor(), player.getName(), Color.RESET.getColor(), Color.RED.getColor(), enemies[0].getName(), Color.RESET.getColor());

        if (enemies.length > 1) {
            battleTitle = String.format("%s%s%s vs %s%d%s Enemies",
                    Color.BLUE.getColor(), player.getName(), Color.RESET.getColor(), Color.RED.getColor(), enemies.length, Color.RESET.getColor());
        }


        printTitle(battleTitle);

        System.out.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();

        for (Character chara : battleChars)
            chara.enterBattleState();


        while (battleOngoing) {

            Arrays.sort(battleChars, Comparator.comparingInt(Character::getBattleSpeed).reversed());
            for (Character entity : battleChars) {

                if (defeatedEnemies.contains(entity))
                    continue;

                clearTerminal();
                String currentStatsMessage = printCurrentBattleStats(player, enemies, turnCounter);
                if (entity == player) {
                    playerTurn(player, sc, enemies, currentStatsMessage);
                    sc.nextLine();
                } else
                    enemyTurn((Enemy) entity, sc, player, random, currentStatsMessage);

                int result = checkIfBattleStatus(player, enemies);

                if (result == 1) {
                    clearTerminal();
                    battleLostEvent(player, totalBattleXp, sc);
                    battleOngoing = false;
                    return 0;
                } else if (result == 2) {
                    clearTerminal();
                    defeatedEnemies.addAll(getDefeatedEnemies(enemies, sc));

                    enemies = Arrays.stream(enemies).filter(enemy -> !defeatedEnemies.contains(enemy)).toArray(Enemy[]::new);
                    battleChars = Arrays.stream(battleChars).filter(chara -> !defeatedEnemies.contains(chara)).toArray(Character[]::new);

                    if (enemies.length == 0) {
                        battleWonEvent(player, totalBattleXp, sc);
                        battleOngoing = false;
                        return 1;
                    }
                }
            }
            turnCounter++;

        }

        return 2;

    }

    private Set<Character> getDefeatedEnemies(Enemy[] enemies, Scanner sc) {

        Set<Character> defeatedEnemies = Arrays.stream(enemies).filter((enemy -> enemy.getHp() == 0)).collect(Collectors.toSet());

        for (Character enemy : defeatedEnemies) {
            System.out.printf("%s was defeated\n", enemy.getName());
            System.out.print(PRESS_ENTER_MESSAGE);
            sc.nextLine();
        }


        return defeatedEnemies;
    }

    private void battleWonEvent(Player player, int xpGained, Scanner sc) {

        System.out.printf("Battle is over! %s won!\n", player.getName());
        player.updateTotalXP(xpGained);
        System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP(), player.getLvl() + 1);

        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();

    }

    private void battleLostEvent(Player player, int xpGained, Scanner sc) {

        xpGained = max(xpGained / 2, 1);
        System.out.printf("Battle is over! %s lost!\n", player.getName());
        player.updateTotalXP(xpGained);
        System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP(), player.getLvl() + 1);

        player.restore();

        System.err.print(PRESS_ENTER_MESSAGE);
        sc.nextLine();

    }
}