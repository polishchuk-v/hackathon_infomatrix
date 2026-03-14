package com.example.hackathon.data.repository;

import android.content.Context;
import com.example.hackathon.data.models.User;
import com.example.hackathon.utils.SharedPrefsHelper;

public class UserRepository {
    private SharedPrefsHelper prefsHelper;
    private static final String USER_KEY = "current_user";

    public UserRepository(Context context) {
        prefsHelper = new SharedPrefsHelper(context);
    }

    public void saveUser(User user) {
        prefsHelper.saveObject(USER_KEY, user);
    }

    public User getUser() {
        User user = prefsHelper.loadObject(USER_KEY, User.class);
        return user != null ? user : new User();
    }

    public void updateUser(User user) {
        saveUser(user);
    }

    public boolean isFirstLaunch() {
        return getUser().isFirstLaunch();
    }

    public void setFirstLaunchCompleted() {
        User user = getUser();
        user.setFirstLaunch(false);
        saveUser(user);
    }

    public void resetLevelIfNoTasks(boolean hadTasksYesterday) {
        User user = getUser();
        if (!hadTasksYesterday) {
            int newLevel = Math.max(1, user.getLevel() - 1);
            user.setLevel(newLevel);
            saveUser(user);
        }
    }
}