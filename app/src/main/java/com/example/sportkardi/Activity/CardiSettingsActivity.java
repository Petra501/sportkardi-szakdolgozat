package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardiSettingsActivity extends AppCompatActivity {
    private static final String TAG = CardiSettingsActivity.class.getName();
    private static final int KEY = 44;
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private ConstraintLayout backConstraintLayout;
    private CardView darkBlueThemeCardView, theme2CardView, theme3CardView, theme4CardView, theme5CardView, theme6CardView;
    private RadioButton themeDarkBlueRadioButton, theme2RadioButton, theme3RadioButton, theme4RadioButton, theme5RadioButton, theme6RadioButton;
    private SwitchCompat modeSwitch;

    private static final String NIGHT_MODE_KEY = "night_mode";
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_KEY = "current_theme";
    private static final String THEME_DARK_BLUE = "dark_blue";
    private static final String THEME_2 = "theme2";
    private static final String THEME_3 = "theme3";
    private static final String THEME_4 = "theme4";
    private static final String THEME_5 = "theme5";
    private static final String THEME_6 = "theme6";

    private String personalId, name;
    private List<RadioButton> radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Téma betöltése SharedPreferences-ból
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
        setContentView(R.layout.activity_cardi_settings);
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

        backConstraintLayout = findViewById(R.id.backConstraintLayout);

        darkBlueThemeCardView = findViewById(R.id.darkBlueThemeCardView);
        theme2CardView = findViewById(R.id.theme2CardView);
        theme3CardView = findViewById(R.id.theme3CardView);
        theme4CardView = findViewById(R.id.theme4CardView);
        theme5CardView = findViewById(R.id.theme5CardView);
        theme6CardView = findViewById(R.id.theme6CardView);

        themeDarkBlueRadioButton = findViewById(R.id.themeDarkBlueRadioButton);
        theme2RadioButton = findViewById(R.id.theme2RadioButton);
        theme3RadioButton = findViewById(R.id.theme3RadioButton);
        theme4RadioButton = findViewById(R.id.theme4RadioButton);
        theme5RadioButton = findViewById(R.id.theme5RadioButton);
        theme6RadioButton = findViewById(R.id.theme6RadioButton);

        modeSwitch = findViewById(R.id.modeSwitch);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        darkBlueThemeCardView.setOnClickListener(v -> toggleTheme(THEME_DARK_BLUE));
        theme2CardView.setOnClickListener(v -> toggleTheme(THEME_2));
        theme3CardView.setOnClickListener(v -> toggleTheme(THEME_3));
        theme4CardView.setOnClickListener(v -> toggleTheme(THEME_4));
        theme5CardView.setOnClickListener(v -> toggleTheme(THEME_5));
        theme6CardView.setOnClickListener(v -> toggleTheme(THEME_6));

        //visszanyíl - üzenet küldése a főoldalnak, mielőtt bezáródik az Activity, hogy frissítse a témát
        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //főoldal - üzenet küldése a főoldalnak, mielőtt bezáródik az Activity, hogy frissítse a témát
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiSettingsActivity.this, CardiProfileActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(CardiSettingsActivity.this)
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

                            TapTarget.forView(modeSwitch, "Megjelenítési mód", "Itt tudja megadni, hogy az alkalmazás sötét vagy világos témában jelenjen meg.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColorInt(backgroundColor)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(darkBlueThemeCardView, "Téma", "Egy kártyára koppintva kiválaszthatja milyen témában jelenjen meg az alkalmazás.")
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
                            Toast.makeText(CardiSettingsActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        // Készíts egy listát az összes RadioButton-ról
        radioButtons = Arrays.asList(themeDarkBlueRadioButton, theme2RadioButton, theme3RadioButton, theme4RadioButton, theme5RadioButton, theme6RadioButton);

        if (THEME_2.equals(theme)) {
            handleSelection(theme2RadioButton);
        } else if (THEME_3.equals(theme)) {
            handleSelection(theme3RadioButton);
        } else if (THEME_4.equals(theme)) {
            handleSelection(theme4RadioButton);
        } else if (THEME_5.equals(theme)) {
            handleSelection(theme5RadioButton);
        } else if (THEME_6.equals(theme)) {
            handleSelection(theme6RadioButton);
        } else {
            handleSelection(themeDarkBlueRadioButton);
        }

        // Add hozzá a CardView-kra kattintás eseményét
        themeDarkBlueRadioButton.setOnClickListener(v -> {
            handleSelection(themeDarkBlueRadioButton);
            toggleTheme(THEME_DARK_BLUE);
        });

        theme2RadioButton.setOnClickListener(v -> {
            handleSelection(theme2RadioButton);
            toggleTheme(THEME_2);
        });

        theme3RadioButton.setOnClickListener(v -> {
            handleSelection(theme3RadioButton);
            toggleTheme(THEME_3);
        });

        theme4RadioButton.setOnClickListener(v -> {
            handleSelection(theme4RadioButton);
            toggleTheme(THEME_4);
        });

        theme5RadioButton.setOnClickListener(v -> {
            handleSelection(theme5RadioButton);
            toggleTheme(THEME_5);
        });

        theme6RadioButton.setOnClickListener(v -> {
            handleSelection(theme6RadioButton);
            toggleTheme(THEME_6);
        });

        modeSwitch.setChecked(isNightMode);
        modeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(NIGHT_MODE_KEY, isChecked);
            editor.apply();

            String currentTheme = preferences.getString("CURRENT_THEME_KEY", "Theme_Sportkardi_DarkBlue");

            applyTheme(currentTheme, isChecked);

            Intent intent = new Intent("ACTION_CUSTOM_MESSAGE");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            recreate();
        });
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private void applyTheme(String currentTheme, boolean isNightMode) {
        // A sötét módú vagy világos témanevek összeállítása
        String themeSuffix = isNightMode ? "D" : ""; // Ha sötét mód van, "D"-t adunk hozzá
        String themeName = currentTheme + themeSuffix;

        // A megfelelő erőforrás azonosító lekérése
        int themeResId = getResources().getIdentifier(themeName, "style", getPackageName());

        // Ha az adott téma nem található, alapértelmezett téma alkalmazása
        if (themeResId == 0) {
            themeResId = isNightMode
                    ? R.style.Theme_Sportkardi_DarkBlueD // Alapértelmezett sötét téma
                    : R.style.Theme_Sportkardi_DarkBlue; // Alapértelmezett világos téma
        }

        setTheme(themeResId);
    }


    private void handleSelection(RadioButton selectedRadioButton) {
        for (RadioButton radioButton : radioButtons) {
            radioButton.setChecked(radioButton == selectedRadioButton);
        }
    }

    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE); // Alapértelmezett: dark_blue
    }

    private void saveTheme(String theme) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(THEME_KEY, theme);
        editor.apply();
    }

    private void toggleTheme(String theme) {
        Intent intent = new Intent("ACTION_CUSTOM_MESSAGE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        saveTheme(theme);
        recreate();
    }
}