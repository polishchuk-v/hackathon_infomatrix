package com.example.hackathon.Battle;

import com.example.hackathon.Enemy.classEnemy;
import com.example.hackathon.R;

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
    private ProgressBar hpBar;
    private ImageView enemySprite;
    private Button testKillButton;
    private classEnemy currEnemy;

    private int killCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        enemyNameText = findViewById(R.id.enemyName);
        hpText = findViewById(R.id.hpText);
        hpBar = findViewById(R.id.hpBar);
        enemySprite = findViewById(R.id.enemySprite);
        testKillButton = findViewById(R.id.testKillButton);

        spawnNextEnemy();

        testKillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attackEnemy();
            }
        });
    }

    private void spawnNextEnemy() {
        killCount++;

        switch (killCount) {
            case 1:
                currEnemy = new classEnemy("Аутист", 10, R.drawable.goblin1);
                break;
            case 2:
                currEnemy = new classEnemy("Пидорас", 20, R.drawable.goblin2);
                break;
            case 3:
                currEnemy = new classEnemy("Хуеглот", 30, R.drawable.goblin3);
                break;
            case 4:
                currEnemy = new classEnemy("Уебан", 50, R.drawable.goblin4);
                break;
            case 5:
                currEnemy = new classEnemy("Пропан", 75, R.drawable.ork1);
                break;
            case 6:
                currEnemy = new classEnemy("Долбоебан", 100, R.drawable.shafter1);
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
        int currentHp = currEnemy.getCurrHP();
        int damage = 10;

        int newHp = currentHp - damage;
        if (newHp < 0) newHp = 0;

        currEnemy.setCurrHP(newHp);
        updateEnemyDisplay();

        enemySprite.animate().scaleX(1.2f).scaleY(1.2f).setDuration(100)
                .withEndAction(() -> enemySprite.animate().scaleX(1f).scaleY(1f).setDuration(100).start());

        if (newHp == 0) {
            enemyDefeated();
        } else {
            Toast.makeText(this, "Урон: " + damage, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEnemyDisplay() {
        enemyNameText.setText(currEnemy.getName());
        hpText.setText(currEnemy.getHPText());
        hpBar.setMax(currEnemy.getMaxHP());
        hpBar.setProgress(currEnemy.getCurrHP());
        enemySprite.setImageResource(currEnemy.getImgResID());
    }

    public void enemyDefeated() {
        enemyNameText.setText("ПЕРЕМОГА!");
        Toast.makeText(this, "Ворог переможений! +1 XP", Toast.LENGTH_LONG).show();

        testKillButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                spawnNextEnemy();
            }
        }, 1500);
    }
}