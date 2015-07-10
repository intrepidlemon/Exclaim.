package com.iantoxi.prg02;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        startService(new Intent(getApplicationContext(), AccelerometerChangeDetectionService.class));

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ImageView background = (ImageView) stub.findViewById(R.id.background);
                AnimationDrawable progressAnimation = (AnimationDrawable) background.getDrawable();
                progressAnimation.start();
            }
        });

    }

    public void excitedClick(View view) {
        Intent startIntent = new Intent(this, SendExcitementToHandheldIntentService.class);
        startService(startIntent);
    }
}
