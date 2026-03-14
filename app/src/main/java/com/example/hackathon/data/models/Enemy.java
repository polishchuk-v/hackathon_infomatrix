package com.example.hackathon.data.models;

public class Enemy {
    private int level;
    private String name;
    private int maxHp;
    private int currentHp;
    private int imageResId;
    private boolean isBoss;

    public Enemy(int level, String name, int maxHp, int imageResId, boolean isBoss) {
        this.level = level;
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.imageResId = imageResId;
        this.isBoss = isBoss;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(currentHp, 0);
    }

    public int getLevel() { return level; }
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getImageResId() { return imageResId; }
    public boolean isBoss() { return isBoss; }

    public int getHpPercentage() {
        return (currentHp * 100) / maxHp;
    }
    public String getHpText() {
        return currentHp + " / " + maxHp + " HP";
    }

    public boolean takeDamage(int damage) {
        currentHp = Math.max(currentHp - damage, 0);
        return currentHp == 0;
    }

    public void resetHp() {
        currentHp = maxHp;
    }
}