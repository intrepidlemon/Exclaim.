package com.iantoxi.prg02;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


public class ExcitementListenerService extends WearableListenerService {
    private static final String TRIGGER_EXCITEMENT_CAMERA = "excitement_camera";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(TRIGGER_EXCITEMENT_CAMERA)) {
            Intent startIntent = new Intent(getApplicationContext(), CameraActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }


}
