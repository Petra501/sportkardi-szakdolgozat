package com.example.sportkardi.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sportkardi.R;
import com.google.firebase.auth.FirebaseAuth;

public class SavedDatasLoginActivity extends AppCompatActivity {
    private static final String TAG = SavedDatasLoginActivity.class.getName();
    private static final int KEY = 44;

    EditText pinEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_saved_datas_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        }

        int key = getIntent().getIntExtra("KEY", 0);

        if(key != 44){
            finish();
        }


        mAuth = FirebaseAuth.getInstance();
        pinEditText = findViewById(R.id.pinEditText);


    }

    public void next(View view) {

        if (TextUtils.isEmpty(pinEditText.getText().toString())) {
            pinEditText.setError("A mező kitöltése kötelező!");
            pinEditText.requestFocus();
        } else {
            String pin = pinEditText.getText().toString();

            if(pin.equals("petra")){
                showDatas();
            } else {
                Toast.makeText(SavedDatasLoginActivity.this, "Hibás jelszó", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void showDatas(){
        Intent intent = new Intent(this, SavedDatasActivity.class);
        startActivity(intent);
    }
}
