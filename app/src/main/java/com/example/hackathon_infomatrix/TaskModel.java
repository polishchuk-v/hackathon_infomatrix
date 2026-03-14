package com.example.hackathon_infomatrix;

public class TaskModel {
    private String id;
    private String title;
    private boolean completed;
    private long createdAt;
    private String userEmail; // Змінено з userId на userEmail

    public TaskModel() {
        // Порожній конструктор для Firestore
    }

    public TaskModel(String title, boolean completed, long createdAt, String userEmail) {
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.userEmail = userEmail;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}