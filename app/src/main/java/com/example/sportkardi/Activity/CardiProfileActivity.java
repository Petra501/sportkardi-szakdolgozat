package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class CardiProfileActivity extends AppCompatActivity {
    private static final String TAG = CardiProfileActivity.class.getName();
    private static final int KEY = 44;
    private static final String NIGHT_MODE_KEY = "night_mode";
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_KEY = "current_theme";
    private static final String THEME_DARK_BLUE = "dark_blue";
    private static final String THEME_2 = "theme2";
    private static final String THEME_3 = "theme3";
    private static final String THEME_4 = "theme4";
    private static final String THEME_5 = "theme5";
    private static final String THEME_6 = "theme6";

    private TextView nameTextView, genderTextView, personalIdTextView, birthYearTextView, phoneNumberTextView;
    private ImageView changeNameImageView, changeBirthYearImageView, changePhoneNumberImageView, changePasswordImageView;
    private ConstraintLayout backConstraintLayout, profileConstraintLayout;
    private LinearLayout dataLinearLayout, homeLinearLayout, settingsLinearLayout, infoLinearLayout;

    private String personalId, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String theme = getSavedTheme();
        boolean isNightMode = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(NIGHT_MODE_KEY, false);
        int themeResId;

        if (THEME_2.equals(theme)) {
            themeResId = isNightMode ? R.style.Theme2D : R.style.Theme2;
        } else if (THEME_3.equals(theme)) {
            themeResId = isNightMode ? R.style.Theme3D : R.style.Theme3;
        } else if (THEME_4.equals(theme)) {
            themeResId = isNightMode ? R.style.Theme4D : R.style.Theme4;
        } else if (THEME_5.equals(theme)) {
            themeResId = isNightMode ? R.style.Theme5D : R.style.Theme5;
        } else if (THEME_6.equals(theme)) {
            themeResId = isNightMode ? R.style.Theme6D : R.style.Theme6;
        } else {
            themeResId = isNightMode ? R.style.Theme_Sportkardi_DarkBlueD : R.style.Theme_Sportkardi_DarkBlue;
        }

        setTheme(themeResId);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cardi_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorResId;

            if (THEME_2.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_2_d : R.color.wave_2;
            } else if (THEME_3.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_3_d : R.color.wave_3;
            } else if (THEME_4.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_4_d : R.color.wave_4;
            } else if (THEME_5.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_5_d : R.color.wave_5;
            } else if (THEME_6.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_6_d : R.color.wave_6;
            } else {
                colorResId = isNightMode ? R.color.wave_dark_blue_d : R.color.wave_dark_blue;
            }

            getWindow().setStatusBarColor(getResources().getColor(colorResId));
        }

        int key = getIntent().getIntExtra("KEY", 0);
        personalId = getIntent().getStringExtra("personalId");
        name = getIntent().getStringExtra("name");

        if (key != 44) {
            finish();
        }

        nameTextView = findViewById(R.id.nameTextView);
        genderTextView = findViewById(R.id.genderTextView);
        personalIdTextView = findViewById(R.id.personalIdTextView);
        birthYearTextView = findViewById(R.id.birthYearTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        dataLinearLayout = findViewById(R.id.dataLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        profileConstraintLayout = findViewById(R.id.profileConstraintLayout);
        changeNameImageView = findViewById(R.id.changeNameImageView);
        changeBirthYearImageView = findViewById(R.id.changeBirthYearImageView);
        changePhoneNumberImageView = findViewById(R.id.changePhoneNumberImageView);
        changePasswordImageView = findViewById(R.id.changePasswordImageView);

        backConstraintLayout = findViewById(R.id.backConstraintLayout);
        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);

        getUserData(personalId);

        changeNameImageView.setOnClickListener(v -> showChangeDataPopup("név"));
        changeBirthYearImageView.setOnClickListener(v -> showChangeDataPopup("születési év"));
        changePhoneNumberImageView.setOnClickListener(v -> showChangeDataPopup("telefonszám"));
        changePasswordImageView.setOnClickListener(v -> showChangePasswordPopup());

        //főoldal
        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiProfileActivity.this, CardiSettingsActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        //info megjelenítése
        infoLinearLayout.setOnClickListener(v -> {
            Context context = this;
            int backgroundColor = getThemeColor(context, androidx.appcompat.R.attr.colorPrimaryDark);
            int textColor = getThemeColor(context, com.google.android.material.R.attr.backgroundColor);

            TapTargetSequence sequence = new TapTargetSequence(CardiProfileActivity.this)
                    .targets(
                            TapTarget.forView(backConstraintLayout, "Vissza", "Itt tud visszalépni az előző oldalra.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(changeNameImageView, "Szerkesztés", "Erre az ikonra koppintva tudja szerkeszeni az adatokat.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(homeLinearLayout, "Főoldal", "Itt tud visszamenni a főoldalra.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(settingsLinearLayout, "Beállítások", "Itt tudja az alkalmazás megjelenítését testreszabni.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false)
                    )

                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {}

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {}

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            Toast.makeText(CardiProfileActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private void getUserData(String personalId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String name = document.getString("name");
                        String gender = document.getString("gender");
                        String birthYear = document.contains("birthYear") ? String.valueOf(document.getLong("birthYear")) : "";
                        String phoneNumber = document.getString("phone");
                        Boolean admin = document.getBoolean("admin");

                        nameTextView.setText(name != null ? name : "N/A");
                        genderTextView.setText(gender != null ? gender : "N/A");
                        birthYearTextView.setText(birthYear);
                        phoneNumberTextView.setText(phoneNumber != null ? phoneNumber : "N/A");
                        personalIdTextView.setText(personalId);

                        if (gender != null && gender.equalsIgnoreCase("férfi")) {
                            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_male_doctor);
                        } else if (gender != null && gender.equalsIgnoreCase("nő")) {
                            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_female_doctor);
                        }
                    } else {
                        Log.e(TAG, "Nem található felhasználó ezzel a personalId-vel.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Hiba a Firestore lekérés során: ", e));
    }

    private void showChangeDataPopup(String data) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_profile_change_data, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        showDimBackground();
        popupWindow.setOnDismissListener(this::hideDimBackground);

        AutoCompleteTextView autoCompleteTextView = popupView.findViewById(R.id.autoCompleteTextView);
        TextInputLayout textInputLayout = popupView.findViewById(R.id.textInputLayout);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button saveButton = popupView.findViewById(R.id.saveButton);
        String hint = "Új " + data;

        if (data.equals("születési év")) {
            autoCompleteTextView.setInputType(InputType.TYPE_CLASS_NUMBER);

            InputFilter[] filters = new InputFilter[]{
                    new InputFilter.LengthFilter(4)
            };
            autoCompleteTextView.setFilters(filters);
        } else if (data.equals("telefonszám")) {
            autoCompleteTextView.setInputType(InputType.TYPE_CLASS_PHONE);
        }


        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        textInputLayout.setHint(hint);

        saveButton.setOnClickListener(v -> {
            String newData = autoCompleteTextView.getText().toString().trim();

            switch (data) {
                case "név":
                    if (newData.isEmpty()) {
                        textInputLayout.setError("A mező kitöltése kötelező!");
                    } else {
                        updateProfileName(newData);
                        popupWindow.dismiss();
                    }
                    break;
                case "születési év":
                    if (newData.isEmpty()) {
                        textInputLayout.setError("A mező kitöltése kötelező!");
                    } else if (newData.length() < 4 || Integer.parseInt(newData) < 1900 || Integer.parseInt(newData) > Calendar.getInstance().get(Calendar.YEAR)) {
                        textInputLayout.setError("Érvénytelen születési évszám!");
                    } else {
                        updateProfileBirthYear(Integer.parseInt(newData));
                        popupWindow.dismiss();
                    }
                    break;
                case "telefonszám":
                    if (newData.isEmpty()) {
                        textInputLayout.setError("A mező kitöltése kötelező!");
                    } else {
                        updateProfilePhoneNumber(newData);
                        popupWindow.dismiss();
                    }
                    break;
            }

        });

        popupWindow.showAtLocation(dataLinearLayout, Gravity.CENTER, 0, 0);
    }

    private void showChangePasswordPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_profile_change_password, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        showDimBackground();
        popupWindow.setOnDismissListener(this::hideDimBackground);

        AutoCompleteTextView passwordAutoCompleteTextView = popupView.findViewById(R.id.passwordAutoCompleteTextView);
        AutoCompleteTextView passwordAgainAutoCompleteTextView = popupView.findViewById(R.id.passwordAgainAutoCompleteTextView);
        TextInputLayout passwordInputLayout = popupView.findViewById(R.id.passwordInputLayout);
        TextInputLayout passwordAgainInputLayout = popupView.findViewById(R.id.passwordAgainInputLayout);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button saveButton = popupView.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        passwordInputLayout.setHint("Új jelszó");
        passwordAgainInputLayout.setHint("Jelszó újra");

        saveButton.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }

            private void validateInputs() {
                String password = passwordAutoCompleteTextView.getText().toString().trim();
                String confirmPassword = passwordAgainAutoCompleteTextView.getText().toString().trim();

                boolean isPasswordValid = true;

                // Validate password field
                if (password.length() < 8) {
                    passwordInputLayout.setError("A jelszónak minimum 8 karakternek kell lennie!");
                    isPasswordValid = false;
                } else if (password.equals(personalId)) {
                    passwordInputLayout.setError("Nem lehet a TAJ-szám a jelszó!");
                    isPasswordValid = false;
                } else {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setErrorEnabled(false);
                }

                if (!confirmPassword.equals(password)) {
                    passwordAgainInputLayout.setError("A két jelszó nem egyezik!");
                    isPasswordValid = false;
                } else if (confirmPassword.length() < 8) {
                    passwordAgainInputLayout.setError("A jelszónak minimum 8 karakternek kell lennie!");
                    isPasswordValid = false;
                } else if (confirmPassword.equals(personalId)) {
                    passwordAgainInputLayout.setError("Nem lehet a TAJ-szám a jelszó!");
                    isPasswordValid = false;
                } else {
                    passwordAgainInputLayout.setError(null);
                    passwordAgainInputLayout.setErrorEnabled(false);
                }

                saveButton.setEnabled(isPasswordValid && !password.isEmpty() && !confirmPassword.isEmpty());
            }
        };

        passwordAutoCompleteTextView.addTextChangedListener(textWatcher);
        passwordAgainAutoCompleteTextView.addTextChangedListener(textWatcher);

        saveButton.setOnClickListener(v -> {
            String newData = passwordAutoCompleteTextView.getText().toString().trim();
            updateProfilePassword(newData);
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(dataLinearLayout, Gravity.CENTER, 0, 0);
    }

    // Halványító réteg a popup mőgé
    private void showDimBackground() {
        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        dimView.setBackgroundColor(getResources().getColor(R.color.black)); // Egy félátlátszó szín
        dimView.setAlpha(0.5f); // A halványítás mértéke
        dimView.setTag("dim_view"); // Azonosító a réteghez

        // DimView hozzáadása az Activity root nézetéhez
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(dimView);
    }

    // Sikeres mósodítás dialog
    private void showSuccessfulDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(CardiProfileActivity.this).inflate(R.layout.dialog_successful_delete, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView successTextView = view.findViewById(R.id.successTextView);
        TextView descTextView = view.findViewById(R.id.descTextView);

        successTextView.setText("Sikeres módosítás!");
        descTextView.setText("Az adat frissítve lett a rendszerben.");

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiProfileActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    // Halványító réteg eltüntetése a popup eltűnésével
    private void hideDimBackground() {
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        View dimView = rootView.findViewWithTag("dim_view");
        if (dimView != null) {
            rootView.removeView(dimView);
        }
    }

    private void updateProfileName(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String dbName = document.getString("name");

                        if (!name.equals(dbName)) {
                            db.collection("users").document(document.getId())
                                    .update("name", name)
                                    .addOnSuccessListener(aVoid -> {
                                        showSuccessfulDialog();
                                        nameTextView.setText(name);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CardiProfileActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(CardiProfileActivity.this, "Nem történt módosítás", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CardiProfileActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CardiProfileActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
    }

    private void updateProfileBirthYear(int birthYear) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        int dbBirthYear = document.contains("birthYear") ? document.getLong("birthYear").intValue() : 0;

                        if (birthYear != dbBirthYear) {
                            db.collection("users").document(document.getId())
                                    .update("birthYear", birthYear)
                                    .addOnSuccessListener(aVoid -> {
                                        showSuccessfulDialog();
                                        birthYearTextView.setText(String.valueOf(birthYear));
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CardiProfileActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(CardiProfileActivity.this, "Nem történt módosítás", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CardiProfileActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CardiProfileActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
    }

    private void updateProfilePhoneNumber(String phoneNumber) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String dbPhone = document.getString("phone");

                        if (!phoneNumber.equals(dbPhone)) {
                            db.collection("users").document(document.getId())
                                    .update("phone", phoneNumber)
                                    .addOnSuccessListener(aVoid -> {
                                        showSuccessfulDialog();
                                        phoneNumberTextView.setText(phoneNumber);
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CardiProfileActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(CardiProfileActivity.this, "Nem történt módosítás", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CardiProfileActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CardiProfileActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
    }

    private void updateProfilePassword(String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String dbPassword = document.getString("password");
                        String hashedPassword;

                        hashedPassword = hashPassword(password);

                        if (!Objects.equals(hashedPassword, "") && !hashedPassword.equals(dbPassword)) {
                            db.collection("users").document(document.getId())
                                    .update("password", hashedPassword)
                                    .addOnSuccessListener(aVoid -> {
                                        showSuccessfulDialog();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CardiProfileActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(CardiProfileActivity.this, "Nincs frissítendő adat", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CardiProfileActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(CardiProfileActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
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

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }
}