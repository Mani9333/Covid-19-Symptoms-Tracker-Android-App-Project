package com.example.mcassignment;

import static java.lang.Math.abs;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class RespService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    public ArrayList<Double> x;
    public ArrayList<Double> y;
    public ArrayList<Double> z;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        x = new ArrayList<Double>();
        y = new ArrayList<Double>();
        z = new ArrayList<Double>();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x.add((double) sensorEvent.values[0]);
            y.add((double) sensorEvent.values[1]);
            z.add((double) sensorEvent.values[2]);

            if(x.size() >= 230) {
                stopSelf();
                calcRespiratoryRate(x);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
    }

    public void calcRespiratoryRate(List<Double> values) {

        List<Double> filteredValues = filter(values, 5);

        double d, sign = 0, crossings = 0;

        for(int i=1; i<filteredValues.size(); i++) {

            d = filteredValues.get(i) - filteredValues.get(i-1);

            if (d != 0) {
                if (sign == 0) sign = d/abs(d);

                double newSign = d/abs(d);
                if(newSign != sign) {
                    sign *= -1;
                    crossings++;
                }
            }
        }

        int respiratoryRate = (int) (crossings * 60 / 90);
        Log.d("TAG", "Resp Rate: " + respiratoryRate);

        Intent intent = new Intent("respRateIntent");
        intent.putExtra("respRate", respiratoryRate);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public List<Double> filter(List<Double> values, int window) {

        List<Double> filteredValues = new ArrayList<>();
        double avg = 0.0;

        for(int i=0; i<values.size(); i++) {
            avg += values.get(i);
            if(i+1 < window) continue;
            filteredValues.add(avg/window);
            avg -= values.get(i+1 - window);
        }

        return filteredValues;

    }
}
