package com.example.hackathon_infomatrix;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;

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
        // ВИПРАВЛЕНО: додано ініціалізацію всіх полів
        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        createButton = findViewById(R.id.butonCreate);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);

        // Лог для перевірки
        Log.d("AddTask", "Views initialized: " + (taskTitleEditText != null));
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("AddTask", "Користувач не авторизований");
            Toast.makeText(this, "Користувач не авторизований", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.d("AddTask", "Користувач: " + currentUser.getEmail());
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
        // ВИПРАВЛЕНО: перевірка на null
        if (taskTitleEditText == null) {
            Log.e("AddTask", "taskTitleEditText is null");
            Toast.makeText(this, "Помилка ініціалізації", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = taskTitleEditText.getText().toString().trim();
        Log.d("AddTask", "Створення завдання: " + title);

        if (TextUtils.isEmpty(title)) {
            taskTitleEditText.setError("Введіть назву завдання");
            taskTitleEditText.requestFocus();
            return;
        }

        if (currentUser == null) {
            Log.e("AddTask", "currentUser is null");
            Toast.makeText(this, "Помилка авторизації", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        int expReward = 10 + (int)(Math.random() * 20); // 10-30 досвіду

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("title", title);
        taskMap.put("completed", false);
        taskMap.put("wasCompleted", false);
        taskMap.put("createdAt", System.currentTimeMillis());
        taskMap.put("userId", currentUser.getUid());
        taskMap.put("expReward", expReward);

        Log.d("AddTask", "Збереження в Firestore...");

        db.collection("users").document(currentUser.getUid())
                .collection("tasks")
                .document()
                .set(taskMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> firestoreTask) {
                        setLoading(false);

                        if (firestoreTask.isSuccessful()) {
                            Log.d("AddTask", "Завдання успішно збережено!");
                            Toast.makeText(AddTaskActivity.this,
                                    "Завдання створено! +" + expReward + " досвіду",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String error = firestoreTask.getException() != null ?
                                    firestoreTask.getException().getMessage() :
                                    "Невідома помилка";
                            Log.e("AddTask", "Помилка: " + error);
                            Toast.makeText(AddTaskActivity.this,
                                    "Помилка: " + error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null && createButton != null && taskTitleEditText != null) {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                createButton.setEnabled(false);
                taskTitleEditText.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                createButton.setEnabled(true);
                taskTitleEditText.setEnabled(true);
            }
        }
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