package com.iantoxi.prg02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "c6k3wpsws3D3AzXSiCKLidO8Y";
    private static final String TWITTER_SECRET = "v4mdHAMunvjP1x0CWyri5wgGWJ9SdlbCSUp5nB4Lb7rObrXT5P";
    private TwitterLoginButton loginButton;
    private TwitterSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), ExcitementListenerService.class));

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                loginButton.setVisibility(View.GONE);

                // Do something with result, which provides a TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });

        session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            loginButton.setVisibility(View.GONE);
            TextView signedIn = (TextView) findViewById(R.id.signed_in);
            signedIn.setText("You are signed in as " + session.getUserName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
