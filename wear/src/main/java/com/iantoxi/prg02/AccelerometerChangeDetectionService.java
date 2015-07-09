package com.iantoxi.prg02;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class AccelerometerChangeDetectionService extends Service implements SensorEventListener{
    private SensorManager excitementSensorManager;
    private Sensor excitementSensor;
    private long lastNotificationTime;

    public AccelerometerChangeDetectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        excitementSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        excitementSensor = excitementSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        excitementSensorManager.registerListener(this, excitementSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        lastNotificationTime = 0;
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        x = Math.abs(event.values[0]);
        y = Math.abs(event.values[1]);
        z = Math.abs(event.values[2]);

        if (x + y + z > 50 && System.currentTimeMillis() - lastNotificationTime > 30000) {
            lastNotificationTime = System.currentTimeMillis();
            notifyExcitement();
        }
    }

    private void notifyExcitement() {
        int notificationId = 001;

        //Intent for notification view
        Intent viewIntent = new Intent(this, MainActivity.class);
//        viewIntent.putExtra("metadataForIntent", 1);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), R.drawable.notificationbg))
                        .setContentText(getString(R.string.excited_text))
                        .setContentIntent(viewPendingIntent);

        //Intent to open handheld camera
        Intent cameraIntent = new Intent(getApplicationContext(),
                SendExcitementToHandheldIntentService.class);
        PendingIntent cameraPendingIntent = PendingIntent.getService(getApplicationContext(),
                0, cameraIntent, 0);

        NotificationCompat.Action cameraAction = new NotificationCompat.Action.Builder(
                R.drawable.camera,
                getString(R.string.take_picture),
                cameraPendingIntent).extend(new NotificationCompat.Action.WearableExtender()
                    .setAvailableOffline(false)).build();

        notificationBuilder.addAction(cameraAction);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
