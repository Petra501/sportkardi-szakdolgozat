package com.example.sportkardi.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.sportkardi.Activity.CardiShowAthleteDatasActivity;
import com.example.sportkardi.Activity.PatientBloodPressureActivity;
import com.example.sportkardi.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TodayBloodPressureFragment extends Fragment {
    private ConstraintLayout cardConstraintLayout1, cardConstraintLayout2, cardConstraintLayout3, addConstraintLayout1, addConstraintLayout2, addConstraintLayout3;
    private TextView bloodPressureTextView1, bloodPressureTextView2, bloodPressureTextView3, pulseTextView1, pulseTextView2, pulseTextView3, timeTextView1, timeTextView2, timeTextView3;
    private LineChart lineChart;

    private String personalId;
    private boolean isEditable;
    private List<String> xValues;
    private TypedValue colorPrimary = new TypedValue();
    private TypedValue textColor = new TypedValue();
    private TypedValue backgroundColor = new TypedValue();
    private List<Entry> systole = new ArrayList<>();
    private List<Entry> diastole = new ArrayList<>();
    private List<Entry> pulse = new ArrayList<>();

    private PatientBloodPressureActivity bloodPressureActivity;
    private CardiShowAthleteDatasActivity cardiShowAthleteDatasActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PatientBloodPressureActivity) {
            bloodPressureActivity = (PatientBloodPressureActivity) context;
        } else if (context instanceof CardiShowAthleteDatasActivity) {
            cardiShowAthleteDatasActivity = (CardiShowAthleteDatasActivity) context;
        }
        else {
            throw new ClassCastException(context.toString()
                    + " must implement PatientBloodPressureActivity or CardiShowAthleteDatasActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today_blood_pressure, container, false);

        if (getArguments() != null) {
            personalId = getArguments().getString("personalId");
            isEditable = getArguments().getBoolean("isEditable");
        }

        cardConstraintLayout1 = view.findViewById(R.id.cardConstraintLayout1);
        cardConstraintLayout2 = view.findViewById(R.id.cardConstraintLayout2);
        cardConstraintLayout3 = view.findViewById(R.id.cardConstraintLayout3);

        addConstraintLayout1 = view.findViewById(R.id.addConstraintLayout1);
        addConstraintLayout2 = view.findViewById(R.id.addConstraintLayout2);
        addConstraintLayout3 = view.findViewById(R.id.addConstraintLayout3);

        bloodPressureTextView1 = view.findViewById(R.id.bloodPressureTextView1);
        bloodPressureTextView2 = view.findViewById(R.id.bloodPressureTextView2);
        bloodPressureTextView3 = view.findViewById(R.id.bloodPressureTextView3);

        pulseTextView1 = view.findViewById(R.id.pulseTextView1);
        pulseTextView2 = view.findViewById(R.id.pulseTextView2);
        pulseTextView3 = view.findViewById(R.id.pulseTextView3);

        timeTextView1 = view.findViewById(R.id.timeTextView1);
        timeTextView2 = view.findViewById(R.id.timeTextView2);
        timeTextView3 = view.findViewById(R.id.timeTextView3);

        lineChart = view.findViewById(R.id.lineChart);

        timeTextView1.setVisibility(View.GONE);
        timeTextView2.setVisibility(View.GONE);
        timeTextView3.setVisibility(View.GONE);

        if (!isEditable) {
            addConstraintLayout1.setVisibility(View.GONE);
            addConstraintLayout2.setVisibility(View.GONE);
            addConstraintLayout3.setVisibility(View.GONE);
        }

        getContext().getTheme().resolveAttribute(android.R.attr.colorPrimary, colorPrimary, true);
        getContext().getTheme().resolveAttribute(android.R.attr.textColor, textColor, true);
        getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, backgroundColor, true);

        getBloodPressureDatas();

        addConstraintLayout1.setOnClickListener(v -> showAddBloodPressureDialog(1));
        addConstraintLayout2.setOnClickListener(v -> showAddBloodPressureDialog(2));
        addConstraintLayout3.setOnClickListener(v -> showAddBloodPressureDialog(3));

        return view;
    }

    @SuppressLint("ResourceType")
    @NonNull
    private LineData getLineData(List<Entry> systole, List<Entry> diastole, List<Entry> pulse, List<Entry> verticalLine0, List<Entry> verticalLine1, List<Entry> verticalLine2) {
        LineDataSet systoleDataSet = new LineDataSet(systole, "Systole (Hgmm)");
        systoleDataSet.setColor(Color.RED);
        systoleDataSet.setLineWidth(2f);
        systoleDataSet.setFillColor(colorPrimary.data);
        systoleDataSet.setFillAlpha(100);
        systoleDataSet.setDrawFilled(true);
        systoleDataSet.setCircleColor(textColor.data);
        systoleDataSet.setCircleSize(3f);
        systoleDataSet.setDrawCircles(true);
        systoleDataSet.setDrawCircleHole(false);
        systoleDataSet.setCircleHoleColor(Color.WHITE);
        systoleDataSet.setValueTextColor(Color.BLACK);
        systoleDataSet.setDrawValues(false);

        LineDataSet diastoleDataSet = new LineDataSet(diastole, "Diastole (Hgmm)");
        diastoleDataSet.setLineWidth(2f);
        diastoleDataSet.setColor(Color.BLUE);
        diastoleDataSet.setFillColor(backgroundColor.data);
        diastoleDataSet.setFillAlpha(255);
        diastoleDataSet.setDrawFilled(true);
        diastoleDataSet.setCircleColor(textColor.data);
        diastoleDataSet.setCircleSize(3f);
        diastoleDataSet.setDrawCircles(true);
        diastoleDataSet.setDrawCircleHole(false);
        diastoleDataSet.setCircleHoleColor(Color.WHITE);
        diastoleDataSet.setValueTextColor(Color.BLACK);
        diastoleDataSet.setDrawValues(false);

        LineDataSet pulseDataSet = new LineDataSet(pulse, "Pulzus (1/perc)");
        pulseDataSet.setLineWidth(2f);
        pulseDataSet.setColor(Color.GREEN);
        pulseDataSet.setCircleColor(textColor.data);
        pulseDataSet.setCircleSize(3f);
        pulseDataSet.setDrawCircles(true);
        pulseDataSet.setDrawCircleHole(false);
        pulseDataSet.setCircleHoleColor(Color.WHITE);
        pulseDataSet.setValueTextColor(Color.BLACK);
        pulseDataSet.setDrawValues(false);

        LineDataSet verticalLineDataSet0 = new LineDataSet(verticalLine0, "");
        verticalLineDataSet0.setColor(textColor.data);
        verticalLineDataSet0.setLineWidth(2f);
        verticalLineDataSet0.setDrawCircles(false);
        verticalLineDataSet0.setDrawValues(false);
        verticalLineDataSet0.setDrawIcons(false);
        verticalLineDataSet0.setForm(Legend.LegendForm.NONE);
        verticalLineDataSet0.setMode(LineDataSet.Mode.LINEAR);

        LineDataSet verticalLineDataSet1 = new LineDataSet(verticalLine1, "");
        verticalLineDataSet1.setColor(textColor.data);
        verticalLineDataSet1.setLineWidth(2f);
        verticalLineDataSet1.setDrawCircles(false);
        verticalLineDataSet1.setDrawValues(false);
        verticalLineDataSet1.setDrawIcons(false);
        verticalLineDataSet1.setForm(Legend.LegendForm.NONE);
        verticalLineDataSet1.setMode(LineDataSet.Mode.LINEAR);

        LineDataSet verticalLineDataSet2 = new LineDataSet(verticalLine2, "");
        verticalLineDataSet2.setColor(textColor.data);
        verticalLineDataSet2.setLineWidth(2f);
        verticalLineDataSet2.setDrawCircles(false);
        verticalLineDataSet2.setDrawValues(false);
        verticalLineDataSet2.setDrawIcons(false);
        verticalLineDataSet2.setForm(Legend.LegendForm.NONE);
        verticalLineDataSet2.setMode(LineDataSet.Mode.LINEAR);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(textColor.data);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP); // Alulra helyezi
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Középre igazítja
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // Vízszintes elrendezés
        legend.setDrawInside(false);
        legend.setYOffset(10f);

        // Adatok beállítása a diagramhoz
        LineData lineData = new LineData(systoleDataSet, diastoleDataSet, pulseDataSet, verticalLineDataSet0, verticalLineDataSet1, verticalLineDataSet2);
        return lineData;
    }

    // Vérnyomásmérés hozzáadása dialog
    private void showAddBloodPressureDialog(int number) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_add_blood_pressure, null);

        AutoCompleteTextView systoleAutoCompleteTextView = view.findViewById(R.id.systoleAutoCompleteTextView);
        AutoCompleteTextView diastoleAutoCompleteTextView = view.findViewById(R.id.diastoleAutoCompleteTextView);
        AutoCompleteTextView pulseAutoCompleteTextView = view.findViewById(R.id.pulseAutoCompleteTextView);

        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button saveButton = view.findViewById(R.id.saveButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        saveButton.setOnClickListener(v -> {
            String systoleStr = systoleAutoCompleteTextView.getText().toString();
            String diastoleStr = diastoleAutoCompleteTextView.getText().toString();
            String pulseStr = pulseAutoCompleteTextView.getText().toString();

            if (!systoleStr.isEmpty() && !diastoleStr.isEmpty() && !pulseStr.isEmpty()) {
                saveBloodPressureData(1, number, Integer.parseInt(systoleStr), Integer.parseInt(diastoleStr), Integer.parseInt(pulseStr));
                alertDialog.dismiss();
            } else {
                Toast.makeText(requireContext(), "Kérlek, tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    // Vérnyomási adat mentése Firebase-be
    private void saveBloodPressureData(int diary, int number, int systole, int diastole, int pulse) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> bloodPressureData = new HashMap<>();
        bloodPressureData.put("personalId", personalId);
        bloodPressureData.put("diary", diary);
        bloodPressureData.put("number", number);
        bloodPressureData.put("systole", systole);
        bloodPressureData.put("diastole", diastole);
        bloodPressureData.put("pulse", pulse);
        bloodPressureData.put("time", Timestamp.now());

        db.collection("bloodPressure")
                .add(bloodPressureData)
                .addOnSuccessListener(documentReference -> {
                    // Értesítjük az activity-t az adatváltozásról
                    if (bloodPressureActivity != null) {
                        bloodPressureActivity.notifyDataChanged();
                    }
                    // Újratöltjük az adatokat a fragmentben is, hogy azonnal frissüljön a nézet
                    getBloodPressureDatas();
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    Toast.makeText(requireContext(), "Hiba történt az adatok mentése során!", Toast.LENGTH_SHORT).show();
                });
    }

    // Vérnyomásmérési adatok listázása
    private void getBloodPressureDatas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Float> datas = new HashMap<>();
        systole.clear();
        diastole.clear();
        pulse.clear();

        // A mai dátum
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);  // Kezdőóra: 00:00
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStartDate = calendar.getTime();

        // Lekérjük a bloodPressure adatokat a personalId és a mai dátum alapján
        db.collection("bloodPressure")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // A 'time' mező lekérése
                            Timestamp timestamp = document.getTimestamp("time");

                            if (timestamp != null) {
                                // Ellenőrzés, hogy a dátum ma van-e
                                Calendar documentDate = Calendar.getInstance();
                                documentDate.setTime(timestamp.toDate());

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String formattedTime = sdf.format(timestamp.toDate());
                                String[] parts = formattedTime.split(":");
                                String hour = parts[0];
                                String minute = parts[1];
                                float fullTime = Float.parseFloat(hour) + (Float.parseFloat(minute) / 60f);
                                float convertedFullTime = convertHourToIndex(fullTime);

                                // Ha a dátum a mai napra esik
                                if (isSameDay(documentDate, calendar)) {
                                    int number = document.getLong("number").intValue();
                                    int systoleData = document.getLong("systole").intValue();
                                    int diastoleData = document.getLong("diastole").intValue();
                                    int pulseData = document.getLong("pulse").intValue();

                                    // Ha number == 1, állítsuk be a bloodPressureTextView1 értékét
                                    if (number == 1) {
                                        bloodPressureTextView1.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                        pulseTextView1.setText(String.valueOf(pulseData));
                                        timeTextView1.setText(formattedTime);
                                        timeTextView1.setVisibility(View.VISIBLE);
                                        addConstraintLayout1.setVisibility(View.GONE);

                                        datas.put("X1", convertedFullTime);
                                        datas.put("systoleY1", (float) systoleData);
                                        datas.put("diastoleY1", (float) diastoleData);
                                        datas.put("pulseY1", (float) pulseData);
                                    }
                                    // Ha number == 2, állítsuk be a bloodPressureTextView2 értékét
                                    else if (number == 2) {
                                        bloodPressureTextView2.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                        pulseTextView2.setText(String.valueOf(pulseData));
                                        timeTextView2.setText(formattedTime);
                                        timeTextView2.setVisibility(View.VISIBLE);
                                        addConstraintLayout2.setVisibility(View.GONE);
                                        datas.put("X2", convertedFullTime);
                                        datas.put("systoleY2", (float) systoleData);
                                        datas.put("diastoleY2", (float) diastoleData);
                                        datas.put("pulseY2", (float) pulseData);
                                    }
                                    // Ha number == 3, állítsuk be a bloodPressureTextView3 értékét
                                    else if (number == 3) {
                                        bloodPressureTextView3.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                        pulseTextView3.setText(String.valueOf(pulseData));
                                        timeTextView3.setText(formattedTime);
                                        timeTextView3.setVisibility(View.VISIBLE);
                                        addConstraintLayout3.setVisibility(View.GONE);

                                        datas.put("X3", convertedFullTime);
                                        datas.put("systoleY3", (float) systoleData);
                                        datas.put("diastoleY3", (float) diastoleData);
                                        datas.put("pulseY3", (float) pulseData);
                                    }
                                }
                            }
                        }

                        if (datas.containsKey("X1")) {
                            systole.add(new Entry(datas.get("X1"), datas.get("systoleY1")));
                            diastole.add(new Entry(datas.get("X1"), datas.get("diastoleY1")));
                            pulse.add(new Entry(datas.get("X1"), datas.get("pulseY1")));
                        }
                        if (datas.containsKey("X2")) {
                            systole.add(new Entry(datas.get("X2"), datas.get("systoleY2")));
                            diastole.add(new Entry(datas.get("X2"), datas.get("diastoleY2")));
                            pulse.add(new Entry(datas.get("X2"), datas.get("pulseY2")));
                        }
                        if (datas.containsKey("X3")) {
                            systole.add(new Entry(datas.get("X3"), datas.get("systoleY3")));
                            diastole.add(new Entry(datas.get("X3"), datas.get("diastoleY3")));
                            pulse.add(new Entry(datas.get("X3"), datas.get("pulseY3")));
                        }

                        updateLineChart();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateLineChart() {
        // Diagram beállítása
        Description description = new Description();
        description.setText("");
        description.setPosition(150f, 15f);
        lineChart.setDescription(description);
        lineChart.getAxisRight().setDrawLabels(false);

        // X tengely beállítása
        xValues = Arrays.asList("06:00", "10:00", "14:00", "18:00", "22:00");

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xValues));
        xAxis.setLabelCount(xValues.size(), true);
        xAxis.setTextColor(textColor.data);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(4f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        // Y tengely beállítása
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(300f);
        yAxis.setTextColor(textColor.data);
        yAxis.setLabelCount(16, true);

        // Az összes Y tengely rácsvonal és tengelyvonal kikapcsolása
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        lineChart.getAxisRight().setEnabled(false);

        // 135-ös és 85-ös vastag vonalak létrehozása
        LimitLine ll135 = new LimitLine(135f, "");
        ll135.setLineWidth(1f);
        ll135.setLineColor(textColor.data);

        LimitLine ll85 = new LimitLine(85f, "");
        ll85.setLineWidth(1f);
        ll85.setLineColor(textColor.data);
        //        ll85.enableDashedLine(10f, 10f, 0f);

        yAxis.removeAllLimitLines();
        yAxis.addLimitLine(ll135);
        yAxis.addLimitLine(ll85);

        for (int i = 0; i <= 300; i += 20) {
            if (i == 135 || i == 85) continue; // Már létrehoztuk ezeket

            LimitLine gridLine = new LimitLine(i, "");
            gridLine.setLineWidth(0.5f);
            gridLine.setLineColor(Color.GRAY);

            yAxis.addLimitLine(gridLine);
        }

        for (int i = 0; i < 5; i++) {

            LimitLine gridLine = new LimitLine(i, "");
            gridLine.setLineWidth(0.5f);
            gridLine.setLineColor(Color.GRAY);

            xAxis.addLimitLine(gridLine);
        }

        //        getDiagramDatas(systole, diastole, pulse);

        List<Entry> verticalLine0 = new ArrayList<>();
        List<Entry> verticalLine1 = new ArrayList<>();
        List<Entry> verticalLine2 = new ArrayList<>();

        for (int i = 0; i < systole.size(); i++) {
            switch (i) {
                case 0:
                    verticalLine0.add(new Entry(systole.get(i).getX(), systole.get(i).getY()));
                    verticalLine0.add(new Entry(diastole.get(i).getX(), diastole.get(i).getY()));
                    break;
                case 1:
                    verticalLine1.add(new Entry(systole.get(i).getX(), systole.get(i).getY()));
                    verticalLine1.add(new Entry(diastole.get(i).getX(), diastole.get(i).getY()));
                    break;
                case 2:
                    verticalLine2.add(new Entry(systole.get(i).getX(), systole.get(i).getY()));
                    verticalLine2.add(new Entry(diastole.get(i).getX(), diastole.get(i).getY()));
                    break;
            }
        }

        LineData lineData = getLineData(systole, diastole, pulse, verticalLine0, verticalLine1, verticalLine2);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public float convertHourToIndex(float hour) {
        return (hour - 6) / 4;
    }

    // Ellenőrzés, hogy két dátum ugyanarra a napra esik-e
    private boolean isSameDay(Calendar documentDate, Calendar todayDate) {
        return documentDate.get(Calendar.YEAR) == todayDate.get(Calendar.YEAR) &&
                documentDate.get(Calendar.MONTH) == todayDate.get(Calendar.MONTH) &&
                documentDate.get(Calendar.DAY_OF_MONTH) == todayDate.get(Calendar.DAY_OF_MONTH);
    }
}