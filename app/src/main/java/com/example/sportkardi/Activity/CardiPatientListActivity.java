package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Adapter.PatientListAdapter;
import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CardiPatientListActivity extends AppCompatActivity {
    private static final String TAG = CardiPatientListActivity.class.getName();
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
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private RecyclerView patientListRecyclerView;
    private SearchView searchView;

    private String personalId, name;
    private PatientListAdapter adapter;
    private ArrayList<Athlete> athletes = new ArrayList<>();

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
        setContentView(R.layout.activity_cardi_patient_list);
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
        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        patientListRecyclerView = findViewById(R.id.patientListRecyclerView);
        searchView = findViewById(R.id.searchView);

        searchView.clearFocus();
        patientListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        patientListRecyclerView.setHasFixedSize(true);

        adapter = new PatientListAdapter(this, athletes, athlete -> {
            Intent intent = new Intent(CardiPatientListActivity.this, CardiShowAthleteDatasActivity.class);
            intent.putExtra("KEY", 44);
            intent.putExtra("personalId", personalId);
            intent.putExtra("athlete_personalId", athlete.getPersonalId());
            intent.putExtra("athlete_name", athlete.getName());
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        patientListRecyclerView.setAdapter(adapter);
        patientListRecyclerView.setClickable(true);

        getPatients(athletes);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return false;
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
            Intent intent = new Intent(CardiPatientListActivity.this, CardiProfileActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(CardiPatientListActivity.this)
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

                            TapTarget.forView(searchView, "Keresés", "Itt tud rákeresni sportolóra TAJ-szám vagy név alapján.")
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
                            Toast.makeText(CardiPatientListActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiPatientListActivity.this, CardiSettingsActivity.class);
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

    // Keresés a kereső mezőbe írtak alapján
    private void fileList(String newText) {
        ArrayList<Athlete> filteredList = new ArrayList<>();
        for (Athlete athlete : athletes) {
            if (athlete.getName().toLowerCase().contains(newText.toLowerCase()) || athlete.getPersonalId().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(athlete);
            }
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
        }
    }

    // Sportolók listájának megkapása
    private void getPatients(ArrayList<Athlete> athletes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("Sportolók");

        itemsRef.orderBy("timestamp", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    athletes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Athlete athlete = document.toObject(Athlete.class);
                        athletes.add(athlete);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                });
    }

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }
}