package com.example.hackathon.data.models;

public class Task {
    private int id;
    private String title;
    private boolean isCompleted;
    private String completedDate;
    private String createdAt;
    private String category;

    public Task(int id, String title, String createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.isCompleted = false;
        this.completedDate = null;
        this.category = "Звичайне";
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCompletedDate() { return completedDate; }
    public String getCreatedAt() { return createdAt; }
    public String getCategory() { return category; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}