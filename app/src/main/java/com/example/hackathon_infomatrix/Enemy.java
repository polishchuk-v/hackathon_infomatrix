package com.example.hackathon_infomatrix;

import java.util.Random;

public class Enemy {
    private String name;
    private int level;
    private int maxHealth;
    private int currentHealth;
    private int attackDamage;
    private int expReward;
    private int goldReward;
    private String spriteName;

    public Enemy(int playerLevel) {
        Random random = new Random();
        this.level = playerLevel + random.nextInt(3); // Ворог може бути на 0-2 рівні вище
        this.name = generateName();
        this.maxHealth = 50 * this.level;
        this.currentHealth = this.maxHealth;
        this.attackDamage = 5 * this.level;
        this.expReward = 50 * this.level;
        this.goldReward = 20 * this.level;
        this.spriteName = "enemy_" + random.nextInt(3); // Випадковий спрайт
    }

    private String generateName() {
        String[] names = {"Goblin", "Orc", "Skeleton", "Dark Mage", "Wolf", "Troll", "Vampire", "Demon"};
        String[] titles = {"the Cursed", "the Dark", "the Fierce", "the Ancient", "the Corrupted"};
        Random random = new Random();
        return names[random.nextInt(names.length)] + " " + titles[random.nextInt(titles.length)];
    }

    public void takeDamage(int damage) {
        currentHealth = Math.max(0, currentHealth - damage);
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    // Гетери
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentHealth() { return currentHealth; }
    public int getAttackDamage() { return attackDamage; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
    public String getSpriteName() { return spriteName; }
}