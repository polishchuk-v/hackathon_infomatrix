package com.example.hackathon.data.models;

public class User {
    private int id = 1;
    private String name = "";
    private int age = 0;
    private String timeZone = "";
    private int level = 1;
    private int storedAttacks = 0;
    private String lastLoginDate = "";
    private boolean isFirstLaunch = true;
    private int totalTasksCompleted = 0;
    private int totalEnemiesDefeated = 0;

    public User() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getTimeZone() { return timeZone; }
    public int getLevel() { return level; }
    public int getStoredAttacks() { return storedAttacks; }
    public String getLastLoginDate() { return lastLoginDate; }
    public int getTotalTasksCompleted() { return totalTasksCompleted; }
    public int getTotalEnemiesDefeated() { return totalEnemiesDefeated; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }
    public void setLevel(int level) { this.level = level; }
    public void setStoredAttacks(int storedAttacks) { this.storedAttacks = storedAttacks; }
    public void setLastLoginDate(String lastLoginDate) { this.lastLoginDate = lastLoginDate; }
    public void setTotalTasksCompleted(int totalTasksCompleted) { this.totalTasksCompleted = totalTasksCompleted; }
    public void setTotalEnemiesDefeated(int totalEnemiesDefeated) { this.totalEnemiesDefeated = totalEnemiesDefeated; }

    public boolean isFirstLaunch() { return isFirstLaunch; }
    public void setFirstLaunch(boolean firstLaunch) { isFirstLaunch = firstLaunch; }
}