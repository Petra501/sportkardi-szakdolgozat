package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.regex.Pattern;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PatientDataSheetActivity extends AppCompatActivity {
    private static final String TAG = PatientDataSheetActivity.class.getName();
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

    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout, dataSheetLinearLayout;
    private ConstraintLayout backConstraintLayout, editDataConstraintLayout;
    private FloatingActionButton addBloodPressureFAB;
    private TextView heightTV, weightTV, periodTV, sportTV, weeklyWorkoutTV, sportAgeTV, sportTypeTV, complaintsTV, habitsTV, medicinesTV;

    private String personalId, name;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

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
        setContentView(R.layout.activity_patient_data_sheet);
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
        editDataConstraintLayout = findViewById(R.id.editDataConstraintLayout);
        dataSheetLinearLayout = findViewById(R.id.dataSheetLinearLayout);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);
        addBloodPressureFAB = findViewById(R.id.addBloodPressureFAB);

        heightTV = findViewById(R.id.heightTV);
        weightTV = findViewById(R.id.weightTV);
        periodTV = findViewById(R.id.periodTV);
        sportTV = findViewById(R.id.sportTV);
        weeklyWorkoutTV = findViewById(R.id.weeklyWorkoutTV);
        sportAgeTV = findViewById(R.id.sportAgeTV);
        sportTypeTV = findViewById(R.id.sportTypeTV);
        complaintsTV = findViewById(R.id.complaintsTV);
        habitsTV = findViewById(R.id.habitsTV);
        medicinesTV = findViewById(R.id.medicineTV);

        loadAthleteData();

        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        editDataConstraintLayout.setOnClickListener(v -> {
            showEditDataDialog();
        });

        //főoldal
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDataSheetActivity.this, PatientProfileActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        // új mérés activity megnyitása
        addBloodPressureFAB.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDataSheetActivity.this, PatientBloodPressureActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        infoLinearLayout.setOnClickListener(v -> {
            Context context = this;
            int backgroundColor = getThemeColor(context, androidx.appcompat.R.attr.colorPrimaryDark);
            int textColor = getThemeColor(context, com.google.android.material.R.attr.backgroundColor);

            TapTargetSequence sequence = new TapTargetSequence(PatientDataSheetActivity.this)
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

                            TapTarget.forView(editDataConstraintLayout, "Szerkesztés", "Itt tudja szerkeszteni az adatlapját.")
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

                            TapTarget.forView(addBloodPressureFAB, "Új mérés", "Itt tud új vérnyomásmérést rögzíteni és korábbiakat megtekinteni.")
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
                            Toast.makeText(PatientDataSheetActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientDataSheetActivity.this, PatientSettingsActivity.class);
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

    // Törölt időpont dialog
    private void showEditDataDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientDataSheetActivity.this).inflate(R.layout.dialog_edit_data_sheet, dialogConstraintLayout);

        AutoCompleteTextView heightAutoCompleteTextView = view.findViewById(R.id.heightAutoCompleteTextView);
        TextInputLayout heightInputLayout = view.findViewById(R.id.heightInputLayout);
        AutoCompleteTextView weightAutoCompleteTextView = view.findViewById(R.id.weightAutoCompleteTextView);
        TextInputLayout weightInputLayout = view.findViewById(R.id.weightInputLayout);
        RadioGroup periodRadioGroup = view.findViewById(R.id.periodRadioGroup);
        RadioButton preparationPeriodRadioButton = view.findViewById(R.id.preparationPeriodRadioButton);
        RadioButton competitionPeriodRadioButton = view.findViewById(R.id.competitionPeriodRadioButton);
        TextInputLayout sportInputLayout = view.findViewById(R.id.sportInputLayout);
        AutoCompleteTextView sportAutoCompleteTextView = view.findViewById(R.id.sportAutoCompleteTextView);
        TextInputLayout weeklyWorkoutInputLayout = view.findViewById(R.id.weeklyWorkoutInputLayout);
        AutoCompleteTextView weeklyWorkoutAutoCompleteTextView = view.findViewById(R.id.weeklyWorkoutAutoCompleteTextView);
        TextInputLayout sportAgeInputLayout = view.findViewById(R.id.sportAgeInputLayout);
        AutoCompleteTextView sportAgeAutoCompleteTextView = view.findViewById(R.id.sportAgeAutoCompleteTextView);
        RadioGroup sportTypeRadioGroup = view.findViewById(R.id.sportTypeRadioGroup);
        RadioButton hobbySportRadioButton = view.findViewById(R.id.hobbySportRadioButton);
        RadioButton amateurSportRadioButton = view.findViewById(R.id.amateurSportRadioButton);
        RadioButton elitSportRadioButton = view.findViewById(R.id.elitSportRadioButton);
        //panasz
        TextView breathingTextView = view.findViewById(R.id.breathingTextView);
        CheckBox breathingCheckBox = view.findViewById(R.id.breathingCheckBox);
        TextView chestPainTextView = view.findViewById(R.id.chestPainTextView);
        CheckBox chestPainCheckBox = view.findViewById(R.id.chestPainCheckBox);
        TextView rhythmDisorderTextView = view.findViewById(R.id.rhythmDisorderTextView);
        CheckBox rhythmDisorderCheckBox = view.findViewById(R.id.rhythmDisorderCheckBox);
        TextView reducedCapacityTextView = view.findViewById(R.id.reducedCapacityTextView);
        CheckBox reducedCapacityCheckBox = view.findViewById(R.id.reducedCapacityCheckBox);
        TextView hypertensionTextView = view.findViewById(R.id.hypertensionTextView);
        CheckBox hypertensionCheckBox = view.findViewById(R.id.hypertensionCheckBox);
        TextView bloodSugarTextView = view.findViewById(R.id.bloodSugarTextView);
        CheckBox bloodSugarCheckBox = view.findViewById(R.id.bloodSugarCheckBox);
        TextView highCholesterolLevelTextView = view.findViewById(R.id.highCholesterolLevelTextView);
        CheckBox highCholesterolLevelCheckBox = view.findViewById(R.id.highCholesterolLevelCheckBox);
        TextView blackOutTextView = view.findViewById(R.id.blackOutTextView);
        CheckBox blackOutCheckBox = view.findViewById(R.id.blackOutCheckBox);
        //szokás
        TextView smokingextView = view.findViewById(R.id.smokingextView);
        CheckBox smokingCheckBox = view.findViewById(R.id.smokingCheckBox);
        TextInputLayout alcoholTextInputLayout = view.findViewById(R.id.alcoholTextInputLayout);
        AutoCompleteTextView alcoholAutoCompleteTextView = view.findViewById(R.id.alcoholAutoCompleteTextView);
        TextView coffeeTextView = view.findViewById(R.id.coffeeTextView);
        CheckBox coffeeCheckBox = view.findViewById(R.id.coffeeCheckBox);
        TextInputLayout coffeeInputLayout = view.findViewById(R.id.coffeeInputLayout);
        AutoCompleteTextView coffeeAutoCompleteTextView = view.findViewById(R.id.coffeeAutoCompleteTextView);
        TextView colaTextView = view.findViewById(R.id.colaTextView);
        CheckBox colaCheckBox = view.findViewById(R.id.colaCheckBox);
        TextInputLayout colaInputLayout = view.findViewById(R.id.colaInputLayout);
        AutoCompleteTextView colaAutoCompleteTextView = view.findViewById(R.id.colaAutoCompleteTextView);
        TextView energyDrinkTextView = view.findViewById(R.id.energyDrinkTextView);
        CheckBox energyDrinkCheckBox = view.findViewById(R.id.energyDrinkCheckBox);
        TextInputLayout energyDrinkInputLayout = view.findViewById(R.id.energyDrinkInputLayout);
        AutoCompleteTextView energyDrinkAutoCompleteTextView = view.findViewById(R.id.energyDrinkAutoCompleteTextView);
        TextView nutritionalSupplementTextView = view.findViewById(R.id.nutritionalSupplementTextView);
        CheckBox nutritionalSupplementCheckBox = view.findViewById(R.id.nutritionalSupplementCheckBox);
        //gyógyszer
        CheckBox cholesterolLoweringCheckBox = view.findViewById(R.id.cholesterolLoweringCheckBox);
        TextView cholesterolLoweringTextView = view.findViewById(R.id.cholesterolLoweringTextView);
        TextView cholesterolLoweringNameTextView = view.findViewById(R.id.cholesterolLoweringNameTextView);
        TextInputLayout cholesterolLoweringNameInputLayout = view.findViewById(R.id.cholesterolLoweringNameInputLayout);
        AutoCompleteTextView cholesterolLoweringNameAutoCompleteTextView = view.findViewById(R.id.cholesterolLoweringNameAutoCompleteTextView);
        TextInputLayout cholesterolLoweringInputLayout = view.findViewById(R.id.cholesterolLoweringInputLayout);
        AutoCompleteTextView cholesterolLoweringAutoCompleteTextView = view.findViewById(R.id.cholesterolLoweringAutoCompleteTextView);
        CheckBox bloodPressureLoweringCheckBox = view.findViewById(R.id.bloodPressureLoweringCheckBox);
        TextView bloodPressureLoweringTextView = view.findViewById(R.id.bloodPressureLoweringTextView);
        TextView bloodPressureLoweringNameTextView = view.findViewById(R.id.bloodPressureLoweringNameTextView);
        TextInputLayout bloodPressureLoweringNameInputLayout = view.findViewById(R.id.bloodPressureLoweringNameInputLayout);
        AutoCompleteTextView bloodPressureLoweringNameAutoCompleteTextView = view.findViewById(R.id.bloodPressureLoweringNameAutoCompleteTextView);
        TextInputLayout bloodPressureLoweringInputLayout = view.findViewById(R.id.bloodPressureLoweringInputLayout);
        AutoCompleteTextView bloodPressureLoweringAutoCompleteTextView = view.findViewById(R.id.bloodPressureLoweringAutoCompleteTextView);
        CheckBox antiAsthmaCheckBox = view.findViewById(R.id.antiAsthmaCheckBox);
        TextView antiAsthmaNameTextView = view.findViewById(R.id.antiAsthmaNameTextView);
        TextView antiAsthmaPortionTextView = view.findViewById(R.id.antiAsthmaPortionTextView);
        TextInputLayout antiAsthmaNameInputLayout = view.findViewById(R.id.antiAsthmaNameInputLayout);
        AutoCompleteTextView antiAsthmaNameAutoCompleteTextView = view.findViewById(R.id.antiAsthmaNameAutoCompleteTextView);
        TextInputLayout antiAsthmaPortionInputLayout = view.findViewById(R.id.antiAsthmaPortionInputLayout);
        AutoCompleteTextView antiAsthmaPortionAutoCompleteTextView = view.findViewById(R.id.antiAsthmaPortionAutoCompleteTextView);

        Button saveDatasButton = view.findViewById(R.id.saveDatasButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        ScrollView scrollView = view.findViewById(R.id.scrollView);

        preparationPeriodRadioButton.setChecked(true);
        hobbySportRadioButton.setChecked(true);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = typedValue.data;
        alcoholAutoCompleteTextView.setDropDownBackgroundDrawable(new ColorDrawable(color));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sportolók")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                heightAutoCompleteTextView.setText(document.getString("height"));
                                weightAutoCompleteTextView.setText(document.getString("weight"));
                                sportAutoCompleteTextView.setText(document.getString("sport"));
                                sportAgeAutoCompleteTextView.setText(document.getString("sportAge"));
                                weeklyWorkoutAutoCompleteTextView.setText(document.getString("weeklyWorkout"));
                                sportAgeTV.setText(document.getString("sportAge"));

                                if (Objects.equals(document.getString("period"), preparationPeriodRadioButton.getText().toString())) {
                                    preparationPeriodRadioButton.setChecked(true);
                                } else {
                                    competitionPeriodRadioButton.setChecked(true);
                                }

                                if (Objects.equals(document.getString("sportType"), hobbySportRadioButton.getText().toString())) {
                                    hobbySportRadioButton.setChecked(true);
                                } else if (Objects.equals(document.getString("sportType"), amateurSportRadioButton.getText().toString())) {
                                    amateurSportRadioButton.setChecked(true);
                                } else {
                                    elitSportRadioButton.setChecked(true);
                                }

                                if (Objects.requireNonNull(document.getString("complaints")).contains(breathingTextView.getText())) {
                                    breathingCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(chestPainTextView.getText())) {
                                    chestPainCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(rhythmDisorderTextView.getText())) {
                                    rhythmDisorderCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(blackOutTextView.getText())) {
                                    blackOutCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(hypertensionTextView.getText())) {
                                    hypertensionCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(bloodSugarTextView.getText())) {
                                    bloodSugarCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(highCholesterolLevelTextView.getText())) {
                                    highCholesterolLevelCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("complaints")).contains(reducedCapacityTextView.getText())) {
                                    reducedCapacityCheckBox.setChecked(true);
                                }

                                if (Objects.requireNonNull(document.getString("habits")).contains(smokingextView.getText())) {
                                    smokingCheckBox.setChecked(true);
                                }
                                if (Pattern.compile("kávé.*").matcher(Objects.requireNonNull(document.getString("habits"))).find()) {
                                    coffeeCheckBox.setChecked(true);
                                }
                                if (Pattern.compile("cola.*").matcher(Objects.requireNonNull(document.getString("habits"))).find()) {
                                    colaCheckBox.setChecked(true);
                                }
                                if (Pattern.compile("energiaital.*").matcher(Objects.requireNonNull(document.getString("habits"))).find()) {
                                    energyDrinkCheckBox.setChecked(true);
                                }
                                if (Objects.requireNonNull(document.getString("habits")).contains(nutritionalSupplementTextView.getText())) {
                                    nutritionalSupplementCheckBox.setChecked(true);
                                }

                                if (Pattern.compile("vérnyomás csökkentő.*").matcher(Objects.requireNonNull(document.getString("medicines"))).find()) {
                                    bloodPressureLoweringCheckBox.setChecked(true);
                                }
                                if (Pattern.compile("koleszterin csökkentő.*").matcher(Objects.requireNonNull(document.getString("medicines"))).find()) {
                                    cholesterolLoweringCheckBox.setChecked(true);
                                }
                                if (Pattern.compile("asthma ellenes szer.*").matcher(Objects.requireNonNull(document.getString("medicines"))).find()) {
                                    antiAsthmaCheckBox.setChecked(true);
                                }
                            }
                        } else {
                            Toast.makeText(this, "Nincs adat a megadott azonosítóval.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérésekor.", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientDataSheetActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.alcohol,
                R.layout.dropdown_item
        );
        alcoholAutoCompleteTextView.setAdapter(adapter);

        if (coffeeCheckBox.isChecked()) {
            coffeeTextView.setVisibility(View.VISIBLE);
            coffeeInputLayout.setVisibility(View.VISIBLE);
        } else {
            coffeeTextView.setVisibility(View.GONE);
            coffeeInputLayout.setVisibility(View.GONE);
        }
        coffeeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    coffeeTextView.setVisibility(View.VISIBLE);
                    coffeeInputLayout.setVisibility(View.VISIBLE);
                } else {
                    coffeeTextView.setVisibility(View.GONE);
                    coffeeInputLayout.setVisibility(View.GONE);
                }
            }
        });

        if (colaCheckBox.isChecked()) {
            colaTextView.setVisibility(View.VISIBLE);
            colaInputLayout.setVisibility(View.VISIBLE);
        } else {
            colaTextView.setVisibility(View.GONE);
            colaInputLayout.setVisibility(View.GONE);
        }
        colaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    colaTextView.setVisibility(View.VISIBLE);
                    colaInputLayout.setVisibility(View.VISIBLE);
                } else {
                    colaTextView.setVisibility(View.GONE);
                    colaInputLayout.setVisibility(View.GONE);
                }
            }
        });

        if (energyDrinkCheckBox.isChecked()) {
            energyDrinkTextView.setVisibility(View.VISIBLE);
            energyDrinkInputLayout.setVisibility(View.VISIBLE);
        } else {
            energyDrinkTextView.setVisibility(View.GONE);
            energyDrinkInputLayout.setVisibility(View.GONE);
        }
        energyDrinkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    energyDrinkTextView.setVisibility(View.VISIBLE);
                    energyDrinkInputLayout.setVisibility(View.VISIBLE);
                } else {
                    energyDrinkTextView.setVisibility(View.GONE);
                    energyDrinkInputLayout.setVisibility(View.GONE);
                }
            }
        });

        if (cholesterolLoweringCheckBox.isChecked()) {
            cholesterolLoweringInputLayout.setVisibility(View.VISIBLE);
            cholesterolLoweringNameInputLayout.setVisibility(View.VISIBLE);
            cholesterolLoweringTextView.setVisibility(View.VISIBLE);
            cholesterolLoweringNameTextView.setVisibility(View.VISIBLE);
        } else {
            cholesterolLoweringInputLayout.setVisibility(View.GONE);
            cholesterolLoweringNameInputLayout.setVisibility(View.GONE);
            cholesterolLoweringTextView.setVisibility(View.GONE);
            cholesterolLoweringNameTextView.setVisibility(View.GONE);
        }
        cholesterolLoweringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cholesterolLoweringInputLayout.setVisibility(View.VISIBLE);
                    cholesterolLoweringNameInputLayout.setVisibility(View.VISIBLE);
                    cholesterolLoweringTextView.setVisibility(View.VISIBLE);
                    cholesterolLoweringNameTextView.setVisibility(View.VISIBLE);
                } else {
                    cholesterolLoweringInputLayout.setVisibility(View.GONE);
                    cholesterolLoweringNameInputLayout.setVisibility(View.GONE);
                    cholesterolLoweringTextView.setVisibility(View.GONE);
                    cholesterolLoweringNameTextView.setVisibility(View.GONE);
                }
            }
        });

        if (bloodPressureLoweringCheckBox.isChecked()) {
            bloodPressureLoweringInputLayout.setVisibility(View.VISIBLE);
            bloodPressureLoweringNameInputLayout.setVisibility(View.VISIBLE);
            bloodPressureLoweringTextView.setVisibility(View.VISIBLE);
            bloodPressureLoweringNameTextView.setVisibility(View.VISIBLE);
        } else {
            bloodPressureLoweringInputLayout.setVisibility(View.GONE);
            bloodPressureLoweringNameInputLayout.setVisibility(View.GONE);
            bloodPressureLoweringTextView.setVisibility(View.GONE);
            bloodPressureLoweringNameTextView.setVisibility(View.GONE);
        }
        bloodPressureLoweringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bloodPressureLoweringInputLayout.setVisibility(View.VISIBLE);
                    bloodPressureLoweringNameInputLayout.setVisibility(View.VISIBLE);
                    bloodPressureLoweringTextView.setVisibility(View.VISIBLE);
                    bloodPressureLoweringNameTextView.setVisibility(View.VISIBLE);
                } else {
                    bloodPressureLoweringInputLayout.setVisibility(View.GONE);
                    bloodPressureLoweringNameInputLayout.setVisibility(View.GONE);
                    bloodPressureLoweringTextView.setVisibility(View.GONE);
                    bloodPressureLoweringNameTextView.setVisibility(View.GONE);
                }
            }
        });

        if (antiAsthmaCheckBox.isChecked()) {
            antiAsthmaNameTextView.setVisibility(View.VISIBLE);
            antiAsthmaPortionTextView.setVisibility(View.VISIBLE);
            antiAsthmaNameInputLayout.setVisibility(View.VISIBLE);
            antiAsthmaPortionInputLayout.setVisibility(View.VISIBLE);
        } else {
            antiAsthmaNameTextView.setVisibility(View.GONE);
            antiAsthmaPortionTextView.setVisibility(View.GONE);
            antiAsthmaNameInputLayout.setVisibility(View.GONE);
            antiAsthmaPortionInputLayout.setVisibility(View.GONE);
        }
        antiAsthmaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    antiAsthmaNameTextView.setVisibility(View.VISIBLE);
                    antiAsthmaPortionTextView.setVisibility(View.VISIBLE);
                    antiAsthmaNameInputLayout.setVisibility(View.VISIBLE);
                    antiAsthmaPortionInputLayout.setVisibility(View.VISIBLE);
                } else {
                    antiAsthmaNameTextView.setVisibility(View.GONE);
                    antiAsthmaPortionTextView.setVisibility(View.GONE);
                    antiAsthmaNameInputLayout.setVisibility(View.GONE);
                    antiAsthmaPortionInputLayout.setVisibility(View.GONE);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        saveDatasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaints = "";
                String habits = "";
                String medicines = "";
                alcoholAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                    alcoholAutoCompleteTextView.setText(adapter.getItem(position));
                });
                String alcohol = alcoholAutoCompleteTextView.getText().toString();

                if (TextUtils.isEmpty(heightAutoCompleteTextView.getText().toString())) {
                    heightInputLayout.setError("A mező kitöltése kötelező!");
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, heightInputLayout.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(weightAutoCompleteTextView.getText().toString())) {
                    weightInputLayout.setError("A mező kitöltése kötelező!");
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, weightInputLayout.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(sportAutoCompleteTextView.getText().toString())) {
                    sportInputLayout.setError("A mező kitöltése kötelező!");
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, sportInputLayout.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(weeklyWorkoutAutoCompleteTextView.getText().toString())) {
                    weeklyWorkoutInputLayout.setError("A mező kitöltése kötelező!");
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, weeklyWorkoutInputLayout.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(sportAgeAutoCompleteTextView.getText().toString())) {
                    sportAgeInputLayout.setError("A mező kitöltése kötelező!");
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, sportAgeInputLayout.getBottom());
                        }
                    });
                } else if (!(alcohol.equals("soha") || alcohol.equals("alkalmanként") || alcohol.equals("rendszeresen"))) {
                    alcoholTextInputLayout.setError("Az érték nem megfelelő!");
                } else if (coffeeCheckBox.isChecked() && TextUtils.isEmpty(coffeeAutoCompleteTextView.getText().toString())) {
                    coffeeInputLayout.setError("A mező kitöltése kötelező!");
                } else if (colaCheckBox.isChecked() && TextUtils.isEmpty(colaAutoCompleteTextView.getText().toString())) {
                    colaInputLayout.setError("A mező kitöltése kötelező!");
                } else if (energyDrinkCheckBox.isChecked() && TextUtils.isEmpty(energyDrinkAutoCompleteTextView.getText().toString())) {
                    energyDrinkInputLayout.setError("A mező kitöltése kötelező!");
                } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringNameAutoCompleteTextView.getText().toString())) {
                    cholesterolLoweringNameInputLayout.setError("A mező kitöltése kötelező!");
                } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringAutoCompleteTextView.getText().toString())) {
                    cholesterolLoweringInputLayout.setError("A mező kitöltése kötelező!");
                } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringNameAutoCompleteTextView.getText().toString())) {
                    bloodPressureLoweringNameInputLayout.setError("A mező kitöltése kötelező!");
                } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringAutoCompleteTextView.getText().toString())) {
                    bloodPressureLoweringInputLayout.setError("A mező kitöltése kötelező!");
                } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaNameAutoCompleteTextView.getText().toString())) {
                    antiAsthmaNameInputLayout.setError("A mező kitöltése kötelező!");
                } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaPortionAutoCompleteTextView.getText().toString())) {
                    antiAsthmaPortionInputLayout.setError("A mező kitöltése kötelező!");
                } else {
                    // sportoló adatai
                    String height = heightAutoCompleteTextView.getText().toString();
                    String weight = weightAutoCompleteTextView.getText().toString();

                    int selectedId = periodRadioGroup.getCheckedRadioButtonId();
                    RadioButton periodRadioButton = view.findViewById(selectedId);
                    String period = periodRadioButton.getText().toString();

                    String sport = sportAutoCompleteTextView.getText().toString();
                    String weeklyWorkout = weeklyWorkoutAutoCompleteTextView.getText().toString();
                    String sportAge = sportAgeAutoCompleteTextView.getText().toString();

                    int selectedID = sportTypeRadioGroup.getCheckedRadioButtonId();
                    RadioButton sportTypeRadioButton = view.findViewById(selectedID);
                    String sportType = sportTypeRadioButton.getText().toString();

                    // panasza
                    if (breathingCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += breathingTextView.getText().toString();
                        } else {
                            complaints += ", " + breathingTextView.getText().toString();
                        }
                    }

                    if (chestPainCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += chestPainTextView.getText().toString();
                        } else {
                            complaints += ", " + chestPainTextView.getText().toString();
                        }
                    }

                    if (rhythmDisorderCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += rhythmDisorderTextView.getText().toString();
                        } else {
                            complaints += ", " + rhythmDisorderTextView.getText().toString();
                        }
                    }

                    if (reducedCapacityCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += reducedCapacityTextView.getText().toString();
                        } else {
                            complaints += ", " + reducedCapacityTextView.getText().toString();
                        }
                    }


                    // egyéb panasz
                    if (hypertensionCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += hypertensionTextView.getText().toString();
                        } else {
                            complaints += ", " + hypertensionTextView.getText().toString();
                        }
                    }

                    if (bloodSugarCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += bloodSugarTextView.getText().toString();
                        } else {
                            complaints += ", " + bloodSugarTextView.getText().toString();
                        }
                    }

                    if (highCholesterolLevelCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += highCholesterolLevelTextView.getText().toString();
                        } else {
                            complaints += ", " + highCholesterolLevelTextView.getText().toString();
                        }
                    }

                    if (blackOutCheckBox.isChecked()) {
                        if (complaints.isEmpty()) {
                            complaints += blackOutTextView.getText().toString();
                        } else {
                            complaints += ", " + blackOutTextView.getText().toString();
                        }
                    }

                    // szokások
                    if (smokingCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += smokingextView.getText().toString();
                        } else {
                            habits += ", " + smokingextView.getText().toString();
                        }
                    }

                    if (!alcohol.equals("soha")) {
                        if (habits.isEmpty()) {
                            habits += "alkohol fogyasztás (" + alcohol + ")";
                        } else {
                            habits += ", alkohol fogyasztás (" + alcohol + ")";
                        }
                    }


                    if (coffeeCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += "kávé (napi " + coffeeAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            habits += ", kávé (napi " + coffeeAutoCompleteTextView.getText().toString() + ")";
                        }
                    }

                    if (colaCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += "cola (napi " + colaAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            habits += ", cola (napi " + colaAutoCompleteTextView.getText().toString() + ")";
                        }
                    }

                    if (energyDrinkCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += "energiaital (napi " + energyDrinkAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            habits += ", energiaital (napi " + energyDrinkAutoCompleteTextView.getText().toString() + ")";
                        }
                    }

                    if (nutritionalSupplementCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += nutritionalSupplementTextView.getText().toString();
                        } else {
                            habits += ", " + nutritionalSupplementTextView.getText().toString();
                        }
                    }


                    // gyógyszerszedés
                    if (cholesterolLoweringCheckBox.isChecked()) {
                        if (medicines.isEmpty()) {
                            medicines += "koleszterin csökkentő (neve: " + cholesterolLoweringNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + cholesterolLoweringAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            medicines += ", koleszterin csökkentő (neve: " + cholesterolLoweringNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + cholesterolLoweringAutoCompleteTextView.getText().toString() + ")";
                        }
                    }

                    if (bloodPressureLoweringCheckBox.isChecked()) {
                        if (medicines.isEmpty()) {
                            medicines += "vérnyomás csökkentő (neve: " + bloodPressureLoweringNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + bloodPressureLoweringAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            medicines += ", vérnyomás csökkentő (neve: " + bloodPressureLoweringNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + bloodPressureLoweringAutoCompleteTextView.getText().toString() + ")";
                        }
                    }

                    if (antiAsthmaCheckBox.isChecked()) {
                        if (medicines.isEmpty()) {
                            medicines += "asthma ellenes szer (neve: " + antiAsthmaNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + antiAsthmaPortionAutoCompleteTextView.getText().toString() + ")";
                        } else {
                            medicines += ", asthma ellenes szer (neve: " + antiAsthmaNameAutoCompleteTextView.getText().toString() +
                                    ", adag: " + antiAsthmaPortionAutoCompleteTextView.getText().toString() + ")";
                        }
                    }


                    if (complaints.isEmpty()) {
                        complaints = "nincs panasza";
                    }

                    if (habits.isEmpty()) {
                        habits = "nincsennek szokásai";
                    }

                    if (medicines.isEmpty()) {
                        medicines = "nem szed gyógyszert";
                    }

                    mFirestore = FirebaseFirestore.getInstance();
                    mItems = mFirestore.collection("Sportolók");
                    Timestamp currentTimestamp = Timestamp.now();

                    Athlete athlete = new Athlete(
                            personalId,
                            name,
                            height,
                            weight,
                            period,
                            sport,
                            weeklyWorkout,
                            sportAge,
                            sportType,
                            complaints,
                            habits,
                            medicines,
                            currentTimestamp);

                    mItems.whereEqualTo("personalId", personalId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                                    mItems.document(documentId).set(athlete)
                                            .addOnSuccessListener(aVoid -> {
                                                alertDialog.dismiss();
                                                showSuccessDialog();
                                            })
                                            .addOnFailureListener(e -> {
                                                alertDialog.dismiss();
                                                Toast.makeText(PatientDataSheetActivity.this, "Hiba a frissítés során", Toast.LENGTH_LONG).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                alertDialog.dismiss();
                                Toast.makeText(PatientDataSheetActivity.this, "Hiba a keresés során", Toast.LENGTH_LONG).show();
                            });
                }
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void loadAthleteData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sportolók")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                setAthleteData(document);
                            }
                        } else {
                            Toast.makeText(this, "Nincs adat a megadott azonosítóval.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérésekor.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Sikeres adatlap módosítás dialog
    private void showSuccessDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientDataSheetActivity.this).inflate(R.layout.popup_successful_request, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView successTextView = view.findViewById(R.id.successTextView);
        TextView descTextView = view.findViewById(R.id.descTextView);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        successTextView.setText("Sikeres mentés!");
        descTextView.setText("Az adatlapja frissült a rendszerben.");

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PatientDataSheetActivity.this);
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                recreate();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setOnDismissListener(dialog -> recreate());

        alertDialog.show();
    }

    private void setAthleteData(QueryDocumentSnapshot document) {
        heightTV.setText(document.getString("height"));
        weightTV.setText(document.getString("weight"));
        periodTV.setText(document.getString("period"));
        sportTV.setText(document.getString("sport"));
        weeklyWorkoutTV.setText(document.getString("weeklyWorkout"));
        sportAgeTV.setText(document.getString("sportAge"));
        sportTypeTV.setText(document.getString("sportType"));
        complaintsTV.setText(document.getString("complaints"));
        habitsTV.setText(document.getString("habits"));
        medicinesTV.setText(document.getString("medicines"));
    }

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }
}