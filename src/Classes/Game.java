package Classes;

import Classes.Entity.Character;
import Classes.Entity.Enemy;
import Classes.Entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.max;

public class Game {
    private final HashMap<String, Move> movesTable;

    public Game(){
        movesTable = new HashMap<>();
    }
    public void initilize(Scanner sc) {
        int choice = 0;
        boolean validChoice = false;


        while(true){
            do {
                System.out.flush();
                System.out.print("Welcome to the Rogue Realms!");
                System.out.print("\n\n");

                System.out.println("1. Start game");
                System.out.println("2. Exit");
                System.out.print("\n\n");
                try {

                    System.out.print("Choice : ");
                    choice = sc.nextInt();

                    switch (choice) {
                        case 1:
                            startGame(sc);
                            validChoice = true;
                            break;
                        case 2:
                            System.exit(0);
                            validChoice = true;
                            break;
                        default:
                            System.out.println("Invalid choice, please try again!");
                            break;
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. please try again.");
                    sc.next();
                }
            } while (!validChoice);
        }

    }

    void initMovesTable(){

        this.movesTable.put("Punch", new Move("Punch", 5, 0, 0, 90));
        this.movesTable.put("Kick", new Move("Kick", 7, 0, 1, 75));
        this.movesTable.put("Slap", new Move("Slap", 3, 0, 2, 100));
        this.movesTable.put("Scratch", new Move("Scratch", 3, 0, 0, 95));
        this.movesTable.put("Bite", new Move("Bite", 2, 0, 3, 65));

    }

    private Player initPlayer(){
        ArrayList<Move> moves = new ArrayList<>();

        moves.add(this.movesTable.get("Punch"));
        moves.add(this.movesTable.get("Kick"));
        moves.add(this.movesTable.get("Slap"));

        return new Player("jack", 80, 20, 10, 10, 20, moves);
    }
    private Enemy initEasyEnemy(){
        ArrayList<Move> moves = new ArrayList<>();

        moves.add(this.movesTable.get("Bite"));
        moves.add(this.movesTable.get("Scratch"));

        return new Enemy("wil", 25, 10, 5, 4, 5, moves, 5);

    }

    private Enemy initHardEnemy(){
        ArrayList<Move> moves = new ArrayList<>();

        moves.add(this.movesTable.get("Punch"));
        moves.add(this.movesTable.get("Kick"));
        moves.add(this.movesTable.get("Slap"));
        moves.add(this.movesTable.get("Scratch"));

        return new Enemy("wil2", 50, 10, 5, 4, 5, moves, 10);

    }

    private void battle(Player player, Enemy enemy, Scanner inputScanner, Random random){
        int turnCounter = 1, choice = 0;

        System.out.println(player.getName() + " vs " + enemy.getName());

        while(true){
            System.out.println("It is now turn " + turnCounter);

            if(turnCounter % 2 != 0){
                System.out.println("It is " + player.getName() + "'s turn!");
                System.out.println("Moves list : ");
                for(int i = 0; i < player.moves.size(); i++){
                    String name = player.moves.get(i).getName();
                    System.out.printf("%d. %s\n", i+1, name);
                }
                System.out.print("Choice : ");

                choice = inputScanner.nextInt() - 1;
                System.out.printf("%s chose to use %s\n", player.getName(), player.moves.get(choice).getName());
                player.use(enemy, player.moves.get(choice));
            }
            else{
                System.out.println("It is " + enemy.getName() + "'s turn!");
                choice = random.nextInt(enemy.moves.size());
                System.out.printf("%s chose to use %s\n", enemy.getName(), enemy.moves.get(choice).getName());
                enemy.use(player, enemy.moves.get(choice));

            }
            int result = checkIfBattleStatus(player, enemy);

            if(result == 1){
                int xpGained = enemy.getXpValue();

                System.out.printf("Battle is over! %s won!\n", player.getName());
                player.updateTotalXP(enemy.getXpValue());
                System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP(), player.getLvl()+ 1);
                player.restore();
                break;
            }
            else if(result == 2){
                int xpGained = max(enemy.getXpValue()/2, 1);
                System.out.printf("Battle is over! %s lost!\n", player.getName());
                player.updateTotalXP(xpGained);
                System.out.printf("%s gained %d xp! he needs %d more xp to reach level %d\n", player.getName(), xpGained, player.getXPTillLvl() - player.getTotalXP() , player.getLvl()+ 1);
                player.restore();
                break;
            }

            System.out.printf("%s current hp is : %d\n", player.getName(), player.getHp());
            System.out.printf("%s current hp is : %d\n", enemy.getName(), enemy.getHp());

            turnCounter++;

        }
    }
    private int checkIfBattleStatus(Character player, Character enemy){
        if(player.getHp() == 0)
            return 2;
        else if(enemy.getHp() == 0)
            return 1;

        return 0;
    }
    private void startGame(Scanner sc){
        boolean validChoice = false;
        int choice = 0;
        Random random = new Random(System.currentTimeMillis());

        initMovesTable();
        Player player = initPlayer();


        while(true){
            do {
                System.out.flush();
                System.out.print("\n\n");
                System.out.println("1. Battle (easy)");
                System.out.println("2. Battle (hard)");
                System.out.println("3. Current stats");
                System.out.println("4. main menu");
                System.out.print("\n\n");
                try {

                    System.out.print("Choice : ");
                    choice = sc.nextInt();

                    switch (choice) {
                        case 1:
                            battle(player, this.initEasyEnemy(), sc, random);
                            player.restore();
                            validChoice = true;
                            break;
                        case 2:
                            battle(player, this.initHardEnemy(), sc, random);
                            player.restore();
                            validChoice = true;
                            break;
                        case 3:
                            System.out.println("\n\nPlayer name : " + player.getName());
                            System.out.println("Player hp : " + player.getHp() + "/" + player.getMaxHP());
                            System.out.println("Player mp : " + player.getMp() + "/" + player.getMaxMP());
                            System.out.println("Player str : " + player.getStr());
                            System.out.println("Player def : " + player.getDef());
                            System.out.println("Player speed : " + player.getSpeed());
                            System.out.println("Player level : " + player.getLvl());
                            System.out.println("Player total xp : " + player.getTotalXP());
                            System.out.println("Player xp till next level : " + player.getXPTillLvl());

                            break;
                        case 4:
                            return;
                        default:
                            System.out.println("Invalid choice, please try again!");
                            break;
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input. please try again.");
                    sc.next();
                }
            } while (!validChoice);
        }


    }
}
