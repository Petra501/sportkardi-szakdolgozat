package com.example.sportkardi.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.R;

public class NewPatientActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = NewPatientActivity.class.getName();
    private static final int KEY = 44;
    private ScrollView scrollView;

    // sportoló adatai
    EditText heightEditText;
    EditText weightEditText;
    RadioGroup periodRadioGroup;
    RadioButton periodRadioButton;
    RadioButton periodRButton;
    EditText sportEditText;
    EditText weeklyWorkoutEditText;
    EditText sportAgeEditText;
    RadioGroup sportTypeRadioGroup;
    RadioButton sportTypeRadioButton;
    RadioButton sportTypeRButton;

    // panasza
    TextView breathingTextView;
    CheckBox breathingCheckBox;

    TextView chestPainTextView;
    CheckBox chestPainCheckBox;

    TextView rhythmDisorderTextView;
    CheckBox rhythmDisorderCheckBox;

    TextView reducedCapacityTextView;
    CheckBox reducedCapacityCheckBox;


    // egyéb panasz
    TextView hypertensionTextView;
    CheckBox hypertensionCheckBox;

    TextView bloodSugarTextView;
    CheckBox bloodSugarCheckBox;

    TextView highCholesterolLevelTextView;
    CheckBox highCholesterolLevelCheckBox;

    TextView blackOutTextView;
    CheckBox blackOutCheckBox;

    // szokások
    TextView smokingextView;
    CheckBox smokingCheckBox;

    Spinner alcoholSpinner;

    TextView coffeeTextView;
    EditText coffeeEditText;
    CheckBox coffeeCheckBox;

    TextView colaTextView;
    EditText colaEditText;
    CheckBox colaCheckBox;

    TextView energyDrinkTextView;
    EditText energyDrinkEditText;
    CheckBox energyDrinkCheckBox;

    TextView nutritionalSupplementTextView;
    CheckBox nutritionalSupplementCheckBox;

    // gyógyszerszedés
    CheckBox cholesterolLoweringCheckBox;
    TextView cholesterolLoweringTextView;
    TextView cholesterolLoweringNameTextView;
    EditText cholesterolLoweringPortionEditText;
    EditText cholesterolLoweringNameEditText;

    CheckBox bloodPressureLoweringCheckBox;
    TextView bloodPressureLoweringTextView;
    TextView bloodPressureLoweringNameTextView;
    EditText bloodPressureLoweringPortionEditText;
    EditText bloodPressureLoweringNameEditText;

    CheckBox antiAsthmaCheckBox;
    TextView antiAsthmaNameTextView;
    TextView antiAsthmaPortionTextView;
    EditText antiAsthmaNameEditText;
    EditText antiAsthmaPortionEditText;

    // egyéb
    String complaints = "";
    String otherComplaints = "";
    String habits = "";
    String medicines = "";
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_patient);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        int key = getIntent().getIntExtra("KEY", 0);

        if (key != 44) {
            finish();
        }

        final String personalId = getIntent().getStringExtra("personalId");
        final String name = getIntent().getStringExtra("name");

        scrollView = findViewById(R.id.main);

        // sportoló adatai
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        sportEditText = findViewById(R.id.sportEditText);
        weeklyWorkoutEditText = findViewById(R.id.weeklyWorkoutEditText);
        sportAgeEditText = findViewById(R.id.sportAgeEditText);
        periodRadioGroup = findViewById(R.id.periodRadioGroup);
        periodRButton = findViewById(R.id.preparationPeriodRadioButton);
        periodRButton.setChecked(true);
        sportTypeRadioGroup = findViewById(R.id.sportTypeRadioGroup);
        sportTypeRButton = findViewById(R.id.hobbySportRadioButton);
        sportTypeRButton.setChecked(true);

        // panasza
        breathingTextView = findViewById(R.id.breathingTextView);
        breathingCheckBox = findViewById(R.id.breathingCheckBox);

        chestPainTextView = findViewById(R.id.chestPainTextView);
        chestPainCheckBox = findViewById(R.id.chestPainCheckBox);

        rhythmDisorderTextView = findViewById(R.id.rhythmDisorderTextView);
        rhythmDisorderCheckBox = findViewById(R.id.rhythmDisorderCheckBox);

        reducedCapacityTextView = findViewById(R.id.reducedCapacityTextView);
        reducedCapacityCheckBox = findViewById(R.id.reducedCapacityCheckBox);


        // egyéb panasz
        hypertensionTextView = findViewById(R.id.hypertensionTextView);
        hypertensionCheckBox = findViewById(R.id.hypertensionCheckBox);

        bloodSugarTextView = findViewById(R.id.bloodSugarTextView);
        bloodSugarCheckBox = findViewById(R.id.bloodSugarCheckBox);

        highCholesterolLevelTextView = findViewById(R.id.highCholesterolLevelTextView);
        highCholesterolLevelCheckBox = findViewById(R.id.highCholesterolLevelCheckBox);

        blackOutTextView = findViewById(R.id.blackOutTextView);
        blackOutCheckBox = findViewById(R.id.blackOutCheckBox);


        // szokások
        smokingextView = findViewById(R.id.smokingextView);
        smokingCheckBox = findViewById(R.id.smokingCheckBox);

        alcoholSpinner = findViewById(R.id.alcoholSpinner);
        alcoholSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.alcohol, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alcoholSpinner.setAdapter(adapter);

        coffeeTextView = findViewById(R.id.coffeeTextView);
        coffeeEditText = findViewById(R.id.coffeeEditText);
        coffeeCheckBox = findViewById(R.id.coffeeCheckBox);
        if (coffeeCheckBox.isChecked()) {
            coffeeTextView.setVisibility(View.VISIBLE);
            coffeeEditText.setVisibility(View.VISIBLE);
        } else {
            coffeeTextView.setVisibility(View.GONE);
            coffeeEditText.setVisibility(View.GONE);
        }
        coffeeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    coffeeTextView.setVisibility(View.VISIBLE);
                    coffeeEditText.setVisibility(View.VISIBLE);
                } else {
                    coffeeTextView.setVisibility(View.GONE);
                    coffeeEditText.setVisibility(View.GONE);
                }
            }
        });

        colaTextView = findViewById(R.id.colaTextView);
        colaEditText = findViewById(R.id.colaEditText);
        colaCheckBox = findViewById(R.id.colaCheckBox);
        if (colaCheckBox.isChecked()) {
            colaTextView.setVisibility(View.VISIBLE);
            colaEditText.setVisibility(View.VISIBLE);
        } else {
            colaTextView.setVisibility(View.GONE);
            colaEditText.setVisibility(View.GONE);
        }
        colaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    colaTextView.setVisibility(View.VISIBLE);
                    colaEditText.setVisibility(View.VISIBLE);
                } else {
                    colaTextView.setVisibility(View.GONE);
                    colaEditText.setVisibility(View.GONE);
                }
            }
        });

        energyDrinkTextView = findViewById(R.id.energyDrinkTextView);
        energyDrinkEditText = findViewById(R.id.energyDrinkEditText);
        energyDrinkCheckBox = findViewById(R.id.energyDrinkCheckBox);
        if (energyDrinkCheckBox.isChecked()) {
            energyDrinkTextView.setVisibility(View.VISIBLE);
            energyDrinkEditText.setVisibility(View.VISIBLE);
        } else {
            energyDrinkTextView.setVisibility(View.GONE);
            energyDrinkEditText.setVisibility(View.GONE);
        }

        energyDrinkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    energyDrinkTextView.setVisibility(View.VISIBLE);
                    energyDrinkEditText.setVisibility(View.VISIBLE);
                } else {
                    energyDrinkTextView.setVisibility(View.GONE);
                    energyDrinkEditText.setVisibility(View.GONE);
                }
            }
        });

        nutritionalSupplementTextView = findViewById(R.id.nutritionalSupplementTextView);
        nutritionalSupplementCheckBox = findViewById(R.id.nutritionalSupplementCheckBox);


        // gyógyszerszedés
        cholesterolLoweringPortionEditText = findViewById(R.id.cholesterolLoweringPortionEditText);
        cholesterolLoweringNameEditText = findViewById(R.id.cholesterolLoweringNameEditText);
        cholesterolLoweringTextView = findViewById(R.id.cholesterolLoweringTextView);
        cholesterolLoweringNameTextView = findViewById(R.id.cholesterolLoweringNameTextView);
        cholesterolLoweringCheckBox = findViewById(R.id.cholesterolLoweringCheckBox);
        if (cholesterolLoweringCheckBox.isChecked()) {
            cholesterolLoweringPortionEditText.setVisibility(View.VISIBLE);
            cholesterolLoweringNameEditText.setVisibility(View.VISIBLE);
            cholesterolLoweringTextView.setVisibility(View.VISIBLE);
            cholesterolLoweringNameTextView.setVisibility(View.VISIBLE);
        } else {
            cholesterolLoweringPortionEditText.setVisibility(View.GONE);
            cholesterolLoweringNameEditText.setVisibility(View.GONE);
            cholesterolLoweringTextView.setVisibility(View.GONE);
            cholesterolLoweringNameTextView.setVisibility(View.GONE);
        }

        cholesterolLoweringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cholesterolLoweringPortionEditText.setVisibility(View.VISIBLE);
                    cholesterolLoweringNameEditText.setVisibility(View.VISIBLE);
                    cholesterolLoweringTextView.setVisibility(View.VISIBLE);
                    cholesterolLoweringNameTextView.setVisibility(View.VISIBLE);
                } else {
                    cholesterolLoweringPortionEditText.setVisibility(View.GONE);
                    cholesterolLoweringNameEditText.setVisibility(View.GONE);
                    cholesterolLoweringTextView.setVisibility(View.GONE);
                    cholesterolLoweringNameTextView.setVisibility(View.GONE);
                }
            }
        });

        bloodPressureLoweringPortionEditText = findViewById(R.id.bloodPressureLoweringPortionEditText);
        bloodPressureLoweringNameEditText = findViewById(R.id.bloodPressureLoweringNameEditText);
        bloodPressureLoweringTextView = findViewById(R.id.bloodPressureLoweringTextView);
        bloodPressureLoweringNameTextView = findViewById(R.id.bloodPressureLoweringNameTextView);
        bloodPressureLoweringCheckBox = findViewById(R.id.bloodPressureLoweringCheckBox);
        if (bloodPressureLoweringCheckBox.isChecked()) {
            bloodPressureLoweringPortionEditText.setVisibility(View.VISIBLE);
            bloodPressureLoweringNameEditText.setVisibility(View.VISIBLE);
            bloodPressureLoweringTextView.setVisibility(View.VISIBLE);
            bloodPressureLoweringNameTextView.setVisibility(View.VISIBLE);
        } else {
            bloodPressureLoweringPortionEditText.setVisibility(View.GONE);
            bloodPressureLoweringNameEditText.setVisibility(View.GONE);
            bloodPressureLoweringTextView.setVisibility(View.GONE);
            bloodPressureLoweringNameTextView.setVisibility(View.GONE);
        }

        bloodPressureLoweringCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bloodPressureLoweringPortionEditText.setVisibility(View.VISIBLE);
                    bloodPressureLoweringNameEditText.setVisibility(View.VISIBLE);
                    bloodPressureLoweringTextView.setVisibility(View.VISIBLE);
                    bloodPressureLoweringNameTextView.setVisibility(View.VISIBLE);
                } else {
                    bloodPressureLoweringPortionEditText.setVisibility(View.GONE);
                    bloodPressureLoweringNameEditText.setVisibility(View.GONE);
                    bloodPressureLoweringTextView.setVisibility(View.GONE);
                    bloodPressureLoweringNameTextView.setVisibility(View.GONE);
                }
            }
        });

        antiAsthmaCheckBox = findViewById(R.id.antiAsthmaCheckBox);
        antiAsthmaNameTextView = findViewById(R.id.antiAsthmaNameTextView);
        antiAsthmaPortionTextView = findViewById(R.id.antiAsthmaPortionTextView);
        antiAsthmaNameEditText = findViewById(R.id.antiAsthmaNameEditText);
        antiAsthmaPortionEditText = findViewById(R.id.antiAsthmaPortionEditText);
        if (antiAsthmaCheckBox.isChecked()) {
            antiAsthmaNameTextView.setVisibility(View.VISIBLE);
            antiAsthmaPortionTextView.setVisibility(View.VISIBLE);
            antiAsthmaNameEditText.setVisibility(View.VISIBLE);
            antiAsthmaPortionEditText.setVisibility(View.VISIBLE);
        } else {
            antiAsthmaNameTextView.setVisibility(View.GONE);
            antiAsthmaPortionTextView.setVisibility(View.GONE);
            antiAsthmaNameEditText.setVisibility(View.GONE);
            antiAsthmaPortionEditText.setVisibility(View.GONE);
        }

        antiAsthmaCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    antiAsthmaNameTextView.setVisibility(View.VISIBLE);
                    antiAsthmaPortionTextView.setVisibility(View.VISIBLE);
                    antiAsthmaNameEditText.setVisibility(View.VISIBLE);
                    antiAsthmaPortionEditText.setVisibility(View.VISIBLE);
                } else {
                    antiAsthmaNameTextView.setVisibility(View.GONE);
                    antiAsthmaPortionTextView.setVisibility(View.GONE);
                    antiAsthmaNameEditText.setVisibility(View.GONE);
                    antiAsthmaPortionEditText.setVisibility(View.GONE);
                }
            }
        });

        save = findViewById(R.id.saveDatasButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaints = "";
                otherComplaints = "";
                habits = "";
                medicines = "";
                String alcohol = alcoholSpinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(heightEditText.getText().toString())) {
                    heightEditText.setError("A mező kitöltése kötelező!");
                    heightEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, heightEditText.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(weightEditText.getText().toString())) {
                    weightEditText.setError("A mező kitöltése kötelező!");
                    weightEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, weightEditText.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(sportEditText.getText().toString())) {
                    sportEditText.setError("A mező kitöltése kötelező!");
                    sportEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, sportEditText.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(weeklyWorkoutEditText.getText().toString())) {
                    weeklyWorkoutEditText.setError("A mező kitöltése kötelező!");
                    weeklyWorkoutEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, weeklyWorkoutEditText.getBottom());
                        }
                    });
                } else if (TextUtils.isEmpty(sportAgeEditText.getText().toString())) {
                    sportAgeEditText.setError("A mező kitöltése kötelező!");
                    sportAgeEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, sportAgeEditText.getBottom());
                        }
                    });
                } else if (alcohol.equals("--válassz egy opciót--")) {
                    Toast.makeText(NewPatientActivity.this, "Alkoholfogyasztás értéke nem megfelelő!", Toast.LENGTH_LONG).show();
                    return;
                } else if (coffeeCheckBox.isChecked() && TextUtils.isEmpty(coffeeEditText.getText().toString())) {
                    coffeeEditText.setError("A mező kitöltése kötelező!");
                    coffeeEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, coffeeEditText.getBottom());
                        }
                    });
                } else if (colaCheckBox.isChecked() && TextUtils.isEmpty(colaEditText.getText().toString())) {
                    colaEditText.setError("A mező kitöltése kötelező!");
                    colaEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, colaEditText.getBottom());
                        }
                    });
                } else if (energyDrinkCheckBox.isChecked() && TextUtils.isEmpty(energyDrinkEditText.getText().toString())) {
                    energyDrinkEditText.setError("A mező kitöltése kötelező!");
                    energyDrinkEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, energyDrinkEditText.getBottom());
                        }
                    });
                } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringNameEditText.getText().toString())) {
                    cholesterolLoweringNameEditText.setError("A mező kitöltése kötelező!");
                    cholesterolLoweringNameEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, cholesterolLoweringNameEditText.getBottom());
                        }
                    });
                } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringPortionEditText.getText().toString())) {
                    cholesterolLoweringPortionEditText.setError("A mező kitöltése kötelező!");
                    cholesterolLoweringPortionEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, cholesterolLoweringPortionEditText.getBottom());
                        }
                    });
                } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringNameEditText.getText().toString())) {
                    bloodPressureLoweringNameEditText.setError("A mező kitöltése kötelező!");
                    bloodPressureLoweringNameEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, bloodPressureLoweringNameEditText.getBottom());
                        }
                    });
                } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringPortionEditText.getText().toString())) {
                    bloodPressureLoweringPortionEditText.setError("A mező kitöltése kötelező!");
                    bloodPressureLoweringPortionEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, bloodPressureLoweringPortionEditText.getBottom());
                        }
                    });
                } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaNameEditText.getText().toString())) {
                    antiAsthmaNameEditText.setError("A mező kitöltése kötelező!");
                    antiAsthmaNameEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, antiAsthmaNameEditText.getBottom());
                        }
                    });
                } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaPortionEditText.getText().toString())) {
                    antiAsthmaPortionEditText.setError("A mező kitöltése kötelező!");
                    antiAsthmaPortionEditText.requestFocus();
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollTo(0, antiAsthmaPortionEditText.getBottom());
                        }
                    });
                } else {
                    // sportoló adatai
                    String height = heightEditText.getText().toString();
                    String weight = weightEditText.getText().toString();

                    int selectedId = periodRadioGroup.getCheckedRadioButtonId();
                    periodRadioButton = findViewById(selectedId);
                    String period = periodRadioButton.getText().toString();

                    String sport = sportEditText.getText().toString();
                    String weeklyWorkout = weeklyWorkoutEditText.getText().toString();
                    String sportAge = sportAgeEditText.getText().toString();

                    int selectedID = sportTypeRadioGroup.getCheckedRadioButtonId();
                    sportTypeRadioButton = findViewById(selectedID);
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
                        if (otherComplaints.isEmpty()) {
                            otherComplaints += hypertensionTextView.getText().toString();
                        } else {
                            otherComplaints += ", " + hypertensionTextView.getText().toString();
                        }
                    }

                    if (bloodSugarCheckBox.isChecked()) {
                        if (otherComplaints.isEmpty()) {
                            otherComplaints += bloodSugarTextView.getText().toString();
                        } else {
                            otherComplaints += ", " + bloodSugarTextView.getText().toString();
                        }
                    }

                    if (highCholesterolLevelCheckBox.isChecked()) {
                        if (otherComplaints.isEmpty()) {
                            otherComplaints += highCholesterolLevelTextView.getText().toString();
                        } else {
                            otherComplaints += ", " + highCholesterolLevelTextView.getText().toString();
                        }
                    }

                    if (blackOutCheckBox.isChecked()) {
                        if (otherComplaints.isEmpty()) {
                            otherComplaints += blackOutTextView.getText().toString();
                        } else {
                            otherComplaints += ", " + blackOutTextView.getText().toString();
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
                            habits += "kávé (napi " + coffeeEditText.getText().toString() + ")";
                        } else {
                            habits += ", kávé (napi " + coffeeEditText.getText().toString() + ")";
                        }
                    }

                    if (colaCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += "cola (napi " + colaEditText.getText().toString() + ")";
                        } else {
                            habits += ", cola (napi " + colaEditText.getText().toString() + ")";
                        }
                    }

                    if (energyDrinkCheckBox.isChecked()) {
                        if (habits.isEmpty()) {
                            habits += "energiaital (napi " + energyDrinkEditText.getText().toString() + ")";
                        } else {
                            habits += ", energiaital (napi " + energyDrinkEditText.getText().toString() + ")";
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
                            medicines += "koleszterin csökkentő (neve: " + cholesterolLoweringNameEditText.getText().toString() +
                                    ", adag: " + cholesterolLoweringPortionEditText.getText().toString() + ")";
                        } else {
                            medicines += ", koleszterin csökkentő (neve: " + cholesterolLoweringNameEditText.getText().toString() +
                                    ", adag: " + cholesterolLoweringPortionEditText.getText().toString() + ")";
                        }
                    }

                    if (bloodPressureLoweringCheckBox.isChecked()) {
                        if (medicines.isEmpty()) {
                            medicines += "vérnyomás csökkentő (neve: " + bloodPressureLoweringNameEditText.getText().toString() +
                                    ", adag: " + bloodPressureLoweringPortionEditText.getText().toString() + ")";
                        } else {
                            medicines += ", vérnyomás csökkentő (neve: " + bloodPressureLoweringNameEditText.getText().toString() +
                                    ", adag: " + bloodPressureLoweringPortionEditText.getText().toString() + ")";
                        }
                    }

                    if (antiAsthmaCheckBox.isChecked()) {
                        if (medicines.isEmpty()) {
                            medicines += "asthma ellenes szer (neve: " + antiAsthmaNameEditText.getText().toString() +
                                    ", adag: " + antiAsthmaPortionEditText.getText().toString() + ")";
                        } else {
                            medicines += ", asthma ellenes szer (neve: " + antiAsthmaNameEditText.getText().toString() +
                                    ", adag: " + antiAsthmaPortionEditText.getText().toString() + ")";
                        }
                    }

                    if (complaints.isEmpty()) {
                        complaints = "nincs panasza";
                    }

                    if (otherComplaints.isEmpty()) {
                        otherComplaints = "nincs egyéb panasza";
                    }

                    if (habits.isEmpty()) {
                        habits = "nincsennek szokásai";
                    }

                    if (medicines.isEmpty()) {
                        medicines = "nem szed gyógyszert";
                    }

                    Intent intent = new Intent(NewPatientActivity.this, DataActivity.class);
                    intent.putExtra("personalId", personalId);
                    intent.putExtra("name", name);
                    intent.putExtra("KEY", KEY);
                    intent.putExtra("height", height);
                    intent.putExtra("weight", weight);
                    intent.putExtra("period", period);
                    intent.putExtra("sport", sport);
                    intent.putExtra("weeklyWorkout", weeklyWorkout);
                    intent.putExtra("sportAge", sportAge);
                    intent.putExtra("sportType", sportType);
                    intent.putExtra("complaints", complaints);
                    intent.putExtra("otherComplaints", otherComplaints);
                    intent.putExtra("habits", habits);
                    intent.putExtra("medicines", medicines);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}