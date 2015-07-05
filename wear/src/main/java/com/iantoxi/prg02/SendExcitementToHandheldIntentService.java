package com.iantoxi.prg02;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendExcitementToHandheldIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String TRIGGER_EXCITEMENT_CAMERA = "excitement_camera";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.iantoxi.prg02.extra.PARAM1";

    private GoogleApiClient mGoogleApiClient;
    private String excitementNodeId = null;

    private int NOTIFICATION_ID = 10101;

    public SendExcitementToHandheldIntentService() {
        super("SendExcitementToHandheldIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            setupExcitementCamera();
            showOpenOnPhoneAnimation();
            initiateExcitementCamera();
            mGoogleApiClient.disconnect();
        }
    }

    private void initiateExcitementCamera() {
        if (excitementNodeId != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, excitementNodeId,
                    TRIGGER_EXCITEMENT_CAMERA, null).await();
        }
    }

    private void showOpenOnPhoneAnimation() {
        if (excitementNodeId != null) {
            Intent openOnPhoneIntent = new Intent(this, ConfirmationActivity.class);
            openOnPhoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openOnPhoneIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
            startActivity(openOnPhoneIntent);
            // Clear the notification
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private void setupExcitementCamera() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        CapabilityApi.GetCapabilityResult result =
                Wearable.CapabilityApi.getCapability(
                        mGoogleApiClient, TRIGGER_EXCITEMENT_CAMERA,
                        CapabilityApi.FILTER_REACHABLE).await();

        updateCapability(result.getCapability());
    }

    private void updateCapability(CapabilityInfo capabilityInfo) {
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        excitementNodeId = pickBestNodeId(connectedNodes);
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

}
