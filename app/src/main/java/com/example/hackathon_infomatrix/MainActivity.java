package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ADD_TASK_REQUEST_CODE = 100;

    private ListView taskListView;
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
    private String userEmail;

    private List<TaskModel> allTasks = new ArrayList<>();
    private List<TaskModel> todayTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show();
            return;
        }

        userEmail = mAuth.getCurrentUser().getEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Email користувача не знайдено", Toast.LENGTH_SHORT).show();
            return;
        }

        initViews();
        setupClickListeners();
        loadTasksFromFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromFirebase();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            // Оновлюємо список після створення нової звички
            loadTasksFromFirebase();
            Toast.makeText(this, "Список оновлено", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        try {
            taskListView = findViewById(R.id.tasklist);
            flameCountText = findViewById(R.id.flameCountText);
            emptyStateText = findViewById(R.id.emptyStateText);
            imageViewMenu = findViewById(R.id.imageViewMenu);
            bottomNavToday = findViewById(R.id.bottomNavToday);
            bottomNavAdd = findViewById(R.id.bottomNavAdd);
            bottomNavProgress = findViewById(R.id.bottomNavProgress);
            flameContainer = findViewById(R.id.flameContainer);

            if (taskListView == null) {
                Toast.makeText(this, "Помилка: ListView не знайдено! Перевірте ID в XML", Toast.LENGTH_LONG).show();
                return;
            }

            taskAdapter = new TaskAdapter(this, todayTasks);
            taskListView.setAdapter(taskAdapter);

        } catch (Exception e) {
            Log.e(TAG, "Помилка ініціалізації: " + e.getMessage(), e);
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
            try {
                Log.d(TAG, "Відкриття AddTaskActivity");
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
            } catch (Exception e) {
                Log.e(TAG, "Помилка відкриття AddTaskActivity: " + e.getMessage(), e);
                Toast.makeText(this, "Помилка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        bottomNavProgress.setOnClickListener(v -> {
            Toast.makeText(this, "📊 Прогрес", Toast.LENGTH_SHORT).show();
        });

        flameContainer.setOnClickListener(v -> {
            Toast.makeText(this, "🔥 Прогрес", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTasksFromFirebase() {
        if (userEmail == null) return;

        Log.d(TAG, "Завантаження завдань для email: " + userEmail);

        db.collection("tasks")
                .whereEqualTo("userEmail", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            allTasks.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TaskModel taskModel = document.toObject(TaskModel.class);
                                taskModel.setId(document.getId());
                                allTasks.add(taskModel);
                            }

                            Log.d(TAG, "Завантажено " + allTasks.size() + " завдань");

                            filterTodayTasks();
                            updateProgress();

                        } else {
                            Log.e(TAG, "Помилка завантаження: " + task.getException().getMessage(), task.getException());
                            Toast.makeText(MainActivity.this, "Помилка завантаження: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void filterTodayTasks() {
        if (todayTasks == null || allTasks == null) return;

        todayTasks.clear();
        todayTasks.addAll(TaskUtils.filterTodayTasks(allTasks));

        if (todayTasks.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            taskListView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            taskListView.setVisibility(View.VISIBLE);
        }

        if (taskAdapter != null) {
            taskAdapter.updateTasks(todayTasks);
        }
    }

    public void updateTaskStatus(TaskModel task, boolean isChecked) {
        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getId() != null && allTasks.get(i).getId().equals(task.getId())) {
                allTasks.get(i).setCompleted(isChecked);
                break;
            }
        }

        if (task.getId() != null) {
            db.collection("tasks")
                    .document(task.getId())
                    .update("completed", isChecked)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Статус завдання оновлено: " + task.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Помилка оновлення статусу: " + e.getMessage(), e);
                        Toast.makeText(this, "Помилка оновлення: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        updateProgress();
        filterTodayTasks();

        String message = isChecked ? "✅ Завдання виконано" : "📝 Завдання поновлено";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void deleteTask(TaskModel task) {
        if (task.getId() != null) {
            db.collection("tasks")
                    .document(task.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        allTasks.remove(task);
                        filterTodayTasks();
                        updateProgress();
                        Log.d(TAG, "Завдання видалено: " + task.getId());
                        Toast.makeText(MainActivity.this, "🗑️ Завдання видалено", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Помилка видалення: " + e.getMessage(), e);
                        Toast.makeText(MainActivity.this, "Помилка видалення: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateProgress() {
        int progress = TaskUtils.calculateProgress(allTasks);
        flameCountText.setText(progress + "%");
    }
}