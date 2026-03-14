    package com.example.hackathon.ui.main;

    import android.app.AlertDialog;
    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.hackathon.Battle.BattleActivity;
    import com.example.hackathon.data.models.Task;
    import com.example.hackathon.data.models.User;
    import com.example.hackathon.data.repository.TaskRepository;
    import com.example.hackathon.data.repository.UserRepository;
    import com.example.hackathon_infomatrix.R;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;

    public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

        private TextView welcomeText, statsText;
        private RecyclerView tasksRecyclerView;
        private Button btnAddTask;
        private TextView navBattle;

        private UserRepository userRepository;
        private TaskRepository taskRepository;
        private TaskAdapter taskAdapter;
        private User currentUser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            userRepository = new UserRepository(this);
            taskRepository = new TaskRepository(this);
            currentUser = userRepository.getUser();

            checkDayReset();

            initViews();

            loadTasks();

            updateStats();
        }

        private void initViews() {
            welcomeText = findViewById(R.id.welcomeText);
            statsText = findViewById(R.id.statsText);
            tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
            btnAddTask = findViewById(R.id.btnAddTask);
            navBattle = findViewById(R.id.navBattle);

            welcomeText.setText("Вітаємо, " + currentUser.getName() + "!");

            tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            btnAddTask.setOnClickListener(v -> showAddTaskDialog());

            navBattle.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BattleActivity.class);
                startActivity(intent);
            });
        }

        private void loadTasks() {
            List<Task> tasks = taskRepository.getAllTasks();
            taskAdapter = new TaskAdapter(tasks, this);
            tasksRecyclerView.setAdapter(taskAdapter);
        }

        private void showAddTaskDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Додати нове завдання");

            final EditText input = new EditText(this);
            input.setHint("Опис завдання");
            input.setPadding(32, 16, 32, 16);
            builder.setView(input);

            String[] categories = {"Звичайне", "Робота", "Дім", "Хобі"};
            builder.setSingleChoiceItems(categories, 0, (dialog, which) -> {
            });

            builder.setPositiveButton("Додати", (dialog, which) -> {
                String taskText = input.getText().toString().trim();
                if (!taskText.isEmpty()) {
                    int selectedCategory = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                    String category = categories[selectedCategory];

                    taskRepository.addTask(taskText, category);
                    loadTasks();
                    Toast.makeText(this, "Завдання додано!", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Скасувати", null);
            builder.show();
        }

        private void updateStats() {
            int attackCount = currentUser.getStoredAttacks();
            statsText.setText("⚔️ " + attackCount);
        }

        private void checkDayReset() {
            String lastLogin = currentUser.getLastLoginDate();
            String today = getCurrentDate();

            if (!today.equals(lastLogin)) {
                boolean hadCompletedTasks = taskRepository.hasCompletedTasksOnDate(lastLogin);

                if (!hadCompletedTasks && !lastLogin.isEmpty()) {
                    int newLevel = Math.max(1, currentUser.getLevel() - 1);
                    currentUser.setLevel(newLevel);

                    Toast.makeText(this,
                            "Вчора не було виконаних завдань. Рівень знижено до " + newLevel,
                            Toast.LENGTH_LONG).show();
                }

                currentUser.setLastLoginDate(today);
                userRepository.saveUser(currentUser);
            }
        }

        private String getCurrentDate() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(new Date());
        }

        @Override
        public void onTaskCompleted(Task task) {
            taskRepository.updateTask(task);

            currentUser.setStoredAttacks(currentUser.getStoredAttacks() + 1);
            currentUser.setTotalTasksCompleted(currentUser.getTotalTasksCompleted() + 1);
            userRepository.saveUser(currentUser);

            updateStats();

            loadTasks();

            Toast.makeText(this, "+1 удар! ⚔️", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onTaskDeleted(Task task) {
            new AlertDialog.Builder(this)
                    .setTitle("Видалити завдання?")
                    .setMessage("Ви впевнені, що хочете видалити це завдання?")
                    .setPositiveButton("Так", (dialog, which) -> {
                        taskRepository.deleteTask(task.getId());
                        loadTasks(); // Перезагружаем список
                        Toast.makeText(this, "Завдання видалено", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Ні", null)
                    .show();
        }
    }

