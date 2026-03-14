package com.example.hackathon_infomatrix;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editHabitName;
    private Toolbar toolbar;
    private androidx.appcompat.widget.AppCompatButton btnCreate;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        editHabitName = findViewById(R.id.editTextLoginEmail);
        btnCreate = findViewById(R.id.butonCreate);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        btnCreate.setOnClickListener(v -> {
            createNewHabit();
        });
    }

    private void createNewHabit() {
        String habitName = editHabitName.getText().toString().trim();

        // Перевірка чи поле не пусте
        if (TextUtils.isEmpty(habitName)) {
            Toast.makeText(this, "Введіть назву звички", Toast.LENGTH_SHORT).show();
            return;
        }

        // Перевірка чи користувач авторизований
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Створюємо об'єкт задачі
        TaskModel newTask = new TaskModel();
        newTask.setTitle(habitName);
        newTask.setCompleted(false);
        newTask.setCreatedAt(System.currentTimeMillis());
        newTask.setUserId(userId);

        // Зберігаємо в Firebase
        db.collection("tasks")
                .add(newTask)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "✅ Звичку створено!", Toast.LENGTH_SHORT).show();
                    finish(); // Повертаємось до MainActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}