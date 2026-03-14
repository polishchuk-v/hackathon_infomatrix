package com.example.hackathon.data.repository;

import android.content.Context;
import com.example.hackathon.data.models.Task;
import com.example.hackathon.utils.SharedPrefsHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskRepository {
    private SharedPrefsHelper prefsHelper;
    private static final String TASKS_KEY = "all_tasks";
    private int nextId = 0;

    public TaskRepository(Context context) {
        prefsHelper = new SharedPrefsHelper(context);
        // Инициализируем nextId при создании
        List<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }
    }

    public List<Task> getAllTasks() {
        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = prefsHelper.loadList(TASKS_KEY, type);
        return tasks != null ? tasks : new ArrayList<>();
    }

    public Task addTask(String title, String category) {
        List<Task> tasks = getAllTasks();
        Task newTask = new Task(nextId++, title, getCurrentDate());
        newTask.setCategory(category);
        tasks.add(newTask);
        saveTasks(tasks);
        return newTask;
    }

    public void updateTask(Task task) {
        List<Task> tasks = getAllTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
        saveTasks(tasks);
    }

    public void deleteTask(int taskId) {
        List<Task> tasks = getAllTasks();
        tasks.removeIf(task -> task.getId() == taskId);
        saveTasks(tasks);
    }

    public List<Task> getTasksByDate(String date) {
        List<Task> result = new ArrayList<>();
        for (Task task : getAllTasks()) {
            if (task.getCreatedAt().equals(date)) {
                result.add(task);
            }
        }
        return result;
    }

    public boolean hasCompletedTasksOnDate(String date) {
        for (Task task : getAllTasks()) {
            if (date.equals(task.getCompletedDate())) {
                return true;
            }
        }
        return false;
    }

    private void saveTasks(List<Task> tasks) {
        prefsHelper.saveList(TASKS_KEY, tasks);
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}