package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.NoSwipeViewPager;
import com.example.sportkardi.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PatientIntroActivity extends AppCompatActivity {
    private static final String TAG = PatientIntroActivity.class.getName();
    private static final int KEY = 44;
    private MyViewPagerAdapter viewPagerAdapter;
    private TextView[] dots;
    private LinearLayout dotsLinearLayout;
    private NoSwipeViewPager viewPager;

    private String personalId, name, gender;
    private int[] layouts;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE); // Fehér háttér
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        int key = getIntent().getIntExtra("KEY", 0);
        personalId = getIntent().getStringExtra("personalId");
        name = getIntent().getStringExtra("name");
        gender = getIntent().getStringExtra("gender");

        viewPager = findViewById(R.id.viewPager);
        dotsLinearLayout = findViewById(R.id.dotsLinearLayout);

        layouts = new int[]{
                R.layout.intro_one_patient,
                R.layout.intro_two,
                R.layout.intro_three
        };

        viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        addBottomDots(0);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] activeColors = getResources().getIntArray(R.array.active);
        int[] inActiveColors = getResources().getIntArray(R.array.inactive);
        dotsLinearLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(inActiveColors[currentPage]);
            dotsLinearLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(activeColors[currentPage]);
        }
    }


    public class MyViewPagerAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);

            // intro_one_patient.xml
            if (position == 0){
                Button nextButton = view.findViewById(R.id.nextButton);

                nextButton.setOnClickListener(v -> {
                    viewPager.setCurrentItem(position + 1);
                });
            }

            // intro_two.xml
            if (position == 1) {
                AutoCompleteTextView passwordAutoCompleteTextView = view.findViewById(R.id.passwordAutoCompleteTextView);
                AutoCompleteTextView passwordAgainAutoCompleteTextView = view.findViewById(R.id.passwordAgainAutoCompleteTextView);
                TextInputLayout passwordInputLayout = view.findViewById(R.id.passwordInputLayout);
                TextInputLayout passwordAgainInputLayout = view.findViewById(R.id.passwordAgainInputLayout);
                Button saveButton = view.findViewById(R.id.saveButton);

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
                    viewPager.setCurrentItem(position + 1);
                });
            }

            //intro_two.xml
            if (position == 2) {
                //sportoló adatai
                AutoCompleteTextView heightAutoCompleteTextView = view.findViewById(R.id.heightAutoCompleteTextView);
                TextInputLayout heightInputLayout = view.findViewById(R.id.heightInputLayout);
                AutoCompleteTextView weightAutoCompleteTextView = view.findViewById(R.id.weightAutoCompleteTextView);
                TextInputLayout weightInputLayout = view.findViewById(R.id.weightInputLayout);
                RadioGroup periodRadioGroup = view.findViewById(R.id.periodRadioGroup);
                RadioButton periodRButton = view.findViewById(R.id.preparationPeriodRadioButton);
                TextInputLayout sportInputLayout = view.findViewById(R.id.sportInputLayout);
                AutoCompleteTextView sportAutoCompleteTextView = view.findViewById(R.id.sportAutoCompleteTextView);
                TextInputLayout weeklyWorkoutInputLayout = view.findViewById(R.id.weeklyWorkoutInputLayout);
                AutoCompleteTextView weeklyWorkoutAutoCompleteTextView = view.findViewById(R.id.weeklyWorkoutAutoCompleteTextView);
                TextInputLayout sportAgeInputLayout = view.findViewById(R.id.sportAgeInputLayout);
                AutoCompleteTextView sportAgeAutoCompleteTextView = view.findViewById(R.id.sportAgeAutoCompleteTextView);
                RadioGroup sportTypeRadioGroup = view.findViewById(R.id.sportTypeRadioGroup);
                RadioButton sportTypeRButton = view.findViewById(R.id.hobbySportRadioButton);
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
                ScrollView scrollView = view.findViewById(R.id.scrollView);

                periodRButton.setChecked(true);
                sportTypeRButton.setChecked(true);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        this.layoutInflater.getContext(),
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
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, coffeeCheckBox.getBottom());
                                }
                            });
                        } else if (colaCheckBox.isChecked() && TextUtils.isEmpty(colaAutoCompleteTextView.getText().toString())) {
                            colaInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, colaCheckBox.getBottom());
                                }
                            });
                        } else if (energyDrinkCheckBox.isChecked() && TextUtils.isEmpty(energyDrinkAutoCompleteTextView.getText().toString())) {
                            energyDrinkInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, energyDrinkCheckBox.getBottom());
                                }
                            });
                        } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringNameAutoCompleteTextView.getText().toString())) {
                            cholesterolLoweringNameInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, cholesterolLoweringCheckBox.getBottom());
                                }
                            });
                        } else if (cholesterolLoweringCheckBox.isChecked() && TextUtils.isEmpty(cholesterolLoweringAutoCompleteTextView.getText().toString())) {
                            cholesterolLoweringInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, cholesterolLoweringCheckBox.getBottom());
                                }
                            });
                        } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringNameAutoCompleteTextView.getText().toString())) {
                            bloodPressureLoweringNameInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, bloodPressureLoweringCheckBox.getBottom());
                                }
                            });
                        } else if (bloodPressureLoweringCheckBox.isChecked() && TextUtils.isEmpty(bloodPressureLoweringAutoCompleteTextView.getText().toString())) {
                            bloodPressureLoweringInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, bloodPressureLoweringCheckBox.getBottom());
                                }
                            });
                        } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaNameAutoCompleteTextView.getText().toString())) {
                            antiAsthmaNameInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, antiAsthmaCheckBox.getBottom());
                                }
                            });
                        } else if (antiAsthmaCheckBox.isChecked() && TextUtils.isEmpty(antiAsthmaPortionAutoCompleteTextView.getText().toString())) {
                            antiAsthmaPortionInputLayout.setError("A mező kitöltése kötelező!");
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.scrollTo(0, antiAsthmaCheckBox.getBottom());
                                }
                            });
                        } else {
                            // sportoló adatai
                            String height = heightAutoCompleteTextView.getText().toString();
                            String weight = weightAutoCompleteTextView.getText().toString();

                            int selectedId = periodRadioGroup.getCheckedRadioButtonId();
                            RadioButton periodRadioButton = findViewById(selectedId);
                            String period = periodRadioButton.getText().toString();

                            String sport = sportAutoCompleteTextView.getText().toString();
                            String weeklyWorkout = weeklyWorkoutAutoCompleteTextView.getText().toString();
                            String sportAge = sportAgeAutoCompleteTextView.getText().toString();

                            int selectedID = sportTypeRadioGroup.getCheckedRadioButtonId();
                            RadioButton sportTypeRadioButton = findViewById(selectedID);
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
                            mItems.add(athlete).addOnSuccessListener(documentReference -> {
                            }).addOnFailureListener(e -> {
                                Toast.makeText(PatientIntroActivity.this, "Hiba a mentés során", Toast.LENGTH_LONG).show();
                            });
                            launchHomeScreen();
                        }
                    }
                });
            }

            container.addView(view);
            return view;
        }

        // Új jelszó mentése
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
                                        .addOnFailureListener(e ->
                                                Toast.makeText(PatientIntroActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                            } else {
                                Toast.makeText(PatientIntroActivity.this, "Nincs frissítendő adat", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PatientIntroActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(PatientIntroActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
        }

        // Jelszó titkosítása
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

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + 1;
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(PatientIntroActivity.this, PatientMainMenuActivity.class);
        intent.putExtra("KEY", KEY);
        intent.putExtra("name", name);
        intent.putExtra("personalId", personalId);
        intent.putExtra("gender", gender);
        startActivity(intent);
        finish();
    }
}