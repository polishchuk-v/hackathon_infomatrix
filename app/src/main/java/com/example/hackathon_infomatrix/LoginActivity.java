/*package com.example.hackathon_infomatrix;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private boolean isPasswordInVisible = false;
    private ImageView toggleImageView;
    private TextView forgotPassword;
    private Button buttonSignIn;
    private TextView linkToRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    public void initViews() {
        passwordEditText = findViewById(R.id.editTextLoginPassword);
        toggleImageView = findViewById(R.id.imageViewToggleLoginPassword);
        forgotPassword = findViewById(R.id.linkToForgotPassword);
        buttonSignIn = findViewById(R.id.buttonLoginSignIn);
        linkToRegister = findViewById(R.id.linkToLoginRegister);
        emailEditText = findViewById(R.id.editTextLoginEmail);
        progressBar = findViewById(R.id.registerProgressBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        mAuth = FirebaseAuth.getInstance();

        String emailFromRecovery = getIntent().getStringExtra("email");
        if (emailFromRecovery != null) {
            emailEditText.setText(emailFromRecovery);
        }

        toggleImageView.setOnClickListener(v -> {
            // Збереження шрифту перед натисканням на "око"
            Typeface typeface = passwordEditText.getTypeface();

            if (isPasswordInVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleImageView.setImageResource(R.drawable.ic_eye_off);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleImageView.setImageResource(R.drawable.ic_eye);
            }

            // Повернення шрифту після взаємодії з "оком"
            passwordEditText.setTypeface(typeface);

            passwordEditText.setSelection(passwordEditText.length());

            // Позиціюємо курсор в кінець тексту
            isPasswordInVisible = !isPasswordInVisible;
        });

        forgotPassword.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });

        buttonSignIn.setOnClickListener(v -> {
            if (progressBar.getVisibility() == View.VISIBLE) return; // запобігає дублюванню

            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Enter the correct email address");
                emailEditText.requestFocus();
                return;
            }

            if (password.isEmpty() || password.length() < 6) {
                passwordEditText.setError("The password must be at least 6 characters long");
                passwordEditText.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            buttonSignIn.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        buttonSignIn.setEnabled(true);

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(LoginActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Toast.makeText(LoginActivity.this, "Вхід успішний", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Помилка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        linkToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}
*/