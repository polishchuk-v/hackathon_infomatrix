package com.example.hackathon.Battle;

import com.example.hackathon.Enemy.classEnemy;
import com.example.hackathon.data.repository.UserRepository;
import com.example.hackathon.ui.main.MainActivity;
import com.example.hackathon_infomatrix.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BattleActivity extends AppCompatActivity  {
    private TextView enemyNameText;
    private TextView hpText;
    private TextView attacksCountText;
    private ProgressBar hpBar;
    private ImageView enemySprite;
    private Button attackButton;
    private Button backButton;
    private classEnemy currEnemy;

    private UserRepository userRepository;
    private int availableAttacks;
    private int killCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        userRepository = new UserRepository(this);
        availableAttacks = userRepository.getUser().getStoredAttacks();

        enemyNameText = findViewById(R.id.enemyName);
        hpText = findViewById(R.id.hpText);
        hpBar = findViewById(R.id.hpBar);
        enemySprite = findViewById(R.id.enemySprite);
        attackButton = findViewById(R.id.attackButton);
        backButton = findViewById(R.id.backButton);
        attacksCountText = findViewById(R.id.attacksCountText);

        spawnNextEnemy();
        updateAttacksDisplay();

        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attackEnemy();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void spawnNextEnemy() {
        killCount++;

        switch (killCount) {
            case 1:
                currEnemy = new classEnemy("Гоблін-злодій", 10, R.drawable.goblin1);
                break;
            case 2:
                currEnemy = new classEnemy("Гоблін-воїн", 20, R.drawable.goblin2);
                break;
            case 3:
                currEnemy = new classEnemy("Гоблін-шаман", 30, R.drawable.goblin3);
                break;
            case 4:
                currEnemy = new classEnemy("Гоблін-вождь", 50, R.drawable.goblin4);
                break;
            case 5:
                currEnemy = new classEnemy("Орк", 75, R.drawable.ork1);
                break;
            case 6:
                currEnemy = new classEnemy("Шафтер", 100, R.drawable.shafter1);
                break;
            default:
                int randomHp = 150 + (killCount * 10);
                currEnemy = new classEnemy("Ворог рівня " + killCount, randomHp, R.drawable.goblin1);
                break;
        }

        updateEnemyDisplay();

        Toast.makeText(this, "З'явився: " + currEnemy.getName(), Toast.LENGTH_SHORT).show();
    }

    private void attackEnemy() {
        if (availableAttacks <= 0) {
            Toast.makeText(this, "Недостатньо ударів! Виконуйте завдання!", Toast.LENGTH_LONG).show();
            return;
        }

        int currentHp = currEnemy.getCurrHP();
        int damage = 10;

        int newHp = currentHp - damage;
        if (newHp < 0) newHp = 0;

        currEnemy.setCurrHP(newHp);

        availableAttacks--;
        updateAttacksDisplay();

        userRepository.getUser().setStoredAttacks(availableAttacks);
        userRepository.saveUser(userRepository.getUser());

        updateEnemyDisplay();

        enemySprite.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                .withEndAction(() -> enemySprite.animate().scaleX(1f).scaleY(1f).setDuration(100).start());

        if (newHp == 0) {
            enemyDefeated();
        } else {
            Toast.makeText(this, "Урон: " + damage + " | Залишилось ударів: " + availableAttacks, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEnemyDisplay() {
        enemyNameText.setText(currEnemy.getName());
        hpText.setText(currEnemy.getHPText());
        hpBar.setMax(currEnemy.getMaxHP());
        hpBar.setProgress(currEnemy.getCurrHP());
        enemySprite.setImageResource(currEnemy.getImgResID());
    }

    private void updateAttacksDisplay() {
        if (attacksCountText != null) {
            attacksCountText.setText("⚔️ УДАРІВ: " + availableAttacks + " ⚔️");
        }

        if (attackButton != null) {
            attackButton.setText("⚔️ АТАКУВАТИ (" + availableAttacks + ") ⚔️");
        }
    }

    public void enemyDefeated() {
        enemyNameText.setText("ПЕРЕМОГА!");
        Toast.makeText(this, "Ворог переможений! +10 XP", Toast.LENGTH_LONG).show();

        attackButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnNextEnemy();
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        availableAttacks = userRepository.getUser().getStoredAttacks();
        updateAttacksDisplay();
    }
}