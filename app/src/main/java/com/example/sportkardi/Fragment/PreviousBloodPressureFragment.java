package com.example.sportkardi.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sportkardi.Activity.PatientAppointmentActivity;
import com.example.sportkardi.Adapter.PrevoiusBloodPressureListAdapter;
import com.example.sportkardi.Model.BloodPressure;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PreviousBloodPressureFragment extends Fragment {
    private RecyclerView patientListRecyclerView;
    private SearchView searchView;

    private String personalId;
    private boolean isEditable;
    private PrevoiusBloodPressureListAdapter adapter;
    private ArrayList<BloodPressure> bloodPressures = new ArrayList<>();
    private List<String> xValues;
    private TypedValue colorPrimary = new TypedValue();
    private TypedValue textColor = new TypedValue();
    private TypedValue backgroundColor = new TypedValue();
    private List<Entry> systole = new ArrayList<>();
    private List<Entry> diastole = new ArrayList<>();
    private List<Entry> pulse = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_previous_blood_pressure, container, false);

        if (getArguments() != null) {
            personalId = getArguments().getString("personalId");
            isEditable = getArguments().getBoolean("isEditable");
        }

        patientListRecyclerView = view.findViewById(R.id.patientListRecyclerView);
        searchView = view.findViewById(R.id.searchView);

        searchView.clearFocus();
        patientListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        patientListRecyclerView.setHasFixedSize(true);

        adapter = new PrevoiusBloodPressureListAdapter(requireContext(), bloodPressures, bloodPressure -> {
            showBloodPressureDialog(bloodPressure.getPersonalId(), bloodPressure.getTime());
        });

        patientListRecyclerView.setAdapter(adapter);
        patientListRecyclerView.setClickable(true);

        getBloodPressureDatas(bloodPressures);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fileList(newText);
                return false;
            }
        });

        return view;
    }

    private void fileList(String newText) {
        ArrayList<BloodPressure> filteredList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMM dd", Locale.getDefault());

        for (BloodPressure bloodPressure : bloodPressures) {
            if (bloodPressure.getTime() != null) {
                Date date = bloodPressure.getTime().toDate();
                String formattedDate = sdf.format(date).toLowerCase();

                if (formattedDate.contains(newText.toLowerCase()) ||
                        bloodPressure.getPersonalId().toLowerCase().contains(newText.toLowerCase())) {
                    filteredList.add(bloodPressure);
                }
            }
        }

        if (!filteredList.isEmpty()) {
            adapter.setFilteredList(filteredList);
        }
    }

    private void getBloodPressureDatas(ArrayList<BloodPressure> bloodPressures) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("bloodPressure");

        itemsRef.orderBy("time", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bloodPressures.clear(); // Itt elég egyszer törölni
                    Set<String> uniqueDates = new HashSet<>(); // Egyedi dátumokat tároló Set

                    // Mai dátum lekérése (év-hónap-nap formátumban)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String todayDate = sdf.format(new Date());

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        BloodPressure bloodPressure = document.toObject(BloodPressure.class);

                        // Ellenőrizzük, hogy a personalId megegyezik-e az osztály globális personalId-jével
                        if (bloodPressure.getPersonalId().equals(personalId)) {
                            // Időbélyeg lekérése és formázása (csak dátum)
                            Timestamp timestamp = document.getTimestamp("time");
                            if (timestamp != null) {
                                Date date = timestamp.toDate();
                                String formattedDate = sdf.format(date);

                                // Csak akkor adjuk hozzá, ha még nincs benne ÉS nem a mai dátum
                                if (!uniqueDates.contains(formattedDate) && !formattedDate.equals(todayDate)) {
                                    uniqueDates.add(formattedDate);
                                    bloodPressures.add(bloodPressure);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(PatientAppointmentActivity.class.getName(), "Hiba az adatbázis lekérése során");
                });
    }

    // korábbi vérnyomási adat megjelenítése dialogban
    private void showBloodPressureDialog(String personalId, Timestamp date) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_previous_blood_pressure, null);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView bloodPressureTextView1 = view.findViewById(R.id.bloodPressureTextView1);
        TextView bloodPressureTextView2 = view.findViewById(R.id.bloodPressureTextView2);
        TextView bloodPressureTextView3 = view.findViewById(R.id.bloodPressureTextView3);
        TextView pulseTextView1 = view.findViewById(R.id.pulseTextView1);
        TextView pulseTextView2 = view.findViewById(R.id.pulseTextView2);
        TextView pulseTextView3 = view.findViewById(R.id.pulseTextView3);
        TextView timeTextView1 = view.findViewById(R.id.timeTextView1);
        TextView timeTextView2 = view.findViewById(R.id.timeTextView2);
        TextView timeTextView3 = view.findViewById(R.id.timeTextView3);
        Button okButton = view.findViewById(R.id.okButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Float> datas = new HashMap<>();

        // Lekérjük a bloodPressure adatokat a personalId és a mai dátum alapján
        db.collection("bloodPressure")
                .whereEqualTo("personalId", personalId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // A 'time' mező lekérése
                            Timestamp dbDate = document.getTimestamp("time");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("hu"));

                            Date formatDbDate = dbDate.toDate();
                            Date formatSelectedDate = date.toDate();

                            if (sdf.format(formatDbDate).equals(sdf.format(formatSelectedDate))) {
                                Date formatDate = dbDate.toDate();
                                dateTextView.setText(sdf.format(formatDate));

                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                String formattedTime = dateFormat.format(dbDate.toDate());
                                String[] parts = formattedTime.split(":");
                                String hour = parts[0];
                                String minute = parts[1];
                                float fullTime = Float.parseFloat(hour) + (Float.parseFloat(minute) / 60f);
                                float convertedFullTime = convertHourToIndex(fullTime);

                                int number = document.getLong("number").intValue();
                                int systoleData = document.getLong("systole").intValue();
                                int diastoleData = document.getLong("diastole").intValue();
                                int pulseData = document.getLong("pulse").intValue();

                                // Ha number == 1, állítsuk be a bloodPressureTextView1 értékét
                                if (number == 1) {
                                    bloodPressureTextView1.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                    pulseTextView1.setText(String.valueOf(pulseData));

                                    timeTextView1.setText(formattedTime);
                                }
                                // Ha number == 2, állítsuk be a bloodPressureTextView2 értékét
                                else if (number == 2) {
                                    bloodPressureTextView2.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                    pulseTextView2.setText(String.valueOf(pulseData));

                                    timeTextView2.setText(formattedTime);
                                }
                                // Ha number == 3, állítsuk be a bloodPressureTextView3 értékét
                                else if (number == 3) {
                                    bloodPressureTextView3.setText(String.valueOf(systoleData) + "/" + String.valueOf(diastoleData));
                                    pulseTextView3.setText(String.valueOf(pulseData));

                                    timeTextView3.setText(formattedTime);
                                }
                            }
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });

        okButton.setOnClickListener(new View.OnClickListener() {
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

    @SuppressLint("ResourceType")
    @NonNull
    private LineData getLineData(LineChart lineChart, List<Entry> systole, List<Entry> diastole, List<Entry> pulse, List<Entry> verticalLine0, List<Entry> verticalLine1, List<Entry> verticalLine2) {
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

    private void updateLineChart(LineChart lineChart) {
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
        // ll85.enableDashedLine(10f, 10f, 0f);

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

        LineData lineData = getLineData(lineChart, systole, diastole, pulse, verticalLine0, verticalLine1, verticalLine2);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public float convertHourToIndex(float hour) {
        return (hour - 6) / 4;
    }
}