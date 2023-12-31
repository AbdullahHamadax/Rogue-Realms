package Classes.core;

import Classes.core.battle.BattleManager;
import Classes.entity.Enemy;
import Classes.entity.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class GameManager {
    private Scanner sc;
    private final String scriptPath = "C:\\Users\\Altyseer\\IdeaProjects\\LunarNight\\src\\Resources";
    private final HashMap<String, Move> moves;
    private final HashMap<String, Enemy> enemies;
    private final ArrayList<Event> events;
    private final Player player;


    public GameManager(Scanner sc) {
        this.sc = sc;
        moves = initMoves();
        enemies = initEnemies();
        player = initPlayer();
        events = initEvents();
    }


    private String[] loadScript(int id){
        List<String> listOfStrings = new ArrayList<>();

        try{
            URL res = this.getClass().getResource("/" + id + ".txt");
            BufferedReader bf = new BufferedReader(new FileReader(((URL) res).getPath()));

            String line = bf.readLine();

            while (line != null) {
                listOfStrings.add(line);
                line = bf.readLine();
            }
        }
        catch (IOException exception){
            System.out.print(exception.getMessage());
        };

        return listOfStrings.toArray(String[]::new);
    }

    private Player initPlayer(){
        Player player = new Player("jack", 80, 20, 10, 10, 20);
        player.moves.add(moves.get("Tackle"));
        player.moves.add(moves.get("Slap"));
        player.moves.add(moves.get("Bite"));
        player.moves.add(moves.get("Body Slam"));


        return player;
    }

    private HashMap<String, Move> initMoves(){
        HashMap<String, Move> moves = new HashMap<>();

        moves.put("Punch", new Move("Punch", 15, 0, 90));
        moves.put("Kick", new Move("Kick", 25, 0, 75));
        moves.put("Slap", new Move("Slap", 10, 0, 100));
        moves.put("Scratch", new Move("Scratch", 12, 0, 95));
        moves.put("Bite", new Move("Bite", 18, 0,  65));
        moves.put("Headbutt", new Move("Headbutt", 20, 0, 85));
        moves.put("Body Slam", new Move("Body Slam", 22, 0, 80));
        moves.put("Double Kick", new Move("Double Kick", 18, 0, 90));
        moves.put("Tackle", new Move("Tackle", 15, 0, 95));
        moves.put("Roundhouse Kick", new Move("Roundhouse Kick", 30, 0, 70));

        return moves;
    }
    private HashMap<String, Enemy> initEnemies(){
        HashMap<String, Enemy> enemies = new HashMap<>();
        enemies.put("Fiend", new Enemy("Fiend", 80, 30, 15, 12, 10, 15));
        enemies.get("Fiend").moves.add(moves.get("Tackle"));
        enemies.get("Fiend").moves.add(moves.get("Slap"));
        enemies.get("Fiend").moves.add(moves.get("Bite"));


        enemies.put("Ghoul", new Enemy("Ghoul", 60, 30, 25, 8, 10, 25));
        enemies.get("Ghoul").moves.add(moves.get("Tackle"));
        enemies.get("Ghoul").moves.add(moves.get("Headbutt"));
        enemies.get("Ghoul").moves.add(moves.get("Bite"));


        enemies.put("Slime", new Enemy("Slime", 40, 10, 6, 8, 5, 7));
        enemies.get("Slime").moves.add(moves.get("Tackle"));


        enemies.put("Wisp", new Enemy("Wisp", 55, 12, 30, 5, 18, 30));
        enemies.get("Wisp").moves.add(moves.get("Bite"));
        enemies.get("Wisp").moves.add(moves.get("Tackle"));

        return enemies;
    }
    private ArrayList<Event> initEvents(){

        ArrayList<Event> events = new ArrayList<>();

        BattleManager battleManager = new BattleManager(sc);

        /*
        HERE IS THE CREATION OF EVERY EVENT
        */

        DialogueEvent start = new DialogueEvent("Discovery", loadScript(1));
        events.add(start);

        DialogueEvent investigation = new DialogueEvent("Investigation", loadScript(2));
        events.add(investigation);

        ChoiceEvent emOrRes = new ChoiceEvent("Embrace or Resist",
                "The voices in your head tug at your sanity as you grapple with the newfound vampiric powers pulsating within.",
                new String[]{
                        "Embrace the vampiric abilities, feeling the surge of strength and agility coursing through your veins.",
                        "Resist the vampiric urges, relying on your traditional combat skills."
                });
        events.add(emOrRes);

        BattleEvent battleEvent1 = new BattleEvent("Fiend Fight", battleManager, player, enemies.get("Fiend"));
        events.add(battleEvent1);

        BattleEvent battleEvent2 = new BattleEvent("Slime Fight", battleManager, player, enemies.get("Slime"));
        events.add(battleEvent2);

        BattleEvent battleEvent3 = new BattleEvent("Big Fight", battleManager, player,
                new Enemy[] {enemies.get("Slime"), enemies.get("Slime"), enemies.get("Ghoul"), enemies.get("Slime"),enemies.get("Slime")});
        events.add(battleEvent2);


        /*
        HERE IS THE CONNECTING OF THE EVENTS
         */

        start.next = investigation;
        investigation.next = emOrRes;
        emOrRes.nextEvents = new Event[]{battleEvent1, battleEvent2};
        battleEvent1.next = battleEvent3;
        battleEvent2.next = battleEvent3;

        return events;
    }
    public void start(){
        Event head = getHeadEvent();
        while(head != null){
            head = head.start(sc);
        }
    }
    public Player getPlayer(){
        return player;
    }
    public Event getHeadEvent(){
        return events.get(0);
    }
}