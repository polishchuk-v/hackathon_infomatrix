package com.example.hackathon_infomatrix;

public class TaskModel {
    private String id;
    private String title;
    private boolean completed;
    private long createdAt;
    private String userId;

    public TaskModel() {
        // Порожній конструктор для Firestore
    }

    public TaskModel(String title, boolean completed, long createdAt, String userId) {
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    // Гетери та сетери
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}