package com.example.sportkardi.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private static final String PREFERENCE = MainActivity.class.getPackage().toString();
    private static final int KEY = 44;

    private EditText personalIdEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        personalIdEditText = findViewById(R.id.personalIdEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);

        if (personalIdEditText != null) {
            personalIdEditText.addTextChangedListener(new TextWatcher() {
                private boolean isUpdating = false;
                private final int MAX_LENGTH = 9;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isUpdating) return;

                    isUpdating = true;

                    String cleanString = s.toString().replaceAll("\\s", "");

                    if (cleanString.length() > MAX_LENGTH) {
                        cleanString = cleanString.substring(0, MAX_LENGTH);
                    }

                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < cleanString.length(); i++) {
                        if (i > 0 && i % 3 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(cleanString.charAt(i));
                    }

                    personalIdEditText.setText(formatted.toString());
                    personalIdEditText.setSelection(formatted.length());

                    isUpdating = false;
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(personalIdEditText.getText().toString())) {
                    personalIdEditText.setError("A mező kitöltése kötelező!");
                    personalIdEditText.requestFocus();
                } else if (personalIdEditText.getText().length() < 11) {
                    personalIdEditText.setError("A TAJ-szám 9 jegyű!");
                    personalIdEditText.requestFocus();
                } else if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    passwordEditText.setError("A mező kitöltése kötelező!");
                    passwordEditText.requestFocus();
                } else {
                    String personalId = personalIdEditText.getText().toString().replace(" ", "");
                    String password = passwordEditText.getText().toString();

                    loginUser(personalId, password);
                }
            }
        });
    }

    private void loginUser(String personalId, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                            String storedPassword = userDoc.getString("password");
                            String userName = userDoc.getString("name");
                            String gender = userDoc.getString("gender");
                            boolean admin = Boolean.TRUE.equals(userDoc.getBoolean("admin"));

                            String hashedPassword = hashPassword(password);

                            if (hashedPassword != null && hashedPassword.equals(storedPassword)) {
                                checkUserDataSheet(personalId, userName, password, gender, admin);
                            } else {
                                Toast.makeText(MainActivity.this, "Helytelen jelszó!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Érvénytelen TAJ-szám!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Hiba történt az adatbázis lekérdezése közben", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserDataSheet(String personalId, String name, String password, String gender, boolean admin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Sportolók")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            if (password.equals(personalId) && admin) {
                                Intent intent = new Intent(this, CardiIntroActivity.class);
                                intent.putExtra("KEY", KEY);
                                intent.putExtra("personalId", personalId);
                                intent.putExtra("name", name);
                                intent.putExtra("gender", gender);
                                startActivity(intent);
                                finish();
                            } else if (!password.equals(personalId) && admin) {
                                Intent intent = new Intent(this, CardiMainMenuActivity.class);
                                intent.putExtra("KEY", KEY);
                                intent.putExtra("personalId", personalId);
                                intent.putExtra("name", name);
                                intent.putExtra("gender", gender);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(this, PatientIntroActivity.class);
                                intent.putExtra("KEY", KEY);
                                intent.putExtra("personalId", personalId);
                                intent.putExtra("name", name);
                                intent.putExtra("gender", gender);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(this, PatientMainMenuActivity.class);
                            intent.putExtra("KEY", KEY);
                            intent.putExtra("personalId", personalId);
                            intent.putExtra("name", name);
                            intent.putExtra("gender", gender);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Hiba történt az adatbázis lekérdezése közben", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}