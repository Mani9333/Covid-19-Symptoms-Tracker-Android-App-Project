package com.example.mcassignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_RespRate extends AppCompatActivity {

    private Button btnResprate;
    private Button btnSave;
    private Button btnBack;
    private TextView txtResprate;
    private SymptomDBHelper db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resp);

        btnResprate = (Button) findViewById(R.id.respratebtn);
        btnSave = (Button) findViewById(R.id.upresp);
        btnBack = (Button) findViewById(R.id.back_resp);
        txtResprate = (TextView) findViewById(R.id.respratetext);
        db = SymptomDBHelper.getInst(getApplicationContext());

        btnSave.setOnClickListener(view -> {
            boolean isInserted = db.insertData(false);
            if (isInserted) {
                Toast.makeText(Activity_RespRate.this, "Respiratory rate uploaded.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Activity_RespRate.this, "Unable to upload Respiratory rate.", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_RespRate.this, MainMenuActivity.class);
            startActivity(intent);
        });

        btnResprate.setOnClickListener(view -> {

            Intent intent = new Intent(Activity_RespRate.this, RespService.class);
            startService(intent);
            txtResprate.setText("Measuring...");
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int respRate = intent.getIntExtra("respRate", 0);
                User.getInstance().setRespiratoryRate(respRate);
                txtResprate.setText("Respiratory rate measured: " + User.getInstance().getRespiratoryRate());
                Toast.makeText(Activity_RespRate.this, "Respiratory rate measured is " + User.getInstance().getRespiratoryRate(), Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter("respRateIntent"));
    }
}