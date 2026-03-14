package com.example.hackathon.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPrefsHelper {
    private SharedPreferences prefs;
    private Gson gson;

    public SharedPrefsHelper(Context context) {
        prefs = context.getSharedPreferences("hackathon_prefs", Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public <T> void saveObject(String key, T obj) {
        String json = gson.toJson(obj);
        prefs.edit().putString(key, json).apply();
    }

    public <T> T loadObject(String key, Class<T> classType) {
        String json = prefs.getString(key, null);
        if (json == null) return null;
        return gson.fromJson(json, classType);
    }

    public <T> void saveList(String key, List<T> list) {
        String json = gson.toJson(list);
        prefs.edit().putString(key, json).apply();
    }

    public <T> List<T> loadList(String key, Type type) {
        String json = prefs.getString(key, null);
        if (json == null) return new ArrayList<>();
        return gson.fromJson(json, type);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}