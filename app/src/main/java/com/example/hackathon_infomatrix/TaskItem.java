package com.example.hackathon_infomatrix;

public class TaskItem {
    private String id;
    private String title;
    private boolean completed;
    private long createdAt;
    private String userId;
    private int expReward;
    private boolean wasCompleted;

    public TaskItem() {
        this.expReward = 10;
        this.wasCompleted = false;
        this.completed = false;
    }

    public TaskItem(String title, boolean completed, long createdAt, String userId) {
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.userId = userId;
        this.expReward = 10 + (int)(Math.random() * 20);
        this.wasCompleted = false;
    }

    // Гетери та сетери
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getExpReward() {
        return expReward;
    }

    public void setExpReward(int expReward) {
        this.expReward = expReward;
    }

    public boolean wasCompleted() {
        return wasCompleted;
    }

    public void setWasCompleted(boolean wasCompleted) {
        this.wasCompleted = wasCompleted;
    }
}