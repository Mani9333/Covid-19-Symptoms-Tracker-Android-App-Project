package com.example.mcassignment;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocService extends Service implements LocationListener {

    private LocationManager locationManager;
    static final int NOTIFICATION_ID = 1234;
    public static boolean isServiceRunning = false;
    SymptomDBHelper db;
    User user;

    public LocService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = SymptomDBHelper.getInst(getApplicationContext());
//        user = User.getInstance();
        runInBackGround();
        getLocation();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(User.getInstance() == null) {
            stopMyService();
            return;
        }
        Log.e("User: ", User.getInstance().getUsername());
        Log.e("Location: ", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        User.getInstance().setLocation(new LocData(location.getLatitude(), location.getLongitude()));
        db.insertData(true);
        new UploadTask().execute();
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900000, 0, this);
        }
    }

    public void runInBackGround() {

        if (isServiceRunning) return;
        isServiceRunning = true;

        Log.e("TAG", "runInBackGround: Started");

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("GPS_CHANNEL_ID", "GPS_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("GPS Background Channel");
        mNotificationManager.createNotificationChannel(channel);

        Intent notificationIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "GPS_CHANNEL_ID")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    public void stopMyService() {
        stopForeground(true);
        isServiceRunning = false;
        stopSelf();
    }

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                String url = "http://192.168.0.34/upload.php";
                String charset = "UTF-8";
                String subId = "1234";
                String accept = "1";
                String date = "22-03";
                String boundary = Long.toHexString(System.currentTimeMillis());
                String CRLF = "\r\n";

                URLConnection connection;
                connection = new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                File file = new File("/data/data/com.example.mcassignment/databases", User.getInstance().getLastname() + ".db");

                Log.e("TAG", "Inside Background Service: " + file.getPath());

                FileInputStream vf = new FileInputStream(file);

                try (
                        OutputStream output = connection.getOutputStream();
                        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
                ) {
                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"accept\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(accept).append(CRLF).flush();

                    // Send normal accept.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"id\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(subId).append(CRLF).flush();

                    // Send normal date.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"date\"").append(CRLF);
                    writer.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
                    writer.append(CRLF).append(date).append(CRLF).flush();

                    // Send video file.
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + file.getName() + "\"").append(CRLF);
                    writer.append("Content-Type: "+ URLConnection.guessContentTypeFromName(file.getName()) +"; charset=" + charset).append(CRLF); // Text file itself must be saved in this charset!
                    writer.append(CRLF).flush();

                    try {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = vf.read(buffer, 0, buffer.length)) >= 0) {
                            output.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        publishProgress(String.valueOf(exception));
                    }

                    output.flush();
                    vf.close();
                    writer.append(CRLF).flush();

                    writer.append("--" + boundary + "--").append(CRLF).flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                String responseMessage = ((HttpURLConnection) connection).getResponseMessage();
                Log.e("TAG", "Network Request: " + responseMessage);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}