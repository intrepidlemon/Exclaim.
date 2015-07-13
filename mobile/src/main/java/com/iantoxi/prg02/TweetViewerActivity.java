package com.iantoxi.prg02;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import io.fabric.sdk.android.Fabric;


public class TweetViewerActivity extends Activity {
    private final String TWEET_ID = "tweetID";
    private String TWITTER_KEY;
    private String TWITTER_SECRET;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        TWITTER_KEY = getString(R.string.twitter_key);
        TWITTER_SECRET = getString(R.string.twitter_secret);
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new TweetUi());
        setContentView(R.layout.activity_tweet_viewer);
        updateTweet();
    }

    private void updateTweet() {
        Intent intent = getIntent();
        final LinearLayout myLayout
                = (LinearLayout) findViewById(R.id.tweet_viewer);

        final long tweetId = intent.getExtras().getLong(TWEET_ID);
        TweetUtils.loadTweet(tweetId, new LoadCallback<Tweet>() {
            @Override
            public void success(Tweet tweet) {
                myLayout.addView(new TweetView(
                        TweetViewerActivity.this, tweet));
            }

            @Override
            public void failure(TwitterException exception) {
                // Toast.makeText(...).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        updateTweet();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_viewer, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
