package com.example.sportkardi.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.sportkardi.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

public class PatientDataFragment extends Fragment {
    private TextView personalIdTV, genderTV, birthYearTV, phoneNumberTV, heightTV, weightTV, periodTV;
    private TextView sportTV, weeklyWorkoutTV, sportAgeTV, sportTypeTV, complaintsTV, habitsTV, medicinesTV;
    private LinearLayout dataSheetLinearLayout;
    private String personalId;
    private boolean isEditable;
    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_data, container, false);

        if (getArguments() != null) {
            personalId = getArguments().getString("personalId");
            isEditable = getArguments().getBoolean("isEditable");
        }

        dataSheetLinearLayout = view.findViewById(R.id.dataSheetLinearLayout);

        db = FirebaseFirestore.getInstance();
        loadAthleteData(personalId);

        personalIdTV = view.findViewById(R.id.personalIdTV);
        genderTV = view.findViewById(R.id.genderTV);
        birthYearTV = view.findViewById(R.id.birthYearTV);
        phoneNumberTV = view.findViewById(R.id.phoneNumberTV);
        heightTV = view.findViewById(R.id.heightTV);
        weightTV = view.findViewById(R.id.weightTV);
        periodTV = view.findViewById(R.id.periodTV);
        sportTV = view.findViewById(R.id.sportTV);
        weeklyWorkoutTV = view.findViewById(R.id.weeklyWorkoutTV);
        sportAgeTV = view.findViewById(R.id.sportAgeTV);
        sportTypeTV = view.findViewById(R.id.sportTypeTV);
        complaintsTV = view.findViewById(R.id.complaintsTV);
        habitsTV = view.findViewById(R.id.habitsTV);
        medicinesTV = view.findViewById(R.id.medicineTV);

        return view;
    }

    private void loadAthleteData(String personalId) {
        db.collection("Sportolók")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                populateAthleteData(document);

                                // Felhasználói adatok lekérdezése a "users" kollekcióból
                                db.collection("users")
                                        .whereEqualTo("personalId", personalId)
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful()) {
                                                if (!userTask.getResult().isEmpty()) {
                                                    for (QueryDocumentSnapshot userDoc : userTask.getResult()) {
                                                        populateUserData(userDoc);
                                                    }
                                                } else {
                                                    Log.e("error", "Nincs user adat a megadott azonosítóval.");
                                                }
                                            } else {
                                                Log.e("error", "Hiba történt a user adatok lekérésekor.");
                                            }
                                        });
                            }
                        } else {
                            Log.e("error", "Nincs adat a megadott azonosítóval.");
                        }
                    } else {
                        Log.e("error", "Hiba történt az adatok lekérésekor.");
                    }
                });
    }

    // sportoló adatok beállítása
    private void populateAthleteData(QueryDocumentSnapshot document) {
        personalIdTV.setText(document.getString("personalId"));
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

    // felhasználói (user) adatok beállítása
    private void populateUserData(QueryDocumentSnapshot userDoc) {
        genderTV.setText(userDoc.getString("gender"));
        Long birthYear = userDoc.getLong("birthYear");
        if (birthYear != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int age = currentYear - birthYear.intValue();
            birthYearTV.setText(birthYear + " (" + age + " éves)");
        } else {
            birthYearTV.setText("N/A");
        }
        phoneNumberTV.setText(userDoc.getString("phone"));
    }
}