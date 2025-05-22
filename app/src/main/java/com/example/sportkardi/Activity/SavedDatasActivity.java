package com.example.sportkardi.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportkardi.Adapter.PatientListAdapter;
import com.example.sportkardi.Model.Athlete;
import com.example.sportkardi.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SavedDatasActivity extends AppCompatActivity {
    private static final String CLASS = SavedDatasActivity.class.getName();
    private static final String PREFERENCES_FILE = "com.example.sportkardi.PREFERENCES";
    private static final String ATHLETE_IDS_KEY = "athleteIDs";

    private RecyclerView mRecyclerView;
    private PatientListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_saved_datas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Athlete> athletes = new ArrayList<>();
        mAdapter = new PatientListAdapter(this, athletes, athlete -> {
            Intent intent = new Intent(SavedDatasActivity.this, CardiShowAthleteDatasActivity.class);
            intent.putExtra("personalId", athlete.getPersonalId());
            startActivity(intent);
            finish();
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setClickable(true);

        getAthletes(athletes);
    }

    private void getAthletes(ArrayList<Athlete> athletes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = db.collection("Sportolók");
        athletes.clear();
    }
}
