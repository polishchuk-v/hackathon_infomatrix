//package com.example.hackathon_infomatrix;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Patterns;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatButton;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//public class PasswordRecoveryActivity extends AppCompatActivity {
//
//    public AppCompatButton buttonBackSingIn, buttonRecovery;
//    private EditText recoveryEmailEditText;
//    private ProgressBar progressBar;
//
//    private FirebaseAuth mAuth;
//
//    public void initViews() {
//        buttonBackSingIn = findViewById(R.id.buttonBackSingIn);
//        buttonRecovery = findViewById(R.id.buttonRecovery);
//        recoveryEmailEditText = findViewById(R.id.editTextRecoveryEmail);
//        progressBar = findViewById(R.id.registerProgressBar);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_password_recovery);
//        initViews();
//
//        mAuth = FirebaseAuth.getInstance();
//
//        // Повернення email
//        String passedEmail = getIntent().getStringExtra("email");
//        if (passedEmail != null) {
//            recoveryEmailEditText.setText(passedEmail);
//        }
//
//        buttonRecovery.setOnClickListener(v -> {
//            String email = recoveryEmailEditText.getText().toString().trim();
//
//            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                recoveryEmailEditText.setError("Enter the correct email address");
//                recoveryEmailEditText.requestFocus();
//                return;
//            }
//
//            setLoading(true);
//
//            mAuth.sendPasswordResetEmail(email)
//                    .addOnCompleteListener(task -> {
//                        setLoading(false);
//                        if (task.isSuccessful()) {
//                            Toast.makeText(PasswordRecoveryActivity.this, "Лист для скидання пароля надіслано", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
//                            // Передача email назад у LoginActivity
//                            intent.putExtra("email", email);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(PasswordRecoveryActivity.this, "Помилка: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//        });
//
//        buttonBackSingIn.setOnClickListener(view -> {
//            String email = recoveryEmailEditText.getText().toString().trim();
//            Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
//            intent.putExtra("email", email);
//            startActivity(intent);
//            finish();
//        });
//    }
//
//    private void setLoading(boolean isLoading) {
//        if (isLoading) {
//            progressBar.setVisibility(View.VISIBLE);
//            buttonRecovery.setEnabled(false);
//            buttonBackSingIn.setEnabled(false);
//        } else {
//            progressBar.setVisibility(View.GONE);
//            buttonRecovery.setEnabled(true);
//            buttonBackSingIn.setEnabled(true);
//        }
//    }
//}
//
