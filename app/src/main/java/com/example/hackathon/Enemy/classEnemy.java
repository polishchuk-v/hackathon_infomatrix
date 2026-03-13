package com.example.hackathon.Enemy;

public class classEnemy {
    private String name;
    private int maxHP;
    private int currHP;
    private int imgResID;

    public classEnemy (String name, int maxHP, int imgResId) {
        this.name = name;
        this.maxHP = maxHP;
        this.currHP = maxHP;
        this.imgResID = imgResId;
    }

    public String getName() {
        return name;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrHP() {
        return currHP;
    }

    public int getImgResID() {
        return imgResID;
    }

    public void setCurrHP(int currHP) {
        this.currHP = currHP;
    }

    public String getHPText() {
        return currHP + " / " + maxHP + " HP";
    }

    public int getHPperc() {
        return (currHP * 100) / maxHP;
    }
}
