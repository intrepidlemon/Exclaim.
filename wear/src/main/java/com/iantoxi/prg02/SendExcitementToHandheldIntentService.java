package com.iantoxi.prg02;

import android.app.IntentService;
import android.content.Intent;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;


import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendExcitementToHandheldIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String TRIGGER_EXCITEMENT_CAMERA = "excitement_camera";

    private GoogleApiClient mGoogleApiClient;
    private List<Node> nodes = null;

    public SendExcitementToHandheldIntentService() {
        super("SendExcitementToHandheldIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setupExcitementCamera();
        showOpenOnPhoneAnimation();
        initiateExcitementCamera();
        mGoogleApiClient.disconnect();
    }

    private void initiateExcitementCamera() {
        if (nodes != null) {
            for (Node node: nodes) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
                        TRIGGER_EXCITEMENT_CAMERA, null).await();
            }
        }
    }

    private void showOpenOnPhoneAnimation() {
        if (nodes != null) {
            Intent openOnPhoneIntent = new Intent(this, ConfirmationActivity.class);
            openOnPhoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openOnPhoneIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
            startActivity(openOnPhoneIntent);
        }
    }

    private void setupExcitementCamera() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        ConnectionResult connectionResult =
                mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            Log.e("Send Excitement:", "Failed to connect to GoogleApiClient.");
            return;
        }

        nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await().getNodes();
    }

}
