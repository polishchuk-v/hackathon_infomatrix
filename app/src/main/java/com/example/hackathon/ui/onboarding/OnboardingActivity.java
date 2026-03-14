package com.example.hackathon.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hackathon_infomatrix.R;
import com.example.hackathon.data.models.User;
import com.example.hackathon.data.repository.UserRepository;
import com.example.hackathon.ui.main.MainActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OnboardingActivity extends AppCompatActivity {

    private EditText editName, editAge;
    private RadioGroup radioGroupRegion;
    private Button btnStart;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        userRepository = new UserRepository(this);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        radioGroupRegion = findViewById(R.id.radioGroupRegion);
        btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void saveUserData() {
        String name = editName.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Введіть ім'я", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() < 2) {
            Toast.makeText(this, "Ім'я має бути не менше 2 символів", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > 20) {
            Toast.makeText(this, "Ім'я має бути не більше 20 символів", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!name.matches("[а-яА-Яіїєґa-zA-Z\\s]+")) {
            Toast.makeText(this, "Ім'я може містити тільки літери", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.replaceAll("\\s", "").isEmpty()) {
            Toast.makeText(this, "Ім'я не може складатися тільки з пробілів", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(editAge.getText().toString().trim());
            if (age <= 0 || age > 120) {
                Toast.makeText(this, "Введіть коректний вік (1-120)", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введіть вік", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = radioGroupRegion.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Оберіть регіон", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeZone = getTimeZoneFromRadio(selectedId);

        name = formatName(name);

        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setTimeZone(timeZone);
        user.setFirstLaunch(false);
        user.setLastLoginDate(getCurrentDate());

        userRepository.saveUser(user);

        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String formatName(String name) {
        String[] words = name.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return formatted.toString().trim();
    }

    private String getTimeZoneFromRadio(int radioId) {
        if (radioId == R.id.radioKyiv) return "GMT+2";
        if (radioId == R.id.radioWarsaw) return "GMT+1";
        if (radioId == R.id.radioLondon) return "GMT+0";
        if (radioId == R.id.radioNewYork) return "GMT-4";
        if (radioId == R.id.radioTokyo) return "GMT+9";
        return "GMT+2"; // деф Киев стоит
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}