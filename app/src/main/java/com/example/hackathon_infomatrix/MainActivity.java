package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView taskListView;
    private TaskAdapter taskAdapter;
    private List<TaskItem> taskList;
    private TextView emptyStateText;
    private LinearLayout bottomNavAdd, bottomNavToday, bottomNavProgress;
    private ImageView imageViewMenu;
    private TextView flameCountText;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFirebase();
        loadTasks();
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

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Якщо користувач не авторизований, перенаправляємо на екран входу
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    private void loadTasks() {
        if (currentUser == null) return;

        db.collection("users").document(currentUser.getUid())
                .collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            taskList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TaskItem taskItem = document.toObject(TaskItem.class);
                                taskItem.setId(document.getId());
                                taskList.add(taskItem);
                            }
                            taskAdapter.notifyDataSetChanged();
                            updateEmptyState();
                            updateFlameCount();
                        } else {
                            Toast.makeText(MainActivity.this, "Помилка завантаження завдань", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void updateFlameCount() {
        if (taskList.isEmpty()) {
            flameCountText.setText("0%");
            return;
        }

        int completedCount = 0;
        for (TaskItem task : taskList) {
            if (task.isCompleted()) {
                completedCount++;
            }
        }
        int progress = (completedCount * 100) / taskList.size();
        flameCountText.setText(progress + "%");
    }

    private void setupClickListeners() {
        bottomNavAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            }
        });

        bottomNavToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Поки що просто перезавантажуємо список
                loadTasks();
                Toast.makeText(MainActivity.this, "Сьогодні", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Прогрес: " + flameCountText.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Тут можна відкрити меню (наприклад, BottomSheet або PopupMenu)
                Toast.makeText(MainActivity.this, "Меню", Toast.LENGTH_SHORT).show();
            }
        });

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskItem task = taskList.get(position);
                // Відкрити деталі завдання або редагування
                Toast.makeText(MainActivity.this, "Завдання: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Оновлюємо список при поверненні на екран
        loadTasks();
    }
}