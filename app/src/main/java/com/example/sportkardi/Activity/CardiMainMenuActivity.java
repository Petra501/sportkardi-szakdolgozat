package com.example.sportkardi.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Adapter.TodaysAppointmentListAdapter;
import com.example.sportkardi.Model.RequestItem;
import com.example.sportkardi.Model.TodaysAppointment;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.getkeepsafe.taptargetview.TapTarget;

public class CardiMainMenuActivity extends AppCompatActivity {

    private static final String TAG = CardiMainMenuActivity.class.getName();
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

    private TextView welcomeTextView, badgeTextView, noAppointmentTextView, todaysAppointmentsTextView;
    private LinearLayout viewPatientsCard, giveAppointmentCard, registerNewPatientCard;
    private LinearLayout profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private ConstraintLayout profileConstraintLayout, signOutConstraintLayout;
    private RecyclerView appointmentListRecyclerView;

    private String personalId, name, gender, selectedDay;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_CUSTOM_MESSAGE".equals(intent.getAction())) {
                handleMessage();
            }
        }
    };

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
        setContentView(R.layout.activity_cardi_main_menu);
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

        if (key != 44) {
            finish();
        }

        // BroadcastReceiver regiszrtálása
        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver,
                new IntentFilter("ACTION_CUSTOM_MESSAGE")
        );

        profileConstraintLayout = findViewById(R.id.profileConstraintLayout);
        signOutConstraintLayout = findViewById(R.id.signOutConstraintLayout);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        viewPatientsCard = findViewById(R.id.viewPatientsCard);
        giveAppointmentCard = findViewById(R.id.giveAppointmentCard);
        registerNewPatientCard = findViewById(R.id.registerNewPatientCard);

        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        badgeTextView = findViewById(R.id.badgeTextView);

        appointmentListRecyclerView = findViewById(R.id.appointmentListRecyclerView);
        noAppointmentTextView = findViewById(R.id.noAppointmentTextView);
        todaysAppointmentsTextView = findViewById(R.id.todaysAppointmentsTextView);

        noAppointmentTextView.setVisibility(View.VISIBLE);
        appointmentListRecyclerView.setVisibility(View.GONE);

        appointmentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentListRecyclerView.setHasFixedSize(true);

        personalId = getIntent().getStringExtra("personalId");
        name = getIntent().getStringExtra("name");
        gender = getIntent().getStringExtra("gender");

        welcomeTextView.setText("Üdv, " + name);

        signOutConstraintLayout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CardiMainMenuActivity.this, MainActivity.class);
            intent.putExtra("KEY", KEY);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        if (gender != null && gender.equalsIgnoreCase("nő")) {
            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_female_doctor);
        } else if (gender != null && gender.equalsIgnoreCase("férfi")) {
            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_male_doctor);
        }
        requestsSize();

        String[] months = {
                "Január", "Február", "Március", "Április", "Május", "Június",
                "Július", "Augusztus", "Szeptember", "Október", "November", "December"
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String monthName = months[month];
        selectedDay = year + ". " + monthName + " " + dayOfMonth + ".";

        loadAppointmentsInRecyclerView();

        // sportoló megtekintő activity megnyitása
        viewPatientsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardiMainMenuActivity.this, CardiPatientListActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        giveAppointmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardiMainMenuActivity.this, CardiAppointmentActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        registerNewPatientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardiMainMenuActivity.this, CardiRegisterNewPatientActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiMainMenuActivity.this, CardiProfileActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        //info megjelenítése
        infoLinearLayout.setOnClickListener(v -> {
            onInfoButtonClick();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiMainMenuActivity.this, CardiSettingsActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    // leírás létrehozása
    private void initializeTapTargets() {
        Context context = this;
        int backgroundColor = getThemeColor(context, androidx.appcompat.R.attr.colorPrimaryDark);
        int textColor = getThemeColor(context, com.google.android.material.R.attr.backgroundColor);

        TapTargetSequence sequence = new TapTargetSequence(CardiMainMenuActivity.this)
                .targets(
                        TapTarget.forView(viewPatientsCard, "Sportolók", "Itt láthatja az összes regisztrált sportolót.")
                                .outerCircleColorInt(backgroundColor)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(22)
                                .titleTextColorInt(textColor)
                                .descriptionTextSize(16)
                                .descriptionTextColorInt(textColor)
                                .cancelable(true)
                                .tintTarget(false),

                        TapTarget.forView(giveAppointmentCard, "Időpontok", "Itt tud időpontot foglalni a sportolók számára.")
                                .outerCircleColorInt(backgroundColor)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(22)
                                .titleTextColorInt(textColor)
                                .descriptionTextSize(16)
                                .descriptionTextColorInt(textColor)
                                .cancelable(true)
                                .tintTarget(false),

                        TapTarget.forView(registerNewPatientCard, "Új felhasználó", "Itt tud új felhasználót regisztrálni.")
                                .outerCircleColorInt(backgroundColor)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(22)
                                .titleTextColorInt(textColor)
                                .descriptionTextSize(16)
                                .descriptionTextColorInt(textColor)
                                .cancelable(true)
                                .tintTarget(false),

                        TapTarget.forView(signOutConstraintLayout, "Kijelentkezés", "Itt tud kijelentkezni a jelenlegi fiókjából.")
                                .outerCircleColorInt(backgroundColor)
                                .targetCircleColor(R.color.white)
                                .titleTextSize(22)
                                .titleTextColorInt(textColor)
                                .descriptionTextSize(16)
                                .descriptionTextColorInt(textColor)
                                .cancelable(true)
                                .tintTarget(false),

                        TapTarget.forView(todaysAppointmentsTextView, "Mai időpontok", "Ez alatt láthatja felsorolva az aktuális napra tervezett időpontokat.")
                                .outerCircleColorInt(backgroundColor)
                                .targetCircleColorInt(backgroundColor)
                                .titleTextSize(22)
                                .titleTextColorInt(textColor)
                                .descriptionTextSize(16)
                                .descriptionTextColorInt(textColor)
                                .cancelable(true)
                                .tintTarget(false),

                        TapTarget.forView(profileLinearLayout, "Profil", "Profiladatok és személyes információk megtekintése, szerkesztése.")
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
//                        Toast.makeText(CardiMainMenuActivity.this, "Az útmutató véget ért!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        Toast.makeText(CardiMainMenuActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                    }
                });

        sequence.start();
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    private void onInfoButtonClick() {
        initializeTapTargets();
    }

    // kérések számának lekérése
    private void requestsSize() {
        badgeTextView.setVisibility(View.GONE);

        FirebaseFirestore.getInstance().collection("requests")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Error listening for changes: ", error);
                        Toast.makeText(this, "Hiba a kérések figyelése közben.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        ArrayList<RequestItem> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            String comment = document.getString("comment");
                            String personalId = document.getString("personalId");
                            requests.add(new RequestItem(comment, personalId));
                        }

                        // Frissítsd a badgeTextView-t a lista méretével
                        int size = requests.size();
                        if (size != 0) {
                            badgeTextView.setText(String.valueOf(size));
                            badgeTextView.setVisibility(View.VISIBLE);
                        } else {
                            badgeTextView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // Mai időpontok lekérése - RecyclerView frissítése - korábbi időpontok
    private void loadAppointmentsInRecyclerView() {
        FirebaseFirestore.getInstance()
                .collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        ArrayList<TodaysAppointment> todayAppointmentList = new ArrayList<>();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        AtomicInteger processedDocuments = new AtomicInteger(); // Inicializálás itt!
                        int totalDocuments = task.getResult().size();
                        List<TodaysAppointment> tempListForSorting = new ArrayList<>(); // Inicializálás itt!

                        if (totalDocuments == 0) {
                            noAppointmentTextView.setVisibility(View.VISIBLE);
                            appointmentListRecyclerView.setVisibility(View.GONE);
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FirebaseFirestore.getInstance().collection("users")
                                    .whereEqualTo("personalId", document.getString("personalId"))
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        String userName = "";
                                        if (task2.isSuccessful()) {
                                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                String name = document2.getString("name");
                                                if (name != null) {
                                                    userName = name;
                                                    break;
                                                }
                                            }
                                        } else {
                                            Log.e("Firebase", "Error getting user data: ", task2.getException());
                                            userName = "N/A";
                                        }

                                        String user = document.getString("personalId") + " - " + userName;
                                        TodaysAppointment appointment = new TodaysAppointment(
                                                user,
                                                document.getString("day"),
                                                document.getString("time")
                                        );
                                        tempListForSorting.add(appointment);

                                        processedDocuments.getAndIncrement();

                                        if (processedDocuments.get() == totalDocuments) {
                                            // Rendezés az óra szerint
                                            Collections.sort(tempListForSorting, (a1, a2) -> {
                                                try {
                                                    String time1 = a1.getTime().split("-")[0].trim();
                                                    String time2 = a2.getTime().split("-")[0].trim();
                                                    Date date1 = timeFormat.parse(time1);
                                                    Date date2 = timeFormat.parse(time2);
                                                    return date1.compareTo(date2);
                                                } catch (ParseException e) {
                                                    Log.e("AppointmentSorting", "Hiba az időpontok összehasonlításakor: " + e.getMessage());
                                                    return 0;
                                                }
                                            });

                                            if (tempListForSorting.isEmpty()) {
                                                noAppointmentTextView.setVisibility(View.VISIBLE);
                                                appointmentListRecyclerView.setVisibility(View.GONE);
                                            } else {
                                                noAppointmentTextView.setVisibility(View.GONE);
                                                appointmentListRecyclerView.setVisibility(View.VISIBLE);

                                                appointmentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                                                TodaysAppointmentListAdapter adapter = new TodaysAppointmentListAdapter((ArrayList<TodaysAppointment>) tempListForSorting);
                                                appointmentListRecyclerView.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }

                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("theme_changed", false)) {
            recreate();
        }
    }

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private void handleMessage() {
        recreate();
    }
}