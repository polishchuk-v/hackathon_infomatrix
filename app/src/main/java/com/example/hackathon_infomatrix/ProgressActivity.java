package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProgressActivity extends AppCompatActivity {

    private TextView enemyNameText, enemyLevelText, hpText, playerLevelText, playerDamageText;
    private ProgressBar hpBar, playerExpBar;
    private ImageView enemySprite;
    private Button attackButton, backButton;
    private TextView battleCountText;

    private Enemy currentEnemy;
    private PlayerStats playerStats;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private int battleCount = 0;
    private boolean inBattle = false;
    private boolean isEnemyTurn = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable enemyAttackRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        initViews();
        setupFirebase();
        loadPlayerStats();
        setupClickListeners();
    }

    private void initViews() {
        enemyNameText = findViewById(R.id.enemyName);
        enemyLevelText = findViewById(R.id.enemyLevelText);
        hpText = findViewById(R.id.hpText);
        playerLevelText = findViewById(R.id.playerLevelText);
        playerDamageText = findViewById(R.id.playerDamageText);
        hpBar = findViewById(R.id.hpBar);
        playerExpBar = findViewById(R.id.playerExpBar);
        enemySprite = findViewById(R.id.enemySprite);
        attackButton = findViewById(R.id.attackButton);
        backButton = findViewById(R.id.backButton);
        battleCountText = findViewById(R.id.battleCountText);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void loadPlayerStats() {
        Intent intent = getIntent();
        int level = intent.getIntExtra("playerLevel", 1);
        int health = intent.getIntExtra("playerHealth", 100);
        int maxHealth = intent.getIntExtra("playerMaxHealth", 100);
        int damage = intent.getIntExtra("playerDamage", 10);
        int currentExp = intent.getIntExtra("playerExp", 0);
        int expToNext = intent.getIntExtra("playerExpToNext", 100);

        playerStats = new PlayerStats();
        playerStats.setLevel(level);
        playerStats.setCurrentHealth(health);
        playerStats.setMaxHealth(maxHealth);
        playerStats.setAttackDamage(damage);
        playerStats.setCurrentExp(currentExp);
        playerStats.setExpToNextLevel(expToNext);

        updateDisplay();
        spawnNewEnemy();
    }

    private void spawnNewEnemy() {
        currentEnemy = new Enemy(playerStats.getLevel());
        battleCount++;
        inBattle = true;
        isEnemyTurn = false;

        // ВИПРАВЛЕНО: скасовуємо попередню атаку ворога, якщо була
        if (enemyAttackRunnable != null) {
            handler.removeCallbacks(enemyAttackRunnable);
        }

        battleCountText.setText("Бій #" + battleCount);
        updateEnemyDisplay();
    }

    private void updateDisplay() {
        playerLevelText.setText("LVL " + playerStats.getLevel());
        playerDamageText.setText("⚔ " + playerStats.getAttackDamage());

        playerExpBar.setMax(playerStats.getExpToNextLevel());
        playerExpBar.setProgress(playerStats.getCurrentExp());
    }

    private void updateEnemyDisplay() {
        enemyNameText.setText(currentEnemy.getName());
        enemyLevelText.setText("LVL " + currentEnemy.getLevel());
        hpText.setText(currentEnemy.getCurrentHealth() + " / " + currentEnemy.getMaxHealth() + " HP");
        hpBar.setMax(currentEnemy.getMaxHealth());
        hpBar.setProgress(currentEnemy.getCurrentHealth());

        // Спроба завантажити спрайт
        int spriteResId = getResources().getIdentifier(
                currentEnemy.getSpriteName(),
                "drawable",
                getPackageName()
        );
        if (spriteResId != 0) {
            enemySprite.setImageResource(spriteResId);
        }
    }

    private void setupClickListeners() {
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inBattle || currentEnemy == null || isEnemyTurn) return;
                performAttack();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlayerStats();
                Intent intent = new Intent(ProgressActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performAttack() {
        // ВИПРАВЛЕНО: блокуємо множинні атаки
        if (!inBattle || isEnemyTurn) return;

        setButtonsEnabled(false);

        // Гравець атакує
        int playerDamage = playerStats.getAttackDamage();
        currentEnemy.takeDamage(playerDamage);

        Toast.makeText(this, "⚔ Ви завдали " + playerDamage + " шкоди!", Toast.LENGTH_SHORT).show();
        updateEnemyDisplay();

        if (!currentEnemy.isAlive()) {
            onEnemyDefeated();
            return;
        }

        // Ворог контратакує через затримку
        isEnemyTurn = true;
        enemyAttackRunnable = new Runnable() {
            @Override
            public void run() {
                if (inBattle && currentEnemy != null && currentEnemy.isAlive()) {
                    enemyAttack();
                } else {
                    isEnemyTurn = false;
                    setButtonsEnabled(true);
                }
            }
        };
        handler.postDelayed(enemyAttackRunnable, 800);
    }

    private void enemyAttack() {
        // ВИПРАВЛЕНО: перевіряємо, чи ворог ще живий
        if (!inBattle || !currentEnemy.isAlive()) {
            isEnemyTurn = false;
            setButtonsEnabled(true);
            return;
        }

        int enemyDamage = currentEnemy.getAttackDamage();
        playerStats.takeDamage(enemyDamage);

        Toast.makeText(this, "👹 Ворог завдав " + enemyDamage + " шкоди!", Toast.LENGTH_SHORT).show();
        updateDisplay();

        if (playerStats.getCurrentHealth() <= 0) {
            onPlayerDefeated();
            return;
        }

        isEnemyTurn = false;
        setButtonsEnabled(true);
    }

    private void onEnemyDefeated() {
        inBattle = false;
        isEnemyTurn = false;

        // ВІДМІНЯЄМО заплановану атаку ворога
        if (enemyAttackRunnable != null) {
            handler.removeCallbacks(enemyAttackRunnable);
        }

        int expReward = currentEnemy.getExpReward();
        playerStats.addExp(expReward);

        String victoryMessage = "🏆 Перемога! +" + expReward + " досвіду!";

        Random random = new Random();
        if (random.nextBoolean()) {
            int healAmount = playerStats.getMaxHealth() / 4;
            playerStats.heal(healAmount);
            victoryMessage += "\n❤ Ви відновили " + healAmount + " здоров'я!";
        }

        Toast.makeText(this, victoryMessage, Toast.LENGTH_LONG).show();

        updateDisplay();
        savePlayerStats();

        // Спавн нового ворога
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnNewEnemy();
                setButtonsEnabled(true);
            }
        }, 2000);
    }

    private void onPlayerDefeated() {
        inBattle = false;
        isEnemyTurn = false;

        // ВІДМІНЯЄМО заплановану атаку ворога
        if (enemyAttackRunnable != null) {
            handler.removeCallbacks(enemyAttackRunnable);
        }

        Toast.makeText(this, "💀 Ви загинули... Але відродилися!", Toast.LENGTH_LONG).show();

        playerStats.setCurrentHealth(playerStats.getMaxHealth() / 2);
        updateDisplay();
        savePlayerStats();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnNewEnemy();
                setButtonsEnabled(true);
            }
        }, 2000);
    }

    private void setButtonsEnabled(boolean enabled) {
        attackButton.setEnabled(enabled && inBattle && !isEnemyTurn);
        backButton.setEnabled(enabled);
    }

    private void savePlayerStats() {
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
                    db.collection("users").document(currentUser.getUid())
                            .set(statsMap);
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlayerStats();
        if (enemyAttackRunnable != null) {
            handler.removeCallbacks(enemyAttackRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}