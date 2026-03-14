package com.example.hackathon.ui.battle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hackathon_infomatrix.R;
import com.example.hackathon.data.models.Enemy;
import com.example.hackathon.data.models.User;
import com.example.hackathon.data.repository.UserRepository;
import com.example.hackathon.ui.main.MainActivity;

public class BattleActivity extends AppCompatActivity {

    private TextView enemyNameText, hpText, attacksCountText;
    private ProgressBar hpBar;
    private ImageView enemySprite;
    private Button attackButton, backButton;

    private UserRepository userRepository;
    private User currentUser;
    private Enemy currentEnemy;

    private int attackDamage = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        userRepository = new UserRepository(this);
        currentUser = userRepository.getUser();

        initViews();

        spawnEnemyForLevel(currentUser.getLevel());

        updateEnemyDisplay();
        updateAttacksCount();
    }

    private void initViews() {
        enemyNameText = findViewById(R.id.enemyName);
        hpText = findViewById(R.id.hpText);
        hpBar = findViewById(R.id.hpBar);
        enemySprite = findViewById(R.id.enemySprite);
        attackButton = findViewById(R.id.attackButton);
        backButton = findViewById(R.id.backButton);
        attacksCountText = findViewById(R.id.attacksCountText);

        attackButton.setOnClickListener(v -> attack());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(BattleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void spawnEnemyForLevel(int level) {
        boolean isBoss = (level % 5 == 0);

        if (level <= 3) {
            currentEnemy = new Enemy(
                    level,
                    isBoss ? "Гоблін-вождь" : "Гоблін-воїн",
                    50 + level * 10,
                    R.drawable.goblin1,
                    isBoss
            );
        } else if (level <= 6) {
            currentEnemy = new Enemy(
                    level,
                    isBoss ? "Орк-шаман" : "Орк-воїн",
                    100 + (level - 3) * 15,
                    R.drawable.goblin1,
                    isBoss
            );
        } else if (level <= 9) {
            currentEnemy = new Enemy(
                    level,
                    isBoss ? "Троль-король" : "Троль",
                    200 + (level - 6) * 20,
                    R.drawable.goblin1,
                    isBoss
            );
        } else {
            currentEnemy = new Enemy(
                    level,
                    isBoss ? "Дракон стародавній" : "Дракон",
                    500 + (level - 9) * 50,
                    R.drawable.goblin1,
                    isBoss
            );
        }
    }

    private void attack() {
        if (currentUser.getStoredAttacks() <= 0) {
            Toast.makeText(this, "Немає ударів! Виконай завдання!", Toast.LENGTH_LONG).show();
            return;
        }

        animateAttack();

        boolean isDead = currentEnemy.takeDamage(attackDamage);

        currentUser.setStoredAttacks(currentUser.getStoredAttacks() - 1);
        userRepository.saveUser(currentUser);

        updateEnemyDisplay();
        updateAttacksCount();

        if (isDead) {
            enemyDefeated();
        } else {
            Toast.makeText(this, "Удар! -" + attackDamage + " HP", Toast.LENGTH_SHORT).show();
        }
    }

    private void animateAttack() {
        ScaleAnimation scale = new ScaleAnimation(
                1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(100);
        scale.setRepeatCount(1);
        scale.setRepeatMode(Animation.REVERSE);
        enemySprite.startAnimation(scale);
    }

    private void enemyDefeated() {
        currentUser.setLevel(currentUser.getLevel() + 1);
        currentUser.setTotalEnemiesDefeated(currentUser.getTotalEnemiesDefeated() + 1);
        currentUser.setStoredAttacks(currentUser.getStoredAttacks() + 2);
        userRepository.saveUser(currentUser);

        Toast.makeText(this,
                "ПЕРЕМОГА! Рівень підвищено до " + currentUser.getLevel() + "! +2 удари!",
                Toast.LENGTH_LONG).show();

        attackButton.setEnabled(false);
        new Handler().postDelayed(() -> {
            spawnEnemyForLevel(currentUser.getLevel());
            updateEnemyDisplay();
            updateAttacksCount();
            attackButton.setEnabled(true);
        }, 2000);
    }

    private void updateEnemyDisplay() {
        enemyNameText.setText(currentEnemy.getName());
        hpText.setText(currentEnemy.getHpText());
        hpBar.setMax(currentEnemy.getMaxHp());
        hpBar.setProgress(currentEnemy.getCurrentHp());
        enemySprite.setImageResource(currentEnemy.getImageResId());

        if (currentEnemy.isBoss()) {
            enemyNameText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            enemyNameText.setTextSize(32);
        } else {
            enemyNameText.setTextColor(getResources().getColor(android.R.color.white));
            enemyNameText.setTextSize(28);
        }
    }

    private void updateAttacksCount() {
        String attacksText = "⚔️ УДАРІВ: " + currentUser.getStoredAttacks() + " ⚔️";
        attackButton.setText("⚔️ АТАКУВАТИ (" + currentUser.getStoredAttacks() + ") ⚔️");
        attacksCountText.setText(attacksText);
    }
}