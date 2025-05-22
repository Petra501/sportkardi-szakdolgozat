package com.example.sportkardi.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sportkardi.Model.BloodPressure;
import com.example.sportkardi.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StatisticsBloodPressureFragment extends Fragment {
    private String personalId;
    private boolean isEditable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_statistics_blood_pressure, container, false);

        if (getArguments() != null) {
            personalId = getArguments().getString("personalId");
            isEditable = getArguments().getBoolean("isEditable");
        }

        getWeeklyBloodPressureStats();
        return view;
    }

    private void getWeeklyBloodPressureStats() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("bloodPressure");

        itemsRef.orderBy("time", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.e("error", "Nincsenek adatok a Firebase-ben.");
                        return;
                    }

                    List<BloodPressure> bloodPressureList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BloodPressure bloodPressure = document.toObject(BloodPressure.class);
                        if (bloodPressure.getPersonalId().equals(personalId) && bloodPressure.getDiary() == 1) {
                            bloodPressureList.add(bloodPressure);
                        }
                    }

                    Log.d("firelista", "+1");
                    processBloodPressureData(bloodPressureList);
                })
                .addOnFailureListener(e -> Log.e("error", "Hiba az adatok lekérésekor", e));
    }

    private void processBloodPressureData(List<BloodPressure> data) {
        if (data.isEmpty()) return;

        // Első időbélyeg lekérése
        Timestamp firstTimestamp = data.get(0).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstTimestamp.toDate());

        List<List<BloodPressure>> weeklyData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<BloodPressure> weekList = new ArrayList<>();
            long startTime = calendar.getTimeInMillis();
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            long endTime = calendar.getTimeInMillis();

            for (BloodPressure bp : data) {
                long bpTime = bp.getTime().toDate().getTime();
                if (bpTime >= startTime && bpTime < endTime) {
                    weekList.add(bp);
                }
            }

            if (!weekList.isEmpty()) {
                weeklyData.add(weekList);
            }
        }

        updateUIWithStats(weeklyData);
    }

    private void updateUIWithStats(List<List<BloodPressure>> weeklyData) {
        for (int i = 0; i < 4; i++) {
            LinearLayout tableLayout = getTableLayout(i);
            if (i < weeklyData.size()) {
                tableLayout.setVisibility(View.VISIBLE);
                List<BloodPressure> weekData = weeklyData.get(i);
                fillTableWithStats(tableLayout, weekData, i); // Az indexet is átadjuk
            } else {
                tableLayout.setVisibility(View.GONE);
            }
        }
    }

    private void fillTableWithStats(LinearLayout tableLayout, List<BloodPressure> weekData, int index) {
        TextView[][] tableCells = getTableCells(tableLayout, index);

        List<Integer> systoles = new ArrayList<>();
        List<Integer> diastoles = new ArrayList<>();
        List<Integer> pulses = new ArrayList<>();

        for (BloodPressure bp : weekData) {
            systoles.add(bp.getSystole());
            diastoles.add(bp.getDiastole());
            pulses.add(bp.getPulse());
        }

        tableCells[0][0].setText(String.valueOf(getAverage(systoles)));
        tableCells[0][1].setText(String.valueOf(getAverage(diastoles)));
        tableCells[0][2].setText(String.valueOf(getAverage(pulses)));

        tableCells[1][0].setText(String.valueOf(Collections.max(systoles)));
        tableCells[1][1].setText(String.valueOf(Collections.max(diastoles)));
        tableCells[1][2].setText(String.valueOf(Collections.max(pulses)));

        tableCells[2][0].setText(String.valueOf(Collections.min(systoles)));
        tableCells[2][1].setText(String.valueOf(Collections.min(diastoles)));
        tableCells[2][2].setText(String.valueOf(Collections.min(pulses)));

        tableCells[3][0].setText(String.valueOf(getStandardDeviation(systoles)));
        tableCells[3][1].setText(String.valueOf(getStandardDeviation(diastoles)));
        tableCells[3][2].setText(String.valueOf(getStandardDeviation(pulses)));

        tableCells[4][0].setText(String.valueOf(calculateDiurnalIndex(systoles)));
        tableCells[4][1].setText(String.valueOf(calculateDiurnalIndex(diastoles)));
        tableCells[4][2].setText(String.valueOf(calculateDiurnalIndex(pulses)));
    }

    private int getAverage(List<Integer> values) {
        if (values.isEmpty()) return 0;
        double sum = 0;
        for (int v : values) sum += v;
        return (int) Math.round(sum / values.size());
    }

    private int getStandardDeviation(List<Integer> values) {
        if (values.isEmpty()) return 0;
        double mean = getAverage(values);
        double sum = 0;
        for (int v : values) sum += Math.pow(v - mean, 2);
        return (int) Math.round(Math.sqrt(sum / values.size()));
    }

    private int calculateDiurnalIndex(List<Integer> values) {
        if (values.isEmpty()) return 0;
        double nightAvg = getAverage(values.subList(0, values.size() / 2));
        double dayAvg = getAverage(values.subList(values.size() / 2, values.size()));
        return (int) Math.round(((dayAvg - nightAvg) / dayAvg) * 100);
    }

    private LinearLayout getTableLayout(int index) {
        switch (index) {
            case 0:
                return getView().findViewById(R.id.table1);
            case 1:
                return getView().findViewById(R.id.table2);
            case 2:
                return getView().findViewById(R.id.table3);
            case 3:
                return getView().findViewById(R.id.table4);
            default:
                return null;
        }
    }

    private TextView[][] getTableCells(LinearLayout tableLayout, int index) {
        return new TextView[][]{
                {tableLayout.findViewById(getResources().getIdentifier("sysAver" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("diasAver" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("pulsAver" + (index + 1), "id", getContext().getPackageName()))},
                {tableLayout.findViewById(getResources().getIdentifier("sysMax" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("diasMax" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("pulsMax" + (index + 1), "id", getContext().getPackageName()))},
                {tableLayout.findViewById(getResources().getIdentifier("sysMin" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("diasMin" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("pulsMin" + (index + 1), "id", getContext().getPackageName()))},
                {tableLayout.findViewById(getResources().getIdentifier("sysDisp" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("diasDisp" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("pulsDisp" + (index + 1), "id", getContext().getPackageName()))},
                {tableLayout.findViewById(getResources().getIdentifier("sysDiur" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("diasDiur" + (index + 1), "id", getContext().getPackageName())),
                        tableLayout.findViewById(getResources().getIdentifier("pulsDiur" + (index + 1), "id", getContext().getPackageName()))}
        };
    }
}