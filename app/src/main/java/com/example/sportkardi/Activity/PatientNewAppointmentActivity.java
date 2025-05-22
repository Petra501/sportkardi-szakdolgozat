package com.example.sportkardi.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import com.example.sportkardi.Calendar.CalendarView;
import com.example.sportkardi.R;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PatientNewAppointmentActivity extends AppCompatActivity {
    private static final String TAG = PatientNewAppointmentActivity.class.getName();
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

    private ConstraintLayout backConstraintLayout, sendConstraintLayout, appointmentFieldsConstraintLayout;
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private CalendarView calendarView;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView messageTextView;
    private FloatingActionButton addBloodPressureFAB;

    private String personalId, name, selectedDay;
    private Map<String, Integer> dayToAppointmentsCount = new HashMap<>();
    private ArrayList<String> appointments = new ArrayList<>();
    private Map<String, String> specialDays = new HashMap<>();
    private List<Date> recurringDates = new ArrayList<>();
    private List<Date> recurringDatesYes = new ArrayList<>();

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
        setContentView(R.layout.activity_patient_new_appointment);
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
        calendarView = findViewById(R.id.calendarView);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);
        addBloodPressureFAB = findViewById(R.id.addBloodPressureFAB);

        appointmentFieldsConstraintLayout = findViewById(R.id.appointmentFieldsConstraintLayout);
        sendConstraintLayout = findViewById(R.id.sendConstraintLayout);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        messageTextView = findViewById(R.id.messageTextView);

        backConstraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientNewAppointmentActivity.this, PatientAppointmentActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int color = typedValue.data;
        autoCompleteTextView.setDropDownBackgroundDrawable(new ColorDrawable(color));


        for (int hour = 8; hour < 16; hour++) {
            String timeRange = hour + ":00-" + (hour + 1) + ":00";
            appointments.add(timeRange);
        }

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

        messageTextView.setText("");
        autoCompleteTextView.setText("");
        sendConstraintLayout.setEnabled(false);
        sendConstraintLayout.setBackgroundResource(R.drawable.ic_check_disable);

        //főoldal
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientNewAppointmentActivity.this, PatientProfileActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        // új mérés activity megnyitása
        addBloodPressureFAB.setOnClickListener(v -> {
            Intent intent = new Intent(PatientNewAppointmentActivity.this, PatientBloodPressureActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(PatientNewAppointmentActivity.this)
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

                            TapTarget.forView(autoCompleteTextView, "Időpont választás", "Itt tudja kiválasztani az adott naphoz tartozó szabad időpontokat.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(messageTextView, "Megjegyzés", "Ide tud opcionálisan megjegyzést írni az időpontfoglaláshoz, melyet az egészségügyi dolgozók láthatnak.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(sendConstraintLayout, "Küldés", "A zöld repülőre nyomva tudja elküldeni a foglalását. A repülő csak akkor lesz aktív (vagyis zöld színű), ha adott meg időpontot.")
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
                            Toast.makeText(PatientNewAppointmentActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientNewAppointmentActivity.this, PatientSettingsActivity.class);
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

    // Ellenőrizzük, hogy nem szombat vagy vasárnap a mai nap
    private boolean isWeekday(int year, int month, int dayOfMonth) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        return dayOfWeek != java.util.Calendar.SATURDAY && dayOfWeek != java.util.Calendar.SUNDAY;
    }

    // Hétvége és hétköznap kezelése
    private void handleDaySelection(int year, int month, int dayOfMonth) {
        TextView weekendMessageTextView = findViewById(R.id.weekendMessageTextView);
        ConstraintLayout appointmentFieldsConstraintLayout = findViewById(R.id.appointmentFieldsConstraintLayout);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, dayOfMonth);

        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.add(Calendar.DAY_OF_MONTH, 1);
        tomorrowCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tomorrowCalendar.set(Calendar.MINUTE, 0);
        tomorrowCalendar.set(Calendar.SECOND, 0);
        tomorrowCalendar.set(Calendar.MILLISECOND, 0);

        if (selectedCalendar.before(tomorrowCalendar)) {
            weekendMessageTextView.setText("Erre a napra már nem tud időpontot foglalni!");
            weekendMessageTextView.setVisibility(View.VISIBLE);
            appointmentFieldsConstraintLayout.setVisibility(View.GONE);
        } else if (isWeekday(year, month, dayOfMonth)) {
            weekendMessageTextView.setVisibility(View.GONE);
            appointmentFieldsConstraintLayout.setVisibility(View.VISIBLE);
            refreshAppointments(selectedDay);
        } else {
            weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
            weekendMessageTextView.setVisibility(View.VISIBLE);
            appointmentFieldsConstraintLayout.setVisibility(View.GONE);
        }

        for (Date recurringDate : recurringDates) {
            if (stringToDate(selectedDay).getDay() == recurringDate.getDay()) {
                if (stringToDate(selectedDay).after(recurringDate)) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentFieldsConstraintLayout.setVisibility(View.GONE);
                }
            }
        }

        for (Date recurringDate : recurringDatesYes) {
            if (stringToDate(selectedDay).getDay() == recurringDate.getDay()) {
                if (stringToDate(selectedDay).after(recurringDate)) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentFieldsConstraintLayout.setVisibility(View.VISIBLE);
                    refreshAppointments(selectedDay);
                }
            }
        }

        for (String key : specialDays.keySet()) {
            String rule = specialDays.get(key);

            if (selectedDay.equals(key) && !selectedCalendar.before(tomorrowCalendar)) {
                if (rule.equals("no-appointment")) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentFieldsConstraintLayout.setVisibility(View.GONE);
                } else if (rule.equals("allow-only")) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentFieldsConstraintLayout.setVisibility(View.VISIBLE);
                    refreshAppointments(selectedDay);
                } else if (rule.equals("recurring-no")) {
                    weekendMessageTextView.setText("Ezen a napon nincs rendelés!");
                    weekendMessageTextView.setVisibility(View.VISIBLE);
                    appointmentFieldsConstraintLayout.setVisibility(View.GONE);
                } else if (rule.equals("recurring-yes")) {
                    weekendMessageTextView.setVisibility(View.GONE);
                    appointmentFieldsConstraintLayout.setVisibility(View.VISIBLE);
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
            appointmentFieldsConstraintLayout.setVisibility(View.GONE);
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

    // Appointment lista firssítése
    private void refreshAppointments(String selectedDay) {
        autoCompleteTextView.setText("");
        sendConstraintLayout.setEnabled(false);
        sendConstraintLayout.setBackgroundResource(R.drawable.ic_check_disable);

        ArrayList<String> times = new ArrayList<>(appointments);
        FirebaseFirestore.getInstance().collection("appointment")
                .whereEqualTo("day", selectedDay)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dbTime = document.getString("time");

                            times.remove(dbTime);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                R.layout.dropdown_item,
                                times
                        );
                        autoCompleteTextView.setAdapter(adapter);

                        // Dropdown magasság korlátozása (3 elem)
                        int itemHeight = (int) getResources().getDimension(android.R.dimen.app_icon_size);
                        int maxVisibleItems = 4;
                        int numberOfItems = times.size();

                        // Ha a listaelemek száma kisebb, mint a maximum, akkor az összes elem megjelenítése
                        autoCompleteTextView.setDropDownHeight(itemHeight * Math.min(numberOfItems, maxVisibleItems));

                        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                                String selectedTime = adapter.getItem(position);

                                autoCompleteTextView.setText(selectedTime);
                                sendConstraintLayout.setEnabled(true);
                                sendConstraintLayout.setBackgroundResource(R.drawable.ic_check);
                                sendConstraintLayout.setOnClickListener(v -> {
                                    showRequestAppointmentPopup(selectedDay, selectedTime);
                                });
                        });

                    } else {
                        Log.e(TAG, "Error getting appointments: ", task.getException());
                    }
                });
    }

    // Foglalást megerősítő popup
    private void showRequestAppointmentPopup(String selectedDay, String selectedTime) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_request_appointment, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        showDimBackground();
        popupWindow.setOnDismissListener(this::hideDimBackground);

        TextView dayTextView = popupView.findViewById(R.id.dayTextView);
        TextView timeTextView = popupView.findViewById(R.id.timeTextView);
        TextView commentTextView = popupView.findViewById(R.id.commentTextView);
        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button requestButton = popupView.findViewById(R.id.requestButton);

        String message;

        dayTextView.setText(selectedDay);
        timeTextView.setText(selectedTime);
        cancelButton.setOnClickListener(v -> popupWindow.dismiss());

        if (messageTextView.getText().length() != 0) {
            commentTextView.setText(messageTextView.getText());
            message = messageTextView.getText().toString();
        } else {
            message = "Nincs";
            commentTextView.setText("Nincs");
        }

        requestButton.setOnClickListener(v -> {
            AtomicBoolean allowedTime = new AtomicBoolean(true);
            Map<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("personalId", personalId);
            appointmentData.put("time", selectedTime);
            appointmentData.put("day", selectedDay);
            appointmentData.put("message", message);

            FirebaseFirestore.getInstance().collection("appointment")
                    .whereEqualTo("day", selectedDay)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String dbTime = document.getString("time");

                                //Időpont utolsó ellenőrzése
                                if (dbTime.equals(selectedTime)) {
                                    Toast.makeText(this, "Ez az időpont foglalt!", Toast.LENGTH_SHORT).show();
                                    allowedTime.set(false);
                                    popupWindow.dismiss();
                                }
                            }

                            if (allowedTime.get()) {
                                showSuccessDialog();
                                saveAppointment(appointmentData);
                                popupWindow.dismiss();
                            }
                        } else {
                            Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        popupWindow.showAtLocation(appointmentFieldsConstraintLayout, Gravity.CENTER, 0, 0);
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
                        Toast.makeText(this, "Hiba: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //Firebase-be mentés
    private void saveAppointment(Map<String, Object> appointmentData) {
        FirebaseFirestore.getInstance().collection("appointment")
                .add(appointmentData)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Az időpontfoglalás sikertelen!", Toast.LENGTH_SHORT).show();
                });

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                        DocumentSnapshot document = userTask.getResult().getDocuments().get(0);

                        FirebaseFirestore.getInstance().collection("users")
                                .document(document.getId())
                                .update("hasAppointmentRequest", true)
                                .addOnCompleteListener(updateTask -> {
                                    if (!updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Hiba: " + updateTask.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Felhasználó nem található!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Halványító réteg a popup mögé
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

    // Sikeres foglalás dialog
    private void showSuccessDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientNewAppointmentActivity.this).inflate(R.layout.popup_successful_request, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientNewAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                autoCompleteTextView.setText("");
                autoCompleteTextView.setFocusable(false);
                messageTextView.setText("");
                messageTextView.setFocusable(false);
                changeMessage();
                Intent intent = new Intent(PatientNewAppointmentActivity.this, PatientAppointmentActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    // Üzenet küldése a főoldalnak, hogy változás történt
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