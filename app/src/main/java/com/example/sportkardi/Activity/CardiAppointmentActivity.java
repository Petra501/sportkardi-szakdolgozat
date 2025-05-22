package com.example.sportkardi.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Adapter.AppointmentListAdapter;
import com.example.sportkardi.Calendar.CalendarView;
import com.example.sportkardi.Model.Appointment;
import com.example.sportkardi.R;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class CardiAppointmentActivity extends AppCompatActivity {
    private static final String TAG = CardiAppointmentActivity.class.getName();
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

    private ConstraintLayout backConstraintLayout, setCalendarConstraintLayout;
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private RecyclerView appointmentListRecyclerView;
    private CalendarView calendarView;

    private String personalId, name, selectedDay;
    private Map<String, Integer> dayToAppointmentsCount = new HashMap<>();
    private Map<String, String> specialDays = new HashMap<>();
    private List<Date> recurringDates = new ArrayList<>();
    private List<Date> recurringDatesYes = new ArrayList<>();

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
        setContentView(R.layout.activity_cardi_appointment);
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
        setCalendarConstraintLayout = findViewById(R.id.setCalendarConstraintLayout);
        appointmentListRecyclerView = findViewById(R.id.appointmentListRecyclerView);
        calendarView = findViewById(R.id.calendarView);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);

        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        setCalendarConstraintLayout.setOnClickListener(v -> showPopup());

        ArrayList<Appointment> appointments = new ArrayList<>();
        for (int hour = 8; hour < 16; hour++) {
            String timeRange = hour + ":00-" + (hour + 1) + ":00";
            appointments.add(new Appointment(timeRange));
        }

        appointmentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AppointmentListAdapter adapter = new AppointmentListAdapter(appointments);
        appointmentListRecyclerView.setAdapter(adapter);
        adapter.setOnAppointmentClickListener(appointment -> showSportSelectionDialog(appointment));
        adapter.setDeletetmentClickListener(appointment -> showDeleteDialog(appointment));

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
        getSpecialAppointmentDays();
        handleDaySelection(year, month, dayOfMonth);

        calendarView.setOnDateChangeListener((newYear, newMonth, newDayOfMonth) -> {
            String newMonthName = months[newMonth];
            selectedDay = newYear + ". " + newMonthName + " " + newDayOfMonth + ".";
            handleDaySelection(newYear, newMonth, newDayOfMonth);
            refreshAppointments(selectedDay);
        });

        fetchAppointmentsCount();

        //főoldal
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiAppointmentActivity.this, CardiProfileActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(CardiAppointmentActivity.this)
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

                            TapTarget.forView(setCalendarConstraintLayout, "Rendelési idő szerkesztése", "Itt tudja beállítani, hogy mely napokon legyen vagy ne legyen rendelés.")
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
                            Toast.makeText(CardiAppointmentActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(CardiAppointmentActivity.this, CardiSettingsActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
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

    // Ellenőrizzük, hogy nem szombat vagy vasárnap a mai nap
    private boolean isWeekday(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY;
    }

    // Hétvége és hétköznap kezelése
    private void handleDaySelection(int year, int month, int dayOfMonth) {
        TextView weekendMessageTextView = findViewById(R.id.weekendMessageTextView);
        RecyclerView appointmentListRecyclerView = findViewById(R.id.appointmentListRecyclerView);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, dayOfMonth);

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1);
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowCalendar.set(Calendar.MINUTE, 0);
        tomorrowCalendar.set(Calendar.SECOND, 0);
        tomorrowCalendar.set(Calendar.MILLISECOND, 0);

        if (selectedCalendar.before(tomorrowCalendar)) {
            weekendMessageTextView.setText("Erre a napra már nem tud időpontot adni!");
            weekendMessageTextView.setVisibility(View.VISIBLE);
            appointmentListRecyclerView.setVisibility(View.GONE);
        } else if (isWeekday(year, month, dayOfMonth)) {
            weekendMessageTextView.setVisibility(View.GONE);
            appointmentListRecyclerView.setVisibility(View.VISIBLE);
            refreshAppointments(selectedDay);
        } else {
            weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
            weekendMessageTextView.setVisibility(View.VISIBLE);
            appointmentListRecyclerView.setVisibility(View.GONE);
        }

        for (Date recurringDate : recurringDates) {
            if (stringToDate(selectedDay).getDay() == recurringDate.getDay()) {
                if (stringToDate(selectedDay).after(recurringDate)) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentListRecyclerView.setVisibility(View.GONE);
                }
            }
        }

        for (Date recurringDate : recurringDatesYes) {
            if (stringToDate(selectedDay).getDay() == recurringDate.getDay()) {
                if (stringToDate(selectedDay).after(recurringDate)) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentListRecyclerView.setVisibility(View.VISIBLE);
                    refreshAppointments(selectedDay);
                }
            }
        }

        for (String key : specialDays.keySet()) {
            String rule = specialDays.get(key);

            if (selectedDay.equals(key)) {
                if (rule.equals("no-appointment")) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentListRecyclerView.setVisibility(View.GONE);
                } else if (rule.equals("allow-only")) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentListRecyclerView.setVisibility(View.VISIBLE);
                    refreshAppointments(selectedDay);
                } else if (rule.equals("recurring-no")) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentListRecyclerView.setVisibility(View.GONE);
                } else if (rule.equals("recurring-yes")) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentListRecyclerView.setVisibility(View.VISIBLE);
                    refreshAppointments(selectedDay);
                }
            }

            if (rule.equals("recurring-no")) {
                recurringDates.add(stringToDate(key));
            } else if (rule.equals("recurring-yes")) {
                recurringDatesYes.add(stringToDate(key));
            }
        }

        List<String> publicHolidays = Arrays.asList(
                "0 1",  // Újév
                "2 15", // Nemzeti ünnep
                "4 1", // Munka ünnepe
                "7 20", // Államalapítás
                "9 23", // Forradalom napja
                "10 1", // Mindenszentek
                "11 25", // Karácsony
                "11 26"  // Karácsony másnapja
        );

        String currentDateKey = month + " " + dayOfMonth;
        if (publicHolidays.contains(currentDateKey)) {
            weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
            weekendMessageTextView.setVisibility(View.VISIBLE);
            appointmentListRecyclerView.setVisibility(View.GONE);
        }
    }

    private Date stringToDate(String key) {
        Date keyDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu", "HU"));
            keyDate = dateFormat.parse(key);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return keyDate;
    }

    // Appointment lista firssítése
    private void refreshAppointments(String selectedDay) {
        updateAllAppointmentInList();
        FirebaseFirestore.getInstance().collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String personalId = document.getString("personalId");
                            String time = document.getString("time");
                            String message = document.getString("message");
                            updateAppointmentInRecyclerView(time, personalId, message);
                        }
                    } else {
                        Log.e(TAG, "Error getting appointments: ", task.getException());
                    }
                });
    }

    // RecyclerView frissítése - foglalt appointment
    private void updateAppointmentInRecyclerView(String time, String personalId, String message) {
        AppointmentListAdapter adapter = (AppointmentListAdapter) appointmentListRecyclerView.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                Appointment appointment = adapter.getItem(i);
                if (appointment.getTimeRange().equals(time)) {
                    final int position = i;

                    FirebaseFirestore.getInstance().collection("users")
                            .whereEqualTo("personalId", personalId)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String userName = "";
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String name = document.getString("name");
                                        if (name != null) {
                                            userName = name;
                                            break;
                                        }
                                    }
                                    // Itt már biztosan van értéke a userName-nek
                                    appointment.setPersonalId(personalId + " - " + userName);
                                    appointment.setMessage(message);
                                    adapter.notifyItemChanged(position);
                                } else {
                                    Log.e("Firebase", "Error getting documents: ", task.getException());
                                }
                            });
                    break;
                }
            }
        }
    }

    // RecyclerView frissítése - összes appointment
    private void updateAllAppointmentInList() {
        AppointmentListAdapter adapter = (AppointmentListAdapter) appointmentListRecyclerView.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                Appointment appointment = adapter.getItem(i);
                appointment.setPersonalId("-");
                appointment.setMessage("Nincs");
                adapter.notifyItemChanged(i);
            }
        }
    }

    // Firebase-ben található appointment-ok számának lekérése
    private void fetchAppointmentsCount() {
        FirebaseFirestore.getInstance().collection("appointment")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String day = document.getString("day");
                            if (day != null) {
                                dayToAppointmentsCount.put(day, dayToAppointmentsCount.getOrDefault(day, 0) + 1);
                            }
                        }
                        // Az adapter frissítése, hogy tükrözze a színkódokat
                        if (calendarView != null) {
                            calendarView.invalidate();
                        }
                        calendarView.ColorCells(dayToAppointmentsCount);
                    } else {
                        Log.e(TAG, "Error getting appointments count: ", task.getException());
                    }
                });
    }

    // Rendelés szerkesztő dialog
    @SuppressLint("SetTextI18n")
    private void showPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_edit_calendar, null);

        TextView dayTextView = view.findViewById(R.id.dayTextView);
        RadioGroup periodRadioGroup = view.findViewById(R.id.periodRadioGroup);
        RadioButton openPeriodRadioButton = view.findViewById(R.id.openPeriodRadioButton);
        RadioGroup repeatRadioGroup = view.findViewById(R.id.repeatRadioGroup);
        RadioButton noRepeatPeriodRadioButton = view.findViewById(R.id.noRepeatPeriodRadioButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);
        String weekday = showDayOfSelectedDay();

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        dayTextView.setText(selectedDay + ", " + weekday);
        openPeriodRadioButton.setChecked(true);
        noRepeatPeriodRadioButton.setChecked(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rule = "";

                int selectedPeriodId = periodRadioGroup.getCheckedRadioButtonId();
                RadioButton periodRadioButton = view.findViewById(selectedPeriodId);
                String period = periodRadioButton.getText().toString();

                int selectedRepeatId = repeatRadioGroup.getCheckedRadioButtonId();
                RadioButton repeatRadioButton = view.findViewById(selectedRepeatId);
                String repeat = repeatRadioButton.getText().toString();

                if (period.equals("ezen a napon van rendelés") && repeat.equals("csak egy nap")) {
                    rule = "allow-only";
                } else if (period.equals("ezen a napon van rendelés") && repeat.equals("minden héten ezen a napon")) {
                    rule = "recurring-yes";
                } else if (period.equals("ezen a napon nincs rendelés") && repeat.equals("minden héten ezen a napon")) {
                    rule = "recurring-no";
                } else if (period.equals("ezen a napon nincs rendelés") && repeat.equals("csak egy nap")) {
                    rule = "no-appointment";
                }

                checkSpecialAppointmentDays(rule);
                alertDialog.dismiss();
                recreate();
            }
        });


        cancelButton.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
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

    // Speciális nap ellenőrzése
    private void checkSpecialAppointmentDays(String rule) {
        FirebaseFirestore.getInstance()
                .collection("specialDays")
                .document(selectedDay)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String dbRule = documentSnapshot.getString("rule");

                        if (dbRule.equals("no-appointment") && rule.equals("allow-only") ||
                                dbRule.equals("allow-only") && rule.equals("no-appointment") ||
                                dbRule.equals("recurring-no") && rule.equals("recurring-yes") ||
                                dbRule.equals("recurring-yes") && rule.equals("recurring-no")) {

                            deleteSpecialAppointmentDay();
                        } else {
                            if (rule.equals("no-appointment") || rule.equals("recurring-no")) {
                                checkAppointmentsOfDay(rule);
                            } else {
                                saveSpecialAppointmentDays(rule);
                            }
                        }

                    } else {
                        if (rule.equals("no-appointment") || rule.equals("recurring-no")) {
                            checkAppointmentsOfDay(rule);
                        } else {
                            saveSpecialAppointmentDays(rule);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void checkAppointmentsOfDay(String rule) {
        FirebaseFirestore.getInstance()
                .collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            saveSpecialAppointmentDays(rule);
                        } else {
                            showDeleteWarningDialog(rule);
                        }
                    } else {
                        Toast.makeText(this, "Hiba: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Appointment törlésre figyelmeztető dialog
    private void showDeleteWarningDialog(String rule) {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(CardiAppointmentActivity.this).inflate(R.layout.dialog_delete_appointments_warning, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        cancelButton.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSpecialDayAppointment();
                saveSpecialAppointmentDays(rule);
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    // Törli a speciális napok időpontjait
    private void deleteSpecialDayAppointment() {
        FirebaseFirestore.getInstance()
                .collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                FirebaseFirestore.getInstance()
                                        .collection("appointment")
                                        .document(documentId)
                                        .delete()
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Hiba a törlés során.", Toast.LENGTH_SHORT).show()
                                        );
                            }
                        }
                    } else {
                        Toast.makeText(this, "Hiba: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Speciális nap törlése adatbázisból
    private void deleteSpecialAppointmentDay() {
        FirebaseFirestore.getInstance()
                .collection("specialDays")
                .document(selectedDay)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    specialDays.remove(selectedDay);
                    calendarView.setSpecialDays(specialDays);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        recreate();
    }

    // Speciális nap mentése Firebase-be
    private void saveSpecialAppointmentDays(String rule) {
        FirebaseFirestore.getInstance()
                .collection("specialDays")
                .document(selectedDay)
                .set(Collections.singletonMap("rule", rule))
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
                });

        recreate();
    }

    // Speciális nap lekérése Firebase-ből
    private void getSpecialAppointmentDays() {
        FirebaseFirestore.getInstance()
                .collection("specialDays")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dbDay = document.getId();
                            String dbRule = document.getString("rule");
                            specialDays.put(dbDay, dbRule);
                        }
                        // Frissítsd a naptárat
                        calendarView.setSpecialDays(specialDays);
                    } else {
                        Toast.makeText(this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Megadja melyik napon van a kiválasztott nap
    private String showDayOfSelectedDay() {
        String weekDay = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu", "HU"));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("hu", "HU"));

        try {
            Date date = dateFormat.parse(selectedDay);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dayOfWeek = dayFormat.format(calendar.getTime());
            weekDay = dayOfWeek;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weekDay;
    }

    // Időpont adó dialog
    private void showSportSelectionDialog(Appointment appointment) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_sport_selection, null);

        AutoCompleteTextView autoCompleteTextView = dialogView.findViewById(R.id.autoCompleteTextView);
        TextView dayTextView = dialogView.findViewById(R.id.dayTextView);
        TextView timeTextView = dialogView.findViewById(R.id.timeTextView);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiAppointmentActivity.this);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();

        timeTextView.setText(appointment.getTimeRange());
        dayTextView.setText(selectedDay);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = typedValue.data;
        autoCompleteTextView.setDropDownBackgroundDrawable(new ColorDrawable(color));

        ArrayList<String> users = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("admin", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String personalId = document.getString("personalId");
                            boolean hasAppointmentRequest = document.getBoolean("hasAppointmentRequest") != null
                                    ? document.getBoolean("hasAppointmentRequest")
                                    : false;

                            if (name != null && personalId != null) {
                                String displayText = personalId + " - " + name;

                                users.add(displayText);
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                R.layout.dropdown_item,
                                users
                        );
                        autoCompleteTextView.setAdapter(adapter);
                        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                            String selectedId = adapter.getItem(position);

                            autoCompleteTextView.setText(selectedId);
                            appointment.setPersonalId(selectedId);
                            appointmentListRecyclerView.getAdapter().notifyDataSetChanged();
                        });
                    } else {
                        Log.e("Firebase", "Error getting documents: ", task.getException());
                    }
                });


        saveButton.findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedID = appointment.getPersonalId();
                String personalId = "";

                String[] parts = selectedID.split(" - ");
                if (parts.length > 0) {
                    personalId = parts[0];
                }

                String time = appointment.getTimeRange();
                String day = selectedDay;

                // Firebase-be mentés
                Map<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("personalId", personalId);
                appointmentData.put("time", time);
                appointmentData.put("day", day);

                // Firebase Firestore-ba való mentés
                FirebaseFirestore.getInstance().collection("appointment")
                        .add(appointmentData)
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Error adding appointment: ", e);
                        });

                FirebaseFirestore.getInstance().collection("users")
                        .whereEqualTo("personalId", personalId)
                        .get()
                        .addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                                DocumentSnapshot document = userTask.getResult().getDocuments().get(0);

                                // Frissítés
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(document.getId())
                                        .update("hasAppointmentRequest", true)
                                        .addOnCompleteListener(updateTask -> {
                                            if (!updateTask.isSuccessful()) {
                                                Log.e(TAG, "Hiba történt a frissítéskor: ", updateTask.getException());
                                            }
                                        });

                            } else {
                                Log.e(TAG, "Error getting user document");
                            }
                        });

                recreate();
                changeMessage();
                alertDialog.dismiss();
            }
        });

        cancelButton.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
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

    // Törlés dialog
    private void showDeleteDialog(Appointment appointment) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_delete, null);

        TextView personTextView = view.findViewById(R.id.personTextView);
        TextView timeTextView = view.findViewById(R.id.timeTextView);
        TextView dayTextView = view.findViewById(R.id.dayTextView);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        personTextView.setText(appointment.getPersonalId());
        dayTextView.setText(selectedDay);
        timeTextView.setText(appointment.getTimeRange());

        deleteButton.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment(appointment);
                alertDialog.dismiss();
            }
        });

        cancelButton.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
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

    // Időpont törlése adatbázisból
    private void deleteAppointment(Appointment appointment) {
        FirebaseFirestore.getInstance().collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dbPersonalId = document.getString("personalId");
                            String dbTime = document.getString("time");
                            String aPersonalId = appointment.getPersonalId().split(" - ")[0];

                            if (Objects.equals(dbPersonalId, aPersonalId) && Objects.equals(dbTime, appointment.getTimeRange())) {
                                document.getReference().delete()
                                        .addOnFailureListener(e -> Toast.makeText(this, "Hiba történt a törlés során", Toast.LENGTH_SHORT).show());
                            }
                        }

                        showSuccessfulDeleteDialog();
                    } else {
                        Toast.makeText(this, "Hiba: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Sikeres törlés dialog
    private void showSuccessfulDeleteDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(CardiAppointmentActivity.this).inflate(R.layout.dialog_successful_delete, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(CardiAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
                changeMessage();
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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

    // Halványító réteg eltüntetése a popup eltűnésével
    private void hideDimBackground() {
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        View dimView = rootView.findViewWithTag("dim_view");
        if (dimView != null) {
            rootView.removeView(dimView);
        }
    }

    // Üzenet küldése a főoldalnak frissítéshez
    private void changeMessage() {
        Intent intent = new Intent("ACTION_CUSTOM_MESSAGE");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        recreate();
    }

    // Aktuális téma lekérése
    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }
}