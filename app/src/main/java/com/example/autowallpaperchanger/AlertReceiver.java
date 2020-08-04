package com.example.autowallpaperchanger;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

    public static final String URI = "URI";
    public static final String IS_SHUFFLE = "SHUFFLE_STATUS";

    ImageData imageData = ImageData.getInstance();
    Uri uri;
    boolean shuffle;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Intent Received");
        Log.d(TAG, "onReceive: intent ::" + intent.hasExtra(URI));
        Log.d(TAG, "onReceive: " + intent.hasExtra(IS_SHUFFLE));
        uri = Uri.parse(intent.getStringExtra(URI));
//        shuffle = intent.getBooleanExtra(IS_SHUFFLE, false);
//        if(imageData != null){
            changeWallpaper(context);
//            reSchedulerAlarm();
//        }
    }


    private void changeWallpaper(final Context context){
        Log.d(TAG, "changeWallpaper: setting wallpaper");
        final ContentResolver contentResolver = context.getContentResolver();
        new Thread(new Runnable() {
            Bitmap bitmap = null;
            @Override
            public void run() {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                    wallpaperManager.setBitmap(bitmap);
                    Log.d(TAG, "run: wallpaper Changed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private void reSchedulerAlarm(){
//
//    }
}
