package com.iantoxi.prg02;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fabric.sdk.android.Fabric;


public class CameraActivity extends Activity {
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int TWEET_COMPOSER_REQUEST_CODE = 110;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        startCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Image captured and saved to fileUri specified in the Intent
                Intent intent = new TweetComposer.Builder(this)
                        .text(getString(R.string.automatic_tweet))
                        .image(fileUri)
                        .createIntent();

                startActivityForResult(intent, TWEET_COMPOSER_REQUEST_CODE);
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            } else {
                Toast.makeText(this, "Image capture failed :(", Toast.LENGTH_LONG).show();
                startCamera();
            }
        } else if(requestCode == TWEET_COMPOSER_REQUEST_CODE) {
            Intent intent = new Intent(this, TweetNotificationService.class);
            startService(intent);
            finish();
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //TODO save file to internal application files
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
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


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile());
    }

    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(getString(R.string.app_name), "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }
}
