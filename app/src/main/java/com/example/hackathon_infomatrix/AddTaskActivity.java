package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTaskActivity extends AppCompatActivity {

    private EditText editHabitName;
    private Toolbar toolbar;
    private androidx.appcompat.widget.AppCompatButton btnCreate;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Ініціалізація Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Перевірка авторизації
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Отримуємо email користувача
        userEmail = mAuth.getCurrentUser().getEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Email користувача не знайдено", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        editHabitName = findViewById(R.id.editTextLoginEmail);
        btnCreate = findViewById(R.id.butonCreate);

        // Налаштування toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        // Стрілка назад
        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Повертаємось до MainActivity без збереження
        });

        // Кнопка створити
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

        // Створюємо об'єкт задачі
        TaskModel newTask = new TaskModel();
        newTask.setTitle(habitName);
        newTask.setCompleted(false);
        newTask.setCreatedAt(System.currentTimeMillis());
        newTask.setUserEmail(userEmail);

        // Зберігаємо в Firebase
        db.collection("tasks")
                .add(newTask)
                .addOnSuccessListener(documentReference -> {
                    // Отримуємо ID створеного документа
                    String taskId = documentReference.getId();
                    newTask.setId(taskId);

                    Toast.makeText(this, "✅ Звичку створено!", Toast.LENGTH_SHORT).show();

                    // Повертаємо результат до MainActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_task_created", true);
                    setResult(RESULT_OK, resultIntent);

                    finish(); // Повертаємось до MainActivity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "❌ Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}