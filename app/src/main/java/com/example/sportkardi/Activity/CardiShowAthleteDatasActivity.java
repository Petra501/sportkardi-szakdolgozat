package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sportkardi.Adapter.CardiBloodPressureViewPagerAdapter;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CardiShowAthleteDatasActivity extends AppCompatActivity {
    private static final String TAG = CardiShowAthleteDatasActivity.class.getName();
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

    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private ConstraintLayout backConstraintLayout;
    private TextView nameTextView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String name, personalId, athlete_personalId, athlete_name;
    private CardiBloodPressureViewPagerAdapter viewPagerAdapter;

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
        setContentView(R.layout.activity_cardi_show_athlete_datas);
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
        athlete_name = getIntent().getStringExtra("athlete_name");
        athlete_personalId = getIntent().getStringExtra("athlete_personalId");

        if (key != 44) {
            finish();
        }

        backConstraintLayout = findViewById(R.id.backConstraintLayout);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(athlete_name);


        viewPagerAdapter = new CardiBloodPressureViewPagerAdapter(this, athlete_personalId, false);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

//        //főoldal
//        homeLinearLayout.setOnClickListener(v -> {
//            Intent intent = new Intent(CardiShowAthleteDatasActivity.this, CardiMainMenuActivity.class);
//            intent.putExtra("KEY", KEY);
//            intent.putExtra("personalId", personalId);
//            intent.putExtra("name", name);
//            startActivity(intent);
//            finish();
//            overridePendingTransition(0, 0);
//        });

        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiShowAthleteDatasActivity.this, CardiProfileActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(CardiShowAthleteDatasActivity.this)
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

                            TapTarget.forView(tabLayout, "Tab választás", "Itt tudja kiválasztani melyik tab tartalmát szeretné átni az oldalon.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColorInt(backgroundColor)
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
                            Toast.makeText(CardiShowAthleteDatasActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiShowAthleteDatasActivity.this, CardiSettingsActivity.class);
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

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }
}
