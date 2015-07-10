package com.iantoxi.prg02;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.widget.ImageView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.fabric.sdk.android.Fabric;

public class TweetNotificationService extends Service {
    private String SEARCH_QUERY;
    private final int SEARCH_COUNT = 20;
    private final String SEARCH_RESULT_TYPE = "mixed";
    private final String TWEET_ID = "tweetID";

    public TweetNotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        SEARCH_QUERY = getString(R.string.automatic_tweet);

        getTweet();
        return START_NOT_STICKY;
    }

    private void getTweet() {

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        SearchService service = twitterApiClient.getSearchService();


        service.tweets(SEARCH_QUERY, null, null, null, SEARCH_RESULT_TYPE, SEARCH_COUNT, null, null,
                null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {
                        TwitterSession session = Twitter.getSessionManager().getActiveSession();

                        List<Tweet> tweets = searchResult.data.tweets;

                        for (Tweet tweet : tweets) {
                            if (tweet.user.id != session.getUserId()) {
                                String tweetImageUrl = tweet.entities.media.get(0).mediaUrl;
                                String tweetText = tweet.text;
                                long tweetId = tweet.id;

                                Bitmap tweetImage = getBitmapFromURL(tweetImageUrl);

                                notifyWithTweet(tweetImage, tweetText, tweetId);
                                break;
                            }
                        }
                    }

                    @Override
                    public void failure(TwitterException error) {
                    }

                    private Bitmap getBitmapFromURL(String u) {
                        final String url = u;

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Future<Bitmap> result = executor.submit(new Callable<Bitmap>() {

                            @Override
                            public Bitmap call() {
                                try {
                                    return BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        });
                        try {
                            return result.get();
                        } catch (Exception e) {
                            return null;
                        }
                    }

                }
        );
        this.stopSelf();
    }

    private void notifyWithTweet(Bitmap image, String tweet, long tweetID) {
        int notificationId = 001;

        // Build intent for notification content
        Intent viewIntent = new Intent(this, TweetViewerActivity.class);
        viewIntent.putExtra(TWEET_ID, tweetID);
        viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(image);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setLargeIcon(image)
                        .setContentTitle(getString(R.string.tweet_title))
                        .setContentText(tweet)
                        .setContentIntent(viewPendingIntent)
                        .extend(wearableExtender)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(image));

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
