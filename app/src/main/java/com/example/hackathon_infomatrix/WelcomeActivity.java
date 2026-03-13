package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    public Button buttonLogin;
    public TextView registerLink;

    public void initViews() {
        buttonLogin = findViewById(R.id.buttonLoginWelcome);
        registerLink = findViewById(R.id.linkToRegister);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();

        //Перехід на сторінку login
//        buttonLogin.setOnClickListener(v ->  {
//            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
//            finish();
//        });
//
//        //Перехід на сторінку register
//        registerLink.setOnClickListener(v -> {
//            startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
//            finish();
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Якщо користувач вже увійшов — переходимо на головну сторінку
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish(); // Закриваємо WelcomeActivity
        }
    }
}

