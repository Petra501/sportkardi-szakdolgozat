package com.example.sportkardi.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataActivity extends AppCompatActivity {
    private static final String TAG = DataActivity.class.getName();
    private static final int KEY = 44;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private TextView nameTV, heightTV, weightTV, periodTV, sportTV, weeklyWorkoutTV, sportAgeTV, sportTypeTV, complaintsTV, otherComplaintsTV, habitsTV, medicinesTV;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        int key = getIntent().getIntExtra("KEY", 0);
        if (key != KEY) {
            finish();
        }

        nameTV = findViewById(R.id.nameTV);
        heightTV = findViewById(R.id.heightTV);
        weightTV = findViewById(R.id.weightTV);
        periodTV = findViewById(R.id.periodTV);
        sportTV = findViewById(R.id.sportTV);
        weeklyWorkoutTV = findViewById(R.id.weeklyWorkoutTV);
        sportAgeTV = findViewById(R.id.sportAgeTV);
        sportTypeTV = findViewById(R.id.sportTypeTV);
        complaintsTV = findViewById(R.id.complaintsTV);
        otherComplaintsTV = findViewById(R.id.moreComplaintTV);
        habitsTV = findViewById(R.id.habitsTV);
        medicinesTV = findViewById(R.id.medicineTV);

        final String personalId = getIntent().getStringExtra("personalId");
        final String name = getIntent().getStringExtra("name");
        final String height = getIntent().getStringExtra("height");
        final String weight = getIntent().getStringExtra("weight");
        final String period = getIntent().getStringExtra("period");
        final String sport = getIntent().getStringExtra("sport");
        final String weeklyWorkout = getIntent().getStringExtra("weeklyWorkout");
        final String sportAge = getIntent().getStringExtra("sportAge");
        final String sportType = getIntent().getStringExtra("sportType");
        final String complaints = getIntent().getStringExtra("complaints");
        final String otherComplaints = getIntent().getStringExtra("otherComplaints");
        final String habits = getIntent().getStringExtra("habits");
        final String medicines = getIntent().getStringExtra("medicines");

        nameTV.setText(name);
        heightTV.setText(height);
        weightTV.setText(weight);
        periodTV.setText(period);
        sportTV.setText(sport);
        weeklyWorkoutTV.setText(weeklyWorkout);
        sportAgeTV.setText(sportAge);
        sportTypeTV.setText(sportType);
        complaintsTV.setText(complaints);
        otherComplaintsTV.setText(otherComplaints);
        habitsTV.setText(habits);
        medicinesTV.setText(medicines);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Sportolók");
        Timestamp currentTimestamp = Timestamp.now();

        save = findViewById(R.id.saveButton);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Athlete athlete = new Athlete(
                        personalId,
                        name,
                        height,
                        weight,
                        period,
                        sport,
                        weeklyWorkout,
                        sportAge,
                        sportType,
                        complaints,
                        habits,
                        medicines,
                        currentTimestamp);
                mItems.add(athlete).addOnSuccessListener(documentReference -> {
                }).addOnFailureListener(e -> {
                    Toast.makeText(DataActivity.this, "Hiba a mentés során", Toast.LENGTH_LONG).show();
                });
                Toast.makeText(DataActivity.this, "Sikeres mentés", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DataActivity.this, PatientMainMenuActivity.class);
                intent.putExtra("KEY", KEY);
                startActivity(intent);
                finish();
            }
        });
    }
}
