package com.example.hackathon_infomatrix;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTitleEditText;
    private Button createButton;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();
        setupFirebase();
        setupToolbar();
        setupClickListeners();
    }

    private void initViews() {
        taskTitleEditText = findViewById(R.id.editTextLoginEmail);
        createButton = findViewById(R.id.butonCreate);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Нова звичка");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupClickListeners() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });
    }

    private void createTask() {
        String title = taskTitleEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            taskTitleEditText.setError("Введіть назву завдання");
            taskTitleEditText.requestFocus();
            return;
        }

        if (currentUser == null) return;

        // Показуємо індикатор завантаження (можна додати ProgressBar)
        createButton.setEnabled(false);

        // Створюємо об'єкт завдання
        Map<String, Object> task = new HashMap<>();
        task.put("title", title);
        task.put("completed", false);
        task.put("createdAt", System.currentTimeMillis());
        task.put("userId", currentUser.getUid());

        // Зберігаємо в Firestore
        db.collection("users").document(currentUser.getUid())
                .collection("tasks")
                .document()
                .set(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        createButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(AddTaskActivity.this, "Завдання створено", Toast.LENGTH_SHORT).show();
                            finish(); // Повертаємось на головний екран
                        } else {
                            Toast.makeText(AddTaskActivity.this, "Помилка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}