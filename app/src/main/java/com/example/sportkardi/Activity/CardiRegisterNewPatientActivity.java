package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.Model.User;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class CardiRegisterNewPatientActivity extends AppCompatActivity {
    private static final String TAG = CardiRegisterNewPatientActivity.class.getName();
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

    private ConstraintLayout backConstraintLayout;
    private AutoCompleteTextView nameTextView, genderTextView, personalIdTextView, birthYearTextView, phoneNumberTextView;
    private TextInputLayout nameInputLayout, genderInputLayout, personalIdInputLayout, birthYearInputLayout, phoneNumberInputLayout;
    private Button addNewPatientButton;
    private Switch isDoctorSwitch;
    private LinearLayout dataLinearLayout, homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private ScrollView scrollView;

    private String personalId, name;
    private boolean isDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String theme = getSavedTheme();
        boolean isNightMode = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean(NIGHT_MODE_KEY, false);
        String themeSuffix = isNightMode ? "D" : "";
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
        setContentView(R.layout.activity_cardi_register_new_patient);
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
                colorResId = isNightMode ? R.color.wave_3_d: R.color.wave_3;
            } else if (THEME_4.equals(theme)) {
                colorResId = isNightMode ? R.color.wave_4_d: R.color.wave_4;
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

        scrollView = findViewById(R.id.scrollView);

        backConstraintLayout = findViewById(R.id.backConstraintLayout);

        nameTextView = findViewById(R.id.nameTextView);
        genderTextView = findViewById(R.id.genderTextView);
        personalIdTextView = findViewById(R.id.personalIdTextView);
        birthYearTextView = findViewById(R.id.birthYearTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        isDoctorSwitch = findViewById(R.id.isDoctorSwitch);

        nameInputLayout = findViewById(R.id.nameInputLayout);
        genderInputLayout = findViewById(R.id.genderInputLayout);
        personalIdInputLayout = findViewById(R.id.personalIdInputLayout);
        birthYearInputLayout = findViewById(R.id.birthYearInputLayout);
        phoneNumberInputLayout = findViewById(R.id.phoneNumberInputLayout);

        dataLinearLayout = findViewById(R.id.dataLinearLayout);
        addNewPatientButton = findViewById(R.id.addNewPatientButton);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, 
                R.array.gender,
                R.layout.dropdown_item
        );
        genderTextView.setAdapter(adapter);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = typedValue.data;
        genderTextView.setDropDownBackgroundDrawable(new ColorDrawable(color));

        isDoctor = false;

        personalIdTextView.addTextChangedListener(new TextWatcher() {
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

                personalIdTextView.setText(formatted.toString());
                personalIdTextView.setSelection(formatted.length());

                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addNewPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genderTextView.setOnItemClickListener((parent, view, position, id) -> {
                    genderTextView.setText(adapter.getItem(position));
                });
                
                String gender = genderTextView.getText().toString();

                if (TextUtils.isEmpty(nameTextView.getText().toString())) {
                    nameInputLayout.setError("A mező kitöltése kötelező!");
                } else if (TextUtils.isEmpty(personalIdTextView.getText().toString())) {
                    personalIdInputLayout.setError("A mező kitöltése kötelező!");
                } else if (personalIdTextView.getText().length() < 11) {
                    personalIdInputLayout.setError("A TAJ-szám 9 jegyű!");
                } else if (TextUtils.isEmpty(birthYearTextView.getText().toString())) {
                    birthYearInputLayout.setError("A mező kitöltése kötelező!");
                } else if (birthYearTextView.getText().length() < 4 || Integer.parseInt(birthYearTextView.getText().toString()) < 1900 || Integer.parseInt(birthYearTextView.getText().toString()) > Calendar.getInstance().get(Calendar.YEAR)) {
                    birthYearInputLayout.setError("Érvénytelen születési évszám!");
                } else if (TextUtils.isEmpty(phoneNumberTextView.getText().toString())) {
                    phoneNumberInputLayout.setError("A mező kitöltése kötelező!");
                } else if (!(gender.equals("férfi") || gender.equals("nő"))) {
                    genderInputLayout.setError("A nem értéke nem megfelelő!");
                } else {
                    String name = nameTextView.getText().toString();
                    String personalId = personalIdTextView.getText().toString().replace(" ", "");
                    int birthYear = Integer.parseInt(birthYearTextView.getText().toString());
                    String phoneNumber = phoneNumberTextView.getText().toString();

                    String password = personalIdTextView.getText().toString().replace(" ", "");

                    Boolean admin = false;
                    
                    if (isDoctorSwitch.isChecked() && !isDoctor) {
                        showWarningDialog(name, personalId, birthYear, phoneNumber, gender, password, admin);
                    } else {
                        registerUser(name, personalId, birthYear, phoneNumber, gender, password, admin);
                    }
                }
            }
        });

        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //főoldal
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiRegisterNewPatientActivity.this, CardiProfileActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(CardiRegisterNewPatientActivity.this)
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

                            TapTarget.forView(addNewPatientButton, "Mentés", "Ha minden inputmező helyesen lett kitöltve, ezzel a gombbal létrehozhatja az új felhasználót.")
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

                            TapTarget.forView(profileLinearLayout, "Profil", "Itt tudja megtekineni a profiladatokat és személyes információkat.")
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
                        public void onSequenceFinish() {
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            Toast.makeText(CardiRegisterNewPatientActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiRegisterNewPatientActivity.this, CardiSettingsActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    // Egészségügyi dolgozó beállításra vonatkozó figyelmeztető dialog
    private void showWarningDialog(String name, String personalId, int birthYear, String phoneNumber, String gender, String password, Boolean admin) {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(CardiRegisterNewPatientActivity.this).inflate(R.layout.dialog_delete_appointments_warning, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        TextView descTextView = view.findViewById(R.id.descTextView);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        descTextView.setText("Biztosan egészségügyi dolgozónak szeretnéd állítani a felhasználót? Ha megadod neki az engedélyt, látni fogja az összes sportoló adatlapját, tud időpontot és javaslatot adni a kezelésükkel kapcsolatban.");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CardiRegisterNewPatientActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.setEnabled(false);
        doneButton.setBackgroundTintList(ContextCompat.getColorStateList(doneButton.getContext(), R.color.grey));
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                doneButton.setText("Igen (" + millisUntilFinished / 1000 + ")");
            }

            public void onFinish() {
                doneButton.setText("Igen");
                doneButton.setEnabled(true);
                doneButton.setBackgroundTintList(ContextCompat.getColorStateList(doneButton.getContext(), R.color.red));
            }
        }.start();

        cancelButton.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(name, personalId, birthYear, phoneNumber, gender, password, true);
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }
    

    // Sikeres regisztrálás dialog
    private void showSuccessDialog(){
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(CardiRegisterNewPatientActivity.this).inflate(R.layout.popup_successful_request, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView successTextView = view.findViewById(R.id.successTextView);
        TextView descTextView = view.findViewById(R.id.descTextView);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        successTextView.setText("Sikeres mentés!");
        descTextView.setText("Az új felhasználó rögzítésre került a rendszerben.");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CardiRegisterNewPatientActivity.this);
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextView.setText("");
                nameTextView.setFocusable(false);
                genderTextView.setText("");
                genderTextView.setFocusable(false);
                personalIdTextView.setText("");
                personalIdTextView.setFocusable(false);
                birthYearTextView.setText("");
                birthYearTextView.setFocusable(false);
                phoneNumberTextView.setText("");
                phoneNumberTextView.setFocusable(false);
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setOnDismissListener(dialog -> {
            nameTextView.setText("");
            nameTextView.setFocusable(false);
            genderTextView.setText("");
            genderTextView.setFocusable(false);
            personalIdTextView.setText("");
            personalIdTextView.setFocusable(false);
            birthYearTextView.setText("");
            birthYearTextView.setFocusable(false);
            phoneNumberTextView.setText("");
            phoneNumberTextView.setFocusable(false);
        });

        alertDialog.show();
    }

    // TAJ-szám leellenőrzése
    private void registerUser(String name, String personalId, int birthYear, String phoneNumber, String gender, String password, Boolean admin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            personalIdInputLayout.setError("Ez a TAJ-szám már regisztrálva van!");
                        } else {
                            addUserToDatabase(name, personalId, birthYear, phoneNumber, gender, password, admin);
                        }
                    } else {
                        Toast.makeText(CardiRegisterNewPatientActivity.this, "Hiba az adatbázisban", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Felhasználó hozzáadása az adatbázishoz
    private void addUserToDatabase(String name, String personalId, int birthYear, String phoneNumber, String gender, String password, Boolean admin) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Jelszó hashelése sikertelen", Toast.LENGTH_LONG).show();
            return;
        }

        User user = new User(
                personalId,
                name,
                birthYear,
                phoneNumber,
                gender,
                hashedPassword,
                admin,
                false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").add(user)
                .addOnSuccessListener(documentReference -> {
                    showSuccessDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CardiRegisterNewPatientActivity.this, "Hiba a regisztráció során", Toast.LENGTH_LONG).show();
                });
    }

    // Jelszó titkosítása
    private String hashPassword(String password) {
        try {
            // SHA-256 algoritmus inicializálása
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Byte tömb konvertálása hexadecimális stringgé
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