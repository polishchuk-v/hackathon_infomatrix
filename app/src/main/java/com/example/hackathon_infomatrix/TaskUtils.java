package com.example.hackathon_infomatrix;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskUtils {

    public static List<TaskModel> filterTodayTasks(List<TaskModel> allTasks) {
        List<TaskModel> todayTasks = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        long startOfDay = today.getTimeInMillis();
        long endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1;

        for (TaskModel task : allTasks) {
            if (task.getCreatedAt() >= startOfDay && task.getCreatedAt() <= endOfDay) {
                todayTasks.add(task);
            }
        }

        // Сортуємо: невиконані зверху, виконані знизу
        todayTasks.sort((t1, t2) -> {
            if (t1.isCompleted() && !t2.isCompleted()) return 1;
            if (!t1.isCompleted() && t2.isCompleted()) return -1;
            return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
        });

        return todayTasks;
    }

    public static int calculateProgress(List<TaskModel> tasks) {
        if (tasks.isEmpty()) return 0;

        int completedCount = 0;
        for (TaskModel task : tasks) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }
        return (completedCount * 100) / tasks.size();
    }

    public static long getStartOfToday() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTimeInMillis();
    }
}