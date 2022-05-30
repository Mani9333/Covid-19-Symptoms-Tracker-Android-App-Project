package com.example.mcassignment;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.time.Instant;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SymptomDBHelper extends SQLiteOpenHelper {

    private final String pwd;
    private static SymptomDBHelper inst;

    public SymptomDBHelper(@Nullable Context context) {
        super(context, User.getInstance().getLastname() + ".db", null, 1);
        pwd = User.getInstance().getPassword();
    }

    public static synchronized SymptomDBHelper getInst(Context context) {
        if (inst == null) {
            Log.e("TAG", "getInst: " + User.getInstance());
            inst = new SymptomDBHelper(context);
        }
        return inst;
    }

    public Boolean insertData(boolean onlyLocation) {
        SQLiteDatabase db = inst.getWritableDatabase(pwd);
        ContentValues contentValues = new ContentValues();

        contentValues.put("latitude", User.getInstance().getLocation().getLatitude());
        contentValues.put("longitude", User.getInstance().getLocation().getLongitude());
        contentValues.put("timestamp", String.valueOf(Instant.now()));
        if (!onlyLocation) {
            contentValues.put("heart_rate", User.getInstance().getHeartRate());
            contentValues.put("respiratory_rate", User.getInstance().getRespiratoryRate());
            for(Symptom s: User.getInstance().getSymptoms()) {
                contentValues.put(s.getValue(), s.getRating());
            }
        }

        long result = db.insert("userdetails", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists userdetails(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "heart_rate FLOAT DEFAULT 0, " +
                "respiratory_rate FLOAT DEFAULT 0, " +
                "nausea FLOAT DEFAULT 0, " +

                "headache FLOAT DEFAULT 0, " +
                "diarrhea FLOAT DEFAULT 0, " +
                "soar_throat FLOAT DEFAULT 0, " +
                "fever FLOAT DEFAULT 0, " +
                "muscle_ache FLOAT DEFAULT 0, " +
                "loss_smell_taste FLOAT DEFAULT 0, " +
                "cough FLOAT DEFAULT 0, " +
                "shortness_breath FLOAT DEFAULT 0, " +
                "tired FLOAT DEFAULT 0, " +
                "latitude FLOAT DEFAULT 0, " +
                "longitude FLOAT DEFAULT 0, " +
                "timestamp VARCHAR DEFAULT 0" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists userdetails");
    }
}
