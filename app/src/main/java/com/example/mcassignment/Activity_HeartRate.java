package com.example.mcassignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Activity_HeartRate extends AppCompatActivity {

    private User user;
    private TextureView cameraView;
    private Button btnHearrate;
    private Button btnSave;
    private Button btnBack;
    private TextView heartRateText;
    private SymptomDBHelper db;
    HeartService heartService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        user = User.getInstance();
        cameraView = (TextureView) findViewById(R.id.camview);
        btnHearrate = (Button) findViewById(R.id.hratebtn);
        btnSave = (Button) findViewById(R.id.upresp);
        btnBack = (Button) findViewById(R.id.back_heart);
        heartRateText = (TextView) findViewById(R.id.heartRateText);
        db = SymptomDBHelper.getInst(this);
        heartService = new HeartService(this);

        btnSave.setOnClickListener(view -> {
            boolean isInserted = db.insertData(false);
            if (isInserted) {
                Toast.makeText(Activity_HeartRate.this, "Heart rate uploaded.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Activity_HeartRate.this, "Unable to upload Heart rate.", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_HeartRate.this, MainMenuActivity.class);
            startActivity(intent);
        });

        btnHearrate.setOnClickListener(view -> {

            heartService.startCamera(new Surface(cameraView.getSurfaceTexture()));
            heartRateText.setText("Measuring...");
            cameraView.setVisibility(View.VISIBLE);

            cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                    heartService.measureHeartRate(cameraView.getBitmap());
                }
            });

            new Handler().postDelayed(() -> heartService.calcHeartRate(), 45000);
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int heartRate = intent.getIntExtra("heartRate", 0);
                user.setHeartRate(heartRate);
                heartRateText.setText("Your heart rate is " + user.getHeartRate());
                Toast.makeText(Activity_HeartRate.this, "Your heart rate is " + user.getHeartRate(), Toast.LENGTH_LONG).show();
                cameraView.setVisibility(View.INVISIBLE);
            }
        }, new IntentFilter("heartRateIntent"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        heartService.stopCamera();
    }
}