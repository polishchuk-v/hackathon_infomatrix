package com.example.hackathon_infomatrix;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView taskListView;  // змінна називається taskListView
    private TaskAdapter taskAdapter;
    private TextView flameCountText;
    private TextView emptyStateText;

    private ImageView imageViewMenu;
    private LinearLayout bottomNavToday;
    private LinearLayout bottomNavAdd;
    private LinearLayout bottomNavProgress;
    private LinearLayout flameContainer;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<TaskModel> allTasks = new ArrayList<>();
    private List<TaskModel> todayTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupClickListeners();
        loadSampleData();
    }

    private void initViews() {
        try {
            // Ініціалізуємо всі view - ВАЖЛИВО: R.id.tasklist (як в XML)
            taskListView = findViewById(R.id.tasklist);  // саме tasklist, не taskListView
            flameCountText = findViewById(R.id.flameCountText);
            emptyStateText = findViewById(R.id.emptyStateText);
            imageViewMenu = findViewById(R.id.imageViewMenu);
            bottomNavToday = findViewById(R.id.bottomNavToday);
            bottomNavAdd = findViewById(R.id.bottomNavAdd);
            bottomNavProgress = findViewById(R.id.bottomNavProgress);
            flameContainer = findViewById(R.id.flameContainer);

            // Перевіряємо, чи всі view знайдено
            if (taskListView == null) {
                Toast.makeText(this, "Помилка: ListView не знайдено! Перевірте ID в XML", Toast.LENGTH_LONG).show();
                return;
            }

            // Створюємо адаптер і встановлюємо для ListView
            taskAdapter = new TaskAdapter(this, todayTasks);
            taskListView.setAdapter(taskAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Помилка ініціалізації: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        imageViewMenu.setOnClickListener(v -> {
            Toast.makeText(this, "📱 Меню", Toast.LENGTH_SHORT).show();
        });

        bottomNavToday.setOnClickListener(v -> {
            filterTodayTasks();
            Toast.makeText(this, "📅 Завдання на сьогодні", Toast.LENGTH_SHORT).show();
        });

        bottomNavAdd.setOnClickListener(v -> {
            Toast.makeText(this, "➕ Додати нове завдання", Toast.LENGTH_SHORT).show();
        });

        bottomNavProgress.setOnClickListener(v -> {
            Toast.makeText(this, "📊 Прогрес", Toast.LENGTH_SHORT).show();
        });

        flameContainer.setOnClickListener(v -> {
            Toast.makeText(this, "🔥 Прогрес", Toast.LENGTH_SHORT).show();
        });
    }



    private void filterTodayTasks() {
        if (todayTasks == null || allTasks == null) return;

        todayTasks.clear();
        todayTasks.addAll(TaskUtils.filterTodayTasks(allTasks));

        // Показуємо або ховаємо текст про порожній стан
        if (todayTasks.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            taskListView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            taskListView.setVisibility(View.VISIBLE);
        }

        // Оновлюємо адаптер
        if (taskAdapter != null) {
            taskAdapter.updateTasks(todayTasks);
        }
    }

    public void updateTaskStatus(TaskModel task, boolean isChecked) {
        // Оновлюємо статус в списку
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getId() != null && allTasks.get(i).getId().equals(task.getId())) {
                allTasks.get(i).setCompleted(isChecked);
                break;
            }
        }

        // Оновлюємо прогрес
        updateProgress();

        // Перефільтровуємо список
        filterTodayTasks();

        String message = isChecked ? "✅ Завдання виконано" : "📝 Завдання поновлено";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteTask(TaskModel task) {
        // Видаляємо зі списку
        allTasks.remove(task);

        // Оновлюємо відображення
        filterTodayTasks();
        updateProgress();

        Toast.makeText(this, "🗑️ Завдання видалено", Toast.LENGTH_SHORT).show();
    }

    private void updateProgress() {
        int progress = TaskUtils.calculateProgress(allTasks);
        flameCountText.setText(progress + "%");
    }
}