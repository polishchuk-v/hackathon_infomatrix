package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView taskListView;
    private TaskAdapter taskAdapter;
    private List<TaskItem> taskList;
    private TextView emptyStateText;
    private LinearLayout bottomNavAdd, bottomNavToday, bottomNavProgress;
    private ImageView imageViewMenu;
    private TextView flameCountText, levelText, taskCountText;
    private ProgressBar expProgressBar; // Це прогрес-бар для відсотка виконання завдань

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private PlayerStats playerStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFirebase();
        setupClickListeners();
    }

    private void initViews() {
        taskListView = findViewById(R.id.tasklist);
        emptyStateText = findViewById(R.id.emptyStateText);
        bottomNavAdd = findViewById(R.id.bottomNavAdd);
        bottomNavToday = findViewById(R.id.bottomNavToday);
        bottomNavProgress = findViewById(R.id.bottomNavProgress);
        imageViewMenu = findViewById(R.id.imageViewMenu);
        flameCountText = findViewById(R.id.flameCountText);
        levelText = findViewById(R.id.levelText);
        expProgressBar = findViewById(R.id.expProgressBar); // Прогрес-бар для відсотка
        taskCountText = findViewById(R.id.taskCountText);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList, this);
        taskListView.setAdapter(taskAdapter);

        Log.d("MainActivity", "Views initialized");
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d("MainActivity", "No user, redirecting to Welcome");
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        } else {
            Log.d("MainActivity", "User: " + currentUser.getEmail());
            loadPlayerStats();
        }
    }

    private void loadPlayerStats() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                playerStats = new PlayerStats();

                                playerStats.setLevel(getLongValue(document, "level", 1));
                                playerStats.setCurrentExp(getLongValue(document, "currentExp", 0));
                                playerStats.setExpToNextLevel(getLongValue(document, "expToNextLevel", 100));
                                playerStats.setTotalTasksCompleted(getLongValue(document, "totalTasksCompleted", 0));
                                playerStats.setAttackDamage(getLongValue(document, "attackDamage", 10));
                                playerStats.setMaxHealth(getLongValue(document, "maxHealth", 100));
                                playerStats.setCurrentHealth(getLongValue(document, "currentHealth", 100));

                                Log.d("MainActivity", "Player stats loaded");
                            } else {
                                playerStats = new PlayerStats();
                                savePlayerStats(true);
                                Log.d("MainActivity", "New player stats created");
                            }
                            updateLevelDisplay();
                            loadTasks();
                        } else {
                            Log.e("MainActivity", "Error loading stats", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Помилка завантаження статистики",
                                    Toast.LENGTH_SHORT).show();
                            playerStats = new PlayerStats();
                            updateLevelDisplay();
                            loadTasks();
                        }
                    }
                });
    }

    private int getLongValue(DocumentSnapshot document, String field, int defaultValue) {
        if (document.contains(field)) {
            Long value = document.getLong(field);
            return value != null ? value.intValue() : defaultValue;
        }
        return defaultValue;
    }

    private void savePlayerStats(boolean createNewIfNotExists) {
        if (currentUser == null || playerStats == null) return;

        Map<String, Object> statsMap = new HashMap<>();
        statsMap.put("level", playerStats.getLevel());
        statsMap.put("currentExp", playerStats.getCurrentExp());
        statsMap.put("expToNextLevel", playerStats.getExpToNextLevel());
        statsMap.put("totalTasksCompleted", playerStats.getTotalTasksCompleted());
        statsMap.put("attackDamage", playerStats.getAttackDamage());
        statsMap.put("maxHealth", playerStats.getMaxHealth());
        statsMap.put("currentHealth", playerStats.getCurrentHealth());

        db.collection("users").document(currentUser.getUid())
                .update(statsMap)
                .addOnFailureListener(e -> {
                    if (createNewIfNotExists) {
                        db.collection("users").document(currentUser.getUid())
                                .set(statsMap)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("MainActivity", "Player stats saved"))
                                .addOnFailureListener(e2 ->
                                        Log.e("MainActivity", "Error saving stats", e2));
                    }
                });
    }

    private void loadTasks() {
        if (currentUser == null) {
            Log.e("MainActivity", "currentUser is null in loadTasks");
            return;
        }

        Log.d("MainActivity", "Loading tasks for user: " + currentUser.getUid());

        db.collection("users").document(currentUser.getUid())
                .collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            taskList.clear();
                            int count = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TaskItem taskItem = document.toObject(TaskItem.class);
                                taskItem.setId(document.getId());
                                if (taskItem.getExpReward() == 0) {
                                    taskItem.setExpReward(10);
                                }
                                taskList.add(taskItem);
                                count++;
                                Log.d("MainActivity", "Loaded task: " + taskItem.getTitle());
                            }

                            Log.d("MainActivity", "Total tasks loaded: " + count);

                            taskAdapter.notifyDataSetChanged();
                            taskListView.invalidateViews();

                            updateEmptyState();
                            updateFlameCount(); // Це оновлює і прогрес-бар
                            updateTaskCount();

                        } else {
                            Log.e("MainActivity", "Error loading tasks", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Помилка завантаження завдань",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onTaskCompleted(TaskItem task) {
        if (playerStats != null) {
            if (!task.wasCompleted()) {
                playerStats.addExp(task.getExpReward());
                playerStats.setTotalTasksCompleted(playerStats.getTotalTasksCompleted() + 1);
                task.setWasCompleted(true);

                savePlayerStats(false);
                updateLevelDisplay();
                updateFlameCount(); // Оновлюємо прогрес-бар
                updateTaskCount();

                Log.d("MainActivity", "Task completed: " + task.getTitle());
            }
        }
    }

    public void onTaskUncompleted(TaskItem task) {
        if (playerStats != null && task.wasCompleted()) {
            playerStats.removeExp(task.getExpReward());
            playerStats.setTotalTasksCompleted(Math.max(0, playerStats.getTotalTasksCompleted() - 1));
            task.setWasCompleted(false);
            savePlayerStats(false);
            updateLevelDisplay();
            updateFlameCount(); // Оновлюємо прогрес-бар
            updateTaskCount();

            Toast.makeText(this,
                    "Завдання скасовано -" + task.getExpReward() + " досвіду",
                    Toast.LENGTH_SHORT).show();

            Log.d("MainActivity", "Task uncompleted: " + task.getTitle());
        }
    }

    // ВИПРАВЛЕНО: оновлення відсотка вогника І прогрес-бару
    public void updateFlameCount() {
        if (taskList.isEmpty()) {
            flameCountText.setText("0%");
            expProgressBar.setProgress(0); // Прогрес-бар 0%
            return;
        }

        int completedCount = 0;
        for (TaskItem task : taskList) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }

        // Розраховуємо відсоток виконання
        int progress = (completedCount * 100) / taskList.size();

        // Оновлюємо текст вогника
        flameCountText.setText(progress + "%");

        // ВАЖЛИВО: Оновлюємо прогрес-бар ТИМ ЖЕ відсотком
        expProgressBar.setProgress(progress);

        Log.d("MainActivity", "Progress updated: " + progress + "% (" + completedCount + "/" + taskList.size() + ")");

        // Зміна кольору в залежності від прогресу
        if (progress >= 75) {
            flameCountText.setTextColor(getColor(R.color.accent_blue));
            expProgressBar.setProgressTintList(getColorStateList(R.color.accent_blue));
        } else if (progress >= 50) {
            flameCountText.setTextColor(getColor(R.color.steel_blue));
            expProgressBar.setProgressTintList(getColorStateList(R.color.steel_blue));
        } else if (progress >= 25) {
            flameCountText.setTextColor(getColor(R.color.steel_blue));
            expProgressBar.setProgressTintList(getColorStateList(R.color.steel_blue));
        } else {
            flameCountText.setTextColor(getColor(R.color.pale_sky_blue));
            expProgressBar.setProgressTintList(getColorStateList(R.color.pale_sky_blue));
        }
    }

    // Додано: метод для підрахунку завдань
    private void updateTaskCount() {
        int completedCount = 0;
        for (TaskItem task : taskList) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }
        taskCountText.setText(completedCount + "/" + taskList.size());
    }

    private void updateLevelDisplay() {
        if (playerStats != null) {
            levelText.setText("LVL " + playerStats.getLevel());
            // Це для досвіду гравця, а не для прогресу завдань
            // int expProgress = playerStats.getExpProgress();
            // expProgressBar.setProgress(expProgress); // НЕ використовуємо це для expProgressBar
        }
    }

    private void updateEmptyState() {
        if (taskList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            taskListView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            taskListView.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        bottomNavAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Navigating to AddTaskActivity");
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        bottomNavToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTasks();
                Toast.makeText(MainActivity.this, "Сьогодні", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerStats != null) {
                    Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
                    intent.putExtra("playerLevel", playerStats.getLevel());
                    intent.putExtra("playerHealth", playerStats.getCurrentHealth());
                    intent.putExtra("playerMaxHealth", playerStats.getMaxHealth());
                    intent.putExtra("playerDamage", playerStats.getAttackDamage());
                    intent.putExtra("playerExp", playerStats.getCurrentExp());
                    intent.putExtra("playerExpToNext", playerStats.getExpToNextLevel());
                    startActivity(intent);
                }
            }
        });

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    private void showMenu() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Меню");
        String[] items = {"Статистика", "Налаштування", "Вийти"};
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    showStatsDialog();
                    break;
                case 1:
                    Toast.makeText(this, "Налаштування", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    logout();
                    break;
            }
        });
        builder.show();
    }

    private void showStatsDialog() {
        if (playerStats == null) return;

        String stats = "Рівень: " + playerStats.getLevel() + "\n" +
                "Досвід: " + playerStats.getCurrentExp() + "/" + playerStats.getExpToNextLevel() + "\n" +
                "Завдань виконано: " + playerStats.getTotalTasksCompleted() + "\n" +
                "Атака: " + playerStats.getAttackDamage() + "\n" +
                "Здоров'я: " + playerStats.getCurrentHealth() + "/" + playerStats.getMaxHealth();

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Статистика гравця")
                .setMessage(stats)
                .setPositiveButton("OK", null)
                .show();
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume - loading tasks");
        loadTasks();
        if (playerStats != null) {
            loadPlayerStats();
        }
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }
}