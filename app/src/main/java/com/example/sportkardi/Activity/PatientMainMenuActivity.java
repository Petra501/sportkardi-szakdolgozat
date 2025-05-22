package com.example.sportkardi.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import com.example.sportkardi.Model.MyAppointment;
import com.example.sportkardi.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PatientMainMenuActivity extends AppCompatActivity {
    private static final String TAG = PatientMainMenuActivity.class.getName();
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

    private TextView welcomeTextView, dateTextView, timeTextView, noNextAppointmentTextView;
    private TextView previousPressureTextView, counterTextView, assessmentTextView;
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout, viewMyDataSheetCard, complaintManageCard, askAppointmentCard, nextAppointmentLinearLayout;
    private FloatingActionButton addBloodPressureFAB;
    private ConstraintLayout signOutConstraintLayout, profileConstraintLayout;

    private String personalId, name, gender;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_CUSTOM_MESSAGE".equals(intent.getAction())) {
                recreate();
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
        setContentView(R.layout.activity_patient_main_menu);
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
        gender = getIntent().getStringExtra("gender");

        if (key != 44) {
            finish();
        }

        //üzenetek megkapása - BroadcastReceiver regiszrtálása
        LocalBroadcastManager.getInstance(this).registerReceiver(
                messageReceiver,
                new IntentFilter("ACTION_CUSTOM_MESSAGE")
        );

        signOutConstraintLayout = findViewById(R.id.signOutConstraintLayout);
        profileConstraintLayout = findViewById(R.id.profileConstraintLayout);
        welcomeTextView = findViewById(R.id.welcomeTextView);

        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        nextAppointmentLinearLayout = findViewById(R.id.nextAppointmentLinearLayout);
        noNextAppointmentTextView = findViewById(R.id.noNextAppointmentTextView);

        previousPressureTextView = findViewById(R.id.previousPressureTextView);
        counterTextView = findViewById(R.id.counterTextView);
        assessmentTextView = findViewById(R.id.assessmentTextView);

        viewMyDataSheetCard = findViewById(R.id.viewMyDataSheetCard);
        complaintManageCard = findViewById(R.id.complaintManageCard);
        askAppointmentCard = findViewById(R.id.askAppointmentCard);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);
        addBloodPressureFAB = findViewById(R.id.addBloodPressureFAB);

        welcomeTextView.setText("Üdv, " + name);

        noNextAppointmentTextView.setVisibility(View.VISIBLE);
        nextAppointmentLinearLayout.setVisibility(View.GONE);

        signOutConstraintLayout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PatientMainMenuActivity.this, MainActivity.class);
            intent.putExtra("KEY", KEY);
            startActivity(intent);
            finish();
        });

        if (gender != null && gender.equalsIgnoreCase("nő")) {
            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_female);
        } else if (gender != null && gender.equalsIgnoreCase("férfi")) {
            profileConstraintLayout.setBackgroundResource(R.drawable.ic_profile_male);
        }

        previousPressureTextView.setText("-/-");
        assessmentTextView.setText("értékelése: nincs");
        counterTextView.setText("-");

        checkAppointment();
        incomingAppointment();
        updateBloodPressureCards();

        viewMyDataSheetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientMainMenuActivity.this, PatientDataSheetActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

//        complaintManageCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PatientMainMenuActivity.this, CardiRegisterNewPatientActivity.class);
//                intent.putExtra("KEY", KEY);
//                startActivity(intent);
//            }
//        });

        //időpontkérő activity megnyitása
        askAppointmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientMainMenuActivity.this, PatientAppointmentActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //bottomappbar - profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientMainMenuActivity.this, PatientProfileActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // új mérés activity megnyitása
        addBloodPressureFAB.setOnClickListener(v -> {
            Intent intent = new Intent(PatientMainMenuActivity.this, PatientBloodPressureActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        infoLinearLayout.setOnClickListener(v -> {
            Context context = this;
            int backgroundColor = getThemeColor(context, androidx.appcompat.R.attr.colorPrimaryDark);
            int textColor = getThemeColor(context, com.google.android.material.R.attr.backgroundColor);

            TapTargetSequence sequence = new TapTargetSequence(PatientMainMenuActivity.this)
                    .targets(
                            TapTarget.forView(viewMyDataSheetCard, "Adatlapom", "Itt tudja megtekinteni és szerkeszteni a kitöltött adatlapját.")
                                    .outerCircleColorInt(backgroundColor)
                                    .targetCircleColor(R.color.white)
                                    .titleTextSize(22)
                                    .titleTextColorInt(textColor)
                                    .descriptionTextSize(16)
                                    .descriptionTextColorInt(textColor)
                                    .cancelable(true)
                                    .tintTarget(false),

                            TapTarget.forView(askAppointmentCard, "Időpontjaim", "Ezen keresztül tudja kezelni az időpontjait.")
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
                                    .tintTarget(false),

                            TapTarget.forView(signOutConstraintLayout, "Kijelentkezés", "Itt tud kijelentkezni a jelenlegi fiókjából.")
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
                            // opcionálisan lépésenként lehet logolni
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            Toast.makeText(PatientMainMenuActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientMainMenuActivity.this, PatientSettingsActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private int getThemeColor(Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    // Vérnyomás összefoglaló kártyák értékeinek megadása
    @SuppressLint("SetTextI18n")
    private void updateBloodPressureCards() {
        FirebaseFirestore.getInstance().collection("bloodPressure")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                        List<DocumentSnapshot> documents = userTask.getResult().getDocuments();

                        // Vérnyomásmérések idő szerint rendezése (legutóbbi az utolsó)
                        documents.sort((d1, d2) -> {
                            Timestamp t1 = d1.getTimestamp("time");
                            Timestamp t2 = d2.getTimestamp("time");
                            return t2.compareTo(t1);
                        });

                        // Legutóbbi mérés beállítása
                        DocumentSnapshot latest = documents.get(0);
                        Long systole = latest.getLong("systole");
                        Long diastole = latest.getLong("diastole");

                        if (systole != null && diastole != null) {
                            previousPressureTextView.setText(systole + "/" + diastole);

                            if (systole <= 110 || diastole < 70) {
                                assessmentTextView.setText("értékelése: alacsony");
                            } else if (systole > 110 && systole < 130 && diastole > 70 && diastole < 90 ){
                                assessmentTextView.setText("értékelése: jó");
                            } else if (systole >= 130 || diastole > 90) {
                                assessmentTextView.setText("értékelése: magas");
                            }
                        } else {
                            previousPressureTextView.setText("Nincs adat");
                            assessmentTextView.setText("értékelése: nincs");
                        }

                        // Mai nap méréseinek számolása
                        int todayCount = 0;
                        Calendar today = Calendar.getInstance();
                        for (DocumentSnapshot doc : documents) {
                            Timestamp time = doc.getTimestamp("time");
                            if (time != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(time.toDate());

                                if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                                    todayCount++;
                                }
                            }
                        }

                        int remaining = Math.max(0, 3 - todayCount);
                        counterTextView.setText("" + remaining);
                    } else {
                        previousPressureTextView.setText("-/-");
                        assessmentTextView.setText("értékelése: nincs");
                        counterTextView.setText("3");
                    }
                });

    }

    // Leellenőrzi, hogy van-e törölt appointment-je a felhasználónak
    private void checkAppointment() {
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Boolean hasAppointmentRequest = document.getBoolean("hasAppointmentRequest");

                            if (Boolean.TRUE.equals(hasAppointmentRequest)) {
                                FirebaseFirestore.getInstance()
                                        .collection("appointment")
                                        .whereEqualTo("personalId", personalId)
                                        .get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd.", Locale.getDefault());
                                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                                Date currentDate = new Date(); // Mostani dátum

                                                // Csak a mai dátumot vesszük figyelembe
                                                Calendar currentCalendar = Calendar.getInstance();
                                                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                                currentCalendar.set(Calendar.MINUTE, 0);
                                                currentCalendar.set(Calendar.SECOND, 0);
                                                currentCalendar.set(Calendar.MILLISECOND, 0);
                                                Date todayDateOnly = currentCalendar.getTime();

                                                ArrayList<MyAppointment> myAppointmentList = new ArrayList<>();

                                                int count = 0;

                                                for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                    String appointmentDay = document2.getString("day");

                                                    try {
                                                        Date parsedDay = dateFormat.parse(appointmentDay);

                                                        // Ellenőrizzük, hogy a dátum a mai nap vagy későbbi
                                                        if (parsedDay != null && (parsedDay.after(todayDateOnly) || parsedDay.equals(todayDateOnly))) {
                                                            count++;
                                                        }

                                                    } catch (ParseException e) {
                                                        Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                if (count == 0){
                                                    showDeletedAppointmentDialog();
                                                }

                                            } else {
                                                Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Törölt időpont dialog
    private void showDeletedAppointmentDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientMainMenuActivity.this).inflate(R.layout.dialog_deleted_appointment, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientMainMenuActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setOnDismissListener(dialog -> refreshUserDatas());

        alertDialog.show();
    }

    // Frissíti a felhasználó hasAppointmentRequest értékét, ha nincs időpontja
    public void refreshUserDatas(){
        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && !userTask.getResult().isEmpty()) {
                        DocumentSnapshot document = userTask.getResult().getDocuments().get(0);

                        // Frissítés
                        FirebaseFirestore.getInstance().collection("users")
                                .document(document.getId())
                                .update("hasAppointmentRequest", false)
                                .addOnCompleteListener(updateTask -> {
                                    if (!updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Hiba: " + updateTask.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Hiba a felhasználó lekérésekor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Leellenőrzi van-e következő időpont
    private void incomingAppointment() {
        FirebaseFirestore.getInstance()
                .collection("appointment")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd.", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Date currentDate = new Date(); // Mostani dátum

                        // Csak a mai dátumot vesszük figyelembe
                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        currentCalendar.set(Calendar.MINUTE, 0);
                        currentCalendar.set(Calendar.SECOND, 0);
                        currentCalendar.set(Calendar.MILLISECOND, 0);
                        Date todayDateOnly = currentCalendar.getTime();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentDay = document.getString("day");
                            String appointmentTime = document.getString("time");

                            try {
                                Date parsedDay = dateFormat.parse(appointmentDay);

                                // Ellenőrizzük, hogy a dátum a mai nap vagy későbbi
                                if (parsedDay != null && (parsedDay.after(todayDateOnly) || parsedDay.equals(todayDateOnly))) {
                                    String[] timeRange = appointmentTime.split("-");
                                    String appointmentStartTime = timeRange[0]; // Például "10:00"
                                    Date parsedStartTime = timeFormat.parse(appointmentStartTime);

                                    // Mostani időpont (csak az óra és perc)
                                    String currentTimeStr = timeFormat.format(currentDate);
                                    Date currentTime = timeFormat.parse(currentTimeStr);

                                    // Ellenőrizzük, hogy a mai napon későbbi időpont vagy jövőbeli dátum van-e
                                    if (parsedDay.after(todayDateOnly) || (parsedDay.equals(todayDateOnly) && parsedStartTime != null && parsedStartTime.after(currentTime))) {
                                        noNextAppointmentTextView.setVisibility(View.GONE);
                                        nextAppointmentLinearLayout.setVisibility(View.VISIBLE);
                                        timeTextView.setText(appointmentTime);
                                        dateTextView.setText(appointmentDay);
                                    }
                                }

                            } catch (ParseException e) {
                                Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSavedTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(THEME_KEY, THEME_DARK_BLUE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }
}