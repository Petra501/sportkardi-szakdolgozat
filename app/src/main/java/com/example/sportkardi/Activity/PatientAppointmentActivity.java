package com.example.sportkardi.Activity;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Adapter.MyAppointmentListAdapter;
import com.example.sportkardi.Model.MyAppointment;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class PatientAppointmentActivity extends AppCompatActivity {
    private static final String TAG = PatientAppointmentActivity.class.getName();
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

    private ConstraintLayout backConstraintLayout, addConstraintLayout, nextAppointmentConstraintLayout, deleteAppointmentConstraintLayout;
    private ConstraintLayout messageConstraintLayout;
    private LinearLayout homeLinearLayout, profileLinearLayout, infoLinearLayout, settingsLinearLayout;
    private FloatingActionButton addBloodPressureFAB;
    private TextView noNextAppointmentTextView, noPreviousAppointmentTextView, dayTextView, timeTextView;
    private RecyclerView appointmentListRecyclerView;

    private String personalId, name, message;

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
        setContentView(R.layout.activity_patient_appointment);
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

        addConstraintLayout = findViewById(R.id.addConstraintLayout);
        backConstraintLayout = findViewById(R.id.backConstraintLayout);

        nextAppointmentConstraintLayout = findViewById(R.id.nextAppointmentConstraintLayout);
        dayTextView = findViewById(R.id.dayTextView);
        timeTextView = findViewById(R.id.timeTextView);
        noNextAppointmentTextView = findViewById(R.id.noNextAppointmentTextView);
        noPreviousAppointmentTextView = findViewById(R.id.noPreviousAppointmentTextView);
        messageConstraintLayout = findViewById(R.id.messageConstraintLayout);
        deleteAppointmentConstraintLayout = findViewById(R.id.deleteAppointmentConstraintLayout);
        appointmentListRecyclerView = findViewById(R.id.appointmentListRecyclerView);

        homeLinearLayout = findViewById(R.id.homeLinearLayout);
        profileLinearLayout = findViewById(R.id.profileLinearLayout);
        infoLinearLayout = findViewById(R.id.infoLinearLayout);
        settingsLinearLayout = findViewById(R.id.settingsLinearLayout);
        addBloodPressureFAB = findViewById(R.id.addBloodPressureFAB);

        nextAppointmentConstraintLayout.setVisibility(View.GONE);
        noNextAppointmentTextView.setVisibility(View.VISIBLE);
        addConstraintLayout.setVisibility(View.VISIBLE);
        noPreviousAppointmentTextView.setVisibility(View.VISIBLE);
        appointmentListRecyclerView.setVisibility(View.GONE);

//        incomingAppointment();
        loadAppointmentsInRecyclerView();
        appointmentListRecyclerView.setHasFixedSize(true);
        messageConstraintLayout.setVisibility(View.GONE);

        addConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientAppointmentActivity.this, PatientNewAppointmentActivity.class);
                intent.putExtra("KEY", KEY);
                intent.putExtra("personalId", personalId);
                intent.putExtra("name", name);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        backConstraintLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        deleteAppointmentConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        messageConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message != null){
                    showMessageDialog();
                }
            }
        });


        //főoldal
        homeLinearLayout.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        //profil activity megnyitása
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientAppointmentActivity.this, PatientProfileActivity.class);
            intent.putExtra("KEY", KEY);
            intent.putExtra("personalId", personalId);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

        // új mérés activity megnyitása
        addBloodPressureFAB.setOnClickListener(v -> {
            Intent intent = new Intent(PatientAppointmentActivity.this, PatientBloodPressureActivity.class);
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

            TapTargetSequence sequence = new TapTargetSequence(PatientAppointmentActivity.this)
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

                            TapTarget.forView(addConstraintLayout, "Hozzáadás", "Ha nincs időpontja, az itt megjelenő + gombra kattintva foglalhat egyet.")
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
                        public void onSequenceFinish() {

                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            // opcionálisan lépésenként lehet logolni
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            Toast.makeText(PatientAppointmentActivity.this, "Útmutató megszakítva.", Toast.LENGTH_SHORT).show();
                        }
                    });

            sequence.start();
        });

        //beállítások activity megnyitása
        settingsLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PatientAppointmentActivity.this, PatientSettingsActivity.class);
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

    // Törlés dialog
    private void showDeleteDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_delete, null);

        ConstraintLayout constraintLayout = view.findViewById(R.id.constraintLayout);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        TextView textView24 = view.findViewById(R.id.textView24);

        constraintLayout.setVisibility(View.GONE);
        textView24.setText("Biztos, hogy törli az időpontot?");

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        deleteButton.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deleteAppointment();
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

    // Időpont törlése
    private void deleteAppointment() {
        String dayValue = dayTextView.getText().toString();
        String timeValue = timeTextView.getText().toString().split("-")[0].trim();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MMMM dd. HH:mm", Locale.getDefault());
        Date appointmentDateTime = null;
        try {
            appointmentDateTime = dateFormat.parse(dayValue + " " + timeValue);
        } catch (ParseException e) {
            Toast.makeText(this, "Hiba az időpont értelmezésekor.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (appointmentDateTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(appointmentDateTime);
            calendar.add(Calendar.HOUR, -24); // 24 órával korábbi időpont

            if (new Date().after(calendar.getTime())) {
                showDeletedAppointmentDialog();
                return;
            }
        } else {
            Toast.makeText(this, "Érvénytelen időpont.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("appointment")
                .whereEqualTo("personalId", personalId)
                .whereEqualTo("day", dayValue)
                .whereEqualTo("time", timeTextView.getText().toString()) // Fontos a pontos időpont egyezés
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
                            showSuccessfulDeleteDialog();
                        } else {
                            Toast.makeText(this, "Nincs ilyen időpont.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Hiba: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

        refreshUser();
    }

    // Nem törölhető időpont dialog
    private void showDeletedAppointmentDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientAppointmentActivity.this).inflate(R.layout.dialog_deleted_appointment, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView descTextView = view.findViewById(R.id.descTextView);
        TextView descTextView2 = view.findViewById(R.id.descTextView2);

        descTextView2.setVisibility(View.GONE);
        descTextView.setText("Az időpontot 24 órán belül már nem lehet lemondani.");

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientAppointmentActivity.this);
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

        alertDialog.show();
    }

    private void refreshUser() {
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

    // Sikeres törlés dialog
    private void showSuccessfulDeleteDialog() {
        ConstraintLayout dialogConstraintLayout = findViewById(R.id.dialogConstraintLayout);
        View view = LayoutInflater.from(PatientAppointmentActivity.this).inflate(R.layout.dialog_successful_delete, dialogConstraintLayout);
        Button doneButton = view.findViewById(R.id.doneButton);

        GifImageView gifImageView = view.findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(PatientAppointmentActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMessage();
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setOnDismissListener(dialog -> changeMessage());

        alertDialog.show();
    }

    // Üzenet dialog
    private void showMessageDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_message, null);
        Button doneButton = view.findViewById(R.id.doneButton);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        messageTextView.setText(message);
        titleTextView.setText("Megjegyzésem");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        doneButton.findViewById(R.id.doneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    // RecyclerView frissítése - korábbi időpontok
    private void loadAppointmentsInRecyclerView() {
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

                        ArrayList<MyAppointment> myAppointmentList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentDay = document.getString("day");
                            String appointmentTime = document.getString("time");
                            String dbMessage = document.getString("message");

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

                                    if (dbMessage != null && !dbMessage.equals("Nincs")){
                                        messageConstraintLayout.setVisibility(View.VISIBLE);
                                        message = dbMessage;
                                    }

                                    // Ellenőrizzük, hogy a mai napon későbbi időpont vagy jövőbeli dátum van-e
                                    if (parsedDay.after(todayDateOnly) || (parsedDay.equals(todayDateOnly) && parsedStartTime != null && parsedStartTime.after(currentTime))) {
                                        nextAppointmentConstraintLayout.setVisibility(View.VISIBLE);
                                        noNextAppointmentTextView.setVisibility(View.GONE);
                                        addConstraintLayout.setVisibility(View.GONE);
                                        timeTextView.setText(appointmentTime);
                                        dayTextView.setText(appointmentDay);
                                    } else {
                                        MyAppointment appointment = new MyAppointment(
                                                personalId,
                                                document.getString("day"),
                                                document.getString("time"),
                                                document.getString("message")
                                        );
                                        myAppointmentList.add(appointment);

                                        appointmentListRecyclerView.setVisibility(View.VISIBLE);
                                        noPreviousAppointmentTextView.setVisibility(View.GONE);

                                        refreshUser();
                                    }
                                } else {
                                    MyAppointment appointment = new MyAppointment(
                                            personalId,
                                            document.getString("day"),
                                            document.getString("time"),
                                            document.getString("message")
                                    );
                                    myAppointmentList.add(appointment);

                                    appointmentListRecyclerView.setVisibility(View.VISIBLE);
                                    noPreviousAppointmentTextView.setVisibility(View.GONE);
                                }

                            } catch (ParseException e) {
                                Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        // Fordított időrendi sorrend
                        Collections.sort(myAppointmentList, (a1, a2) -> {
                            try {
                                Date date1 = dateFormat.parse(a1.getDay());
                                Date date2 = dateFormat.parse(a2.getDay());
                                int dateComparison = date2.compareTo(date1); // Megfordítva!
                                if (dateComparison != 0) {
                                    return dateComparison; // Dátum szerinti fordított rendezés
                                } else {
                                    // Ha a dátumok egyeznek, az idő szerint is fordítva rendezünk
                                    String[] timeRange1 = a1.getTime().split("-");
                                    String[] timeRange2 = a2.getTime().split("-");
                                    if (timeRange1.length > 0 && timeRange2.length > 0) {
                                        Date time1 = timeFormat.parse(timeRange1[0].trim());
                                        Date time2 = timeFormat.parse(timeRange2[0].trim());
                                        return time2.compareTo(time1); // Kezdő időpont szerinti fordított rendezés
                                    }
                                    return 0;
                                }
                            } catch (ParseException e) {
                                Log.e("AppointmentSorting", "Hiba az időpontok összehasonlításakor: " + e.getMessage());
                                return 0;
                            }
                        });

                        // A RecyclerView és az adapter beállítása a lista frissítése után
                        appointmentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        MyAppointmentListAdapter adapter = new MyAppointmentListAdapter(myAppointmentList);
                        appointmentListRecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(this, "Hiba történt az adatok lekérése során!", Toast.LENGTH_SHORT).show();
                    }
                });

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