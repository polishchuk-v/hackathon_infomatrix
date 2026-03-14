package com.example.hackathon_infomatrix;

public class PlayerStats {
    private int level;
    private int currentExp;
    private int expToNextLevel;
    private int totalTasksCompleted;
    private int attackDamage;
    private int maxHealth;
    private int currentHealth;

    public PlayerStats() {
        this.level = 1;
        this.currentExp = 0;
        this.expToNextLevel = 100;
        this.totalTasksCompleted = 0;
        this.attackDamage = 10;
        this.maxHealth = 100;
        this.currentHealth = 100;
    }

    public PlayerStats(int level, int currentExp, int expToNextLevel, int totalTasksCompleted,
                       int attackDamage, int maxHealth, int currentHealth) {
        this.level = level;
        this.currentExp = currentExp;
        this.expToNextLevel = expToNextLevel;
        this.totalTasksCompleted = totalTasksCompleted;
        this.attackDamage = attackDamage;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
    }

    // Гетери та сетери
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getCurrentExp() { return currentExp; }
    public void setCurrentExp(int currentExp) { this.currentExp = currentExp; }

    public int getExpToNextLevel() { return expToNextLevel; }
    public void setExpToNextLevel(int expToNextLevel) { this.expToNextLevel = expToNextLevel; }

    public int getTotalTasksCompleted() { return totalTasksCompleted; }
    public void setTotalTasksCompleted(int totalTasksCompleted) { this.totalTasksCompleted = totalTasksCompleted; }

    public int getAttackDamage() { return attackDamage; }
    public void setAttackDamage(int attackDamage) { this.attackDamage = attackDamage; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }

    // ВИПРАВЛЕНО: правильне додавання досвіду з врахуванням кількох рівнів
    public void addExp(int exp) {
        if (exp <= 0) return;

        currentExp += exp;

        // Перевіряємо, чи можна підвищити кілька рівнів
        while (currentExp >= expToNextLevel) {
            levelUp();
        }
    }

    // ВИПРАВЛЕНО: опціональне віднімання досвіду
    public void removeExp(int exp) {
        if (exp <= 0) return;

        currentExp = Math.max(0, currentExp - exp);

        // Перевіряємо, чи не треба понизити рівень (опціонально)
        // while (currentExp < 0 && level > 1) {
        //     levelDown();
        // }
    }

    private void levelUp() {
        currentExp -= expToNextLevel;
        level++;

        // ВИПРАВЛЕНО: правильний розрахунок досвіду для наступного рівня
        expToNextLevel = calculateExpForLevel(level);

        attackDamage = calculateAttackForLevel(level);
        maxHealth = calculateHealthForLevel(level);
        currentHealth = maxHealth; // Відновлюємо здоров'я
    }

    private int calculateExpForLevel(int level) {
        // Формула: 100 * рівень (можна змінити)
        return 100 * level;
    }

    private int calculateAttackForLevel(int level) {
        // Формула: 10 + 5 * (рівень - 1)
        return 10 + 5 * (level - 1);
    }

    private int calculateHealthForLevel(int level) {
        // Формула: 100 + 50 * (рівень - 1)
        return 100 + 50 * (level - 1);
    }

    public void takeDamage(int damage) {
        currentHealth = Math.max(0, currentHealth - damage);
    }

    public void heal(int amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public void resetHealth() {
        currentHealth = maxHealth;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public int getExpProgress() {
        return (currentExp * 100) / expToNextLevel;
    }
}