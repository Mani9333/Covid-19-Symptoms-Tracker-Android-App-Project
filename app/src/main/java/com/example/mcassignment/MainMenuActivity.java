package com.example.mcassignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import net.sqlcipher.database.SQLiteDatabase;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainMenuActivity extends AppCompatActivity {

    private Button btnSymptoms;
    private Button btnResprate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SQLiteDatabase.loadLibs(this);

        btnSymptoms = (Button) findViewById(R.id.symptoms);
        btnResprate = (Button) findViewById(R.id.respratebtn);

        btnSymptoms.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenuActivity.this, Activity_Symptoms.class);
            startActivity(intent);
        });

        findViewById(R.id.hratebtn).setOnClickListener(view -> {
            Intent intent = new Intent(MainMenuActivity.this, Activity_HeartRate.class);
            startActivity(intent);
        });

        btnResprate.setOnClickListener(view -> {
            Intent intent = new Intent(MainMenuActivity.this, Activity_RespRate.class);
            startActivity(intent);
        });

        findViewById(R.id.loc).setOnClickListener(view -> {
            Intent intent = new Intent(MainMenuActivity.this, LocService.class);
            startService(intent);
        });
    }
}