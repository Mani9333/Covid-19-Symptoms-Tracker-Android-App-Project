package com.example.mcassignment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class HeartService {

    private Activity activity;
    private String cameraId;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession previewSession;
    private CaptureRequest.Builder captureRequestBuilder;

    private int totalRedPixels;
    private int frameCount;
    private List<Long> measurementsTime;
    private List<Measurement> measurements;

    public HeartService() { }

    public HeartService(Activity activity) {
        this.activity = activity;
        reset();
    }

    public void startCamera(Surface previewSurface) {

        if(cameraDevice != null) cameraDevice.close();

        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.e("camera", "No permission to take photos");
                return;
            }

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camDevice) {
                    cameraDevice = camDevice;

                    try {
                        cameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                previewSession = session;
                                try {
                                    captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                    captureRequestBuilder.addTarget(previewSurface); // this is previewSurface
                                    captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

                                    HandlerThread thread = new HandlerThread("CameraPreview");
                                    thread.start();

                                    previewSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);

                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                Log.e("camera", "Session configuration failed");
                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camDevice) {
                    if(cameraDevice != null) cameraDevice.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camDevice, int i) {
                    if(cameraDevice != null) cameraDevice.close();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void stopCamera() {
        try {
            cameraDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        frameCount = 0;
        measurements = new ArrayList<Measurement>();
    }

    public void measureHeartRate(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int noOfPixels = width * height;
        int[] pixels = new int[noOfPixels];
        bitmap.getPixels(pixels, 0, width, width / 2, height / 2, width / 20, height / 20);

        int redPixels = 0;
        for (int i = 0; i < noOfPixels; i++) {
            redPixels += (pixels[i] >> 16) & 0xFF;
        }

        if (frameCount == 20) {
            totalRedPixels = redPixels;
        }
        else if (frameCount > 20 && frameCount < 49) {
            totalRedPixels = (totalRedPixels *(frameCount-20) + redPixels) / (frameCount-19);
        }
        else {
            totalRedPixels = (totalRedPixels *29 + redPixels) / 30;
        }

        measurements.add(new Measurement(totalRedPixels, System.currentTimeMillis()));
        frameCount++;
    }

    public void calcHeartRate() {

        measurementsTime = new ArrayList<>();

        for (int i=1; i<measurements.size()-1; i++) {
            Measurement cur = measurements.get(i);
            Measurement prev = measurements.get(i-1);
            Measurement next = measurements.get(i+1);
            if (cur.getRedPixels() > prev.getRedPixels() && cur.getRedPixels() > next.getRedPixels()) {
                measurementsTime.add(cur.getMilliSeconds());
            }
        }

        Log.d("TAG", "measureHeartRate: " + measurementsTime.size());
        Log.d("TAG", "Frame Count: " + frameCount);

        List<Long> diff = new ArrayList<>();
        for (int i = 1; i < measurementsTime.size(); i++) {
            diff.add(measurementsTime.get(i) - measurementsTime.get(i-1));
        }
        Collections.sort(diff);
        Long median = diff.get(diff.size()/2);
        int heartRate = (int) (60000/median);

        Log.d("heartRate", "heartRate: " + heartRate);

        Intent intent = new Intent("heartRateIntent");
        intent.putExtra("heartRate", heartRate);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

        stopCamera();
        reset();
    }

}

