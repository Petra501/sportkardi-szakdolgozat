package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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

import com.example.sportkardi.NoSwipeViewPager;
import com.example.sportkardi.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class CardiIntroActivity extends AppCompatActivity {
    private static final String TAG = CardiIntroActivity.class.getName();
    private static final int KEY = 44;
    private CardiIntroActivity.MyViewPagerAdapter viewPagerAdapter;
    private TextView[] dots;
    private LinearLayout dotsLinearLayout;
    private NoSwipeViewPager viewPager;

    private String personalId, name, gender;
    private int[] layouts;

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
                R.layout.intro_one_cardi,
                R.layout.intro_two
        };

        viewPagerAdapter = new CardiIntroActivity.MyViewPagerAdapter();
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

                        // Jelszó mező ellenőrzése
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

                        // Jelszó megerősítése mező ellenőrzése
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
                    launchHomeScreen();
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
                                                Toast.makeText(CardiIntroActivity.this, "Hiba történt a frissítés közben", Toast.LENGTH_LONG).show());
                            } else {
                                Toast.makeText(CardiIntroActivity.this, "Nincs frissítendő adat", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CardiIntroActivity.this, "Felhasználó nem található", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(CardiIntroActivity.this, "Hiba történt az adatbázis elérése közben", Toast.LENGTH_LONG).show());
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
        Intent intent = new Intent(CardiIntroActivity.this, CardiMainMenuActivity.class);
        intent.putExtra("KEY", KEY);
        intent.putExtra("name", name);
        intent.putExtra("personalId", personalId);
        intent.putExtra("gender", gender);
        startActivity(intent);
        finish();
    }
}