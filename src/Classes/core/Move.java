package Classes.core;


import Classes.entity.Character;

public class Move {
    private String name;
    private int power, accuracy, type;

    public Move(String name, int power, int type, int accuracy) {
        this.name = name;
        this.power = power;
        this.type = type;
        this.accuracy = accuracy;
    }

    public String getName() {
        return name;
    }

    public int use(Character user, Character target) {
        int damage = (int) (((double) user.getStr() / 10) * getPower()) + 1;
        double randomValue = Math.random();
        if (randomValue >= (double) getAccuracy() / 100)
            return -1;


        target.updateHp(damage * -1);
        return damage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}