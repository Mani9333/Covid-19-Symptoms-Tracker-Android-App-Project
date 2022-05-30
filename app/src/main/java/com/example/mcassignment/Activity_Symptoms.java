package com.example.mcassignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Activity_Symptoms extends AppCompatActivity {

    Spinner ddSpinner;
    RatingBar barRating;
    Button btnSave;
    Button btnBack;
    SymptomDBHelper db;
    Symptom symptom;
    List<Symptom> symptomsList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        ddSpinner = (Spinner) findViewById(R.id.dropdown);
        barRating = (RatingBar) findViewById(R.id.ratingBar);
        btnSave = (Button) findViewById(R.id.uploadButton);
        btnBack = (Button) findViewById(R.id.back_symp);
        user = User.getInstance();
        db = SymptomDBHelper.getInst(getApplicationContext());

        user.setSymptoms(user.getSymptomsList());
        symptomsList = user.getSymptoms();

        ArrayAdapter<Symptom> myAdapter = new ArrayAdapter<Symptom>(this,
                android.R.layout.simple_spinner_item,
                symptomsList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ddSpinner.setAdapter(myAdapter);

        ddSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                symptom = (Symptom) adapterView.getSelectedItem();
                getRating();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        barRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setRating(v);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadSymptoms();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(Activity_Symptoms.this, MainMenuActivity.class);
            startActivity(intent);
        });
    }

    public void uploadSymptoms() {
        user.setSymptoms(symptomsList);
        boolean isInserted = db.insertData(false);
        if (isInserted) {
            Toast.makeText(Activity_Symptoms.this, "Ratings uploaded.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Activity_Symptoms.this, "Unable to upload ratings.", Toast.LENGTH_LONG).show();
        }
    }

    public void getRating() {
        barRating.setRating(symptom.getRating());
    }

    public void setRating(float rating) {
        symptom.setRating(rating);
    }

}