package com.example.hackathon_infomatrix;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ініціалізація
        textViewWelcome = findViewById(R.id.textViewWelcome);
        mAuth = FirebaseAuth.getInstance();

        // Отримуємо поточного користувача
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Якщо користувач залогінений - показуємо його email
            String email = currentUser.getEmail();
            textViewWelcome.setText("Ласкаво просимо!\n" + email);
        } else {
            // Якщо користувача немає - повідомляємо про це
            textViewWelcome.setText("Користувач не знайдений");
        }
    }
}