package Classes.Entity;

import Classes.Move;

import java.util.ArrayList;

public class Player extends Character{
    private int totalXP, XPTillLvl;
    public Player(String name, int maxHP, int maxMP, int str, int def, int speed, ArrayList<Move> moves) {
        super(name, maxHP, maxMP, str, def, speed, moves);
        this.totalXP = CalculateXPTillLvl(this.getLvl());
        this.XPTillLvl = CalculateXPTillLvl(this.getLvl() + 1);
    }

    private int CalculateXPTillLvl(int lvl){
        double XPConst1 = 0.3;
        double XPConst2 = 2.0;
        return (int) ((lvl/ XPConst1) * XPConst2) + this.getTotalXP();
    }
    private void levelUp(){
        this.setLvl(this.getLvl() + 1);
    }

    public int getTotalXP() {
        return totalXP;
    }

    public int getXPTillLvl(){
        return XPTillLvl;
    }
    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
        while(this.totalXP >= this.XPTillLvl){
            levelUp();
            System.out.println("PLAYER LEVELED UP!! he is now level " + this.getLvl());
            this.XPTillLvl = CalculateXPTillLvl(this.getLvl() + 1);
            updateStats();
        }
    }

    public void updateStats(){
        this.setMaxHP((int) (this.getHp() * (1.25)));
        this.setMaxMP((int) (this.getMaxMP() * (1.25)));
        this.setStr(this.getStr() + 1);
        this.setDef(this.getDef() + 1);

    }

    public void updateTotalXP(int value){
        setTotalXP(this.totalXP + value);
    }
}
