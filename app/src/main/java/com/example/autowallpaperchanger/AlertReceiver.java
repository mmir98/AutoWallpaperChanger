package com.example.autowallpaperchanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        WallpaperData wallpaperData = new WallpaperData();
        wallpaperData.loadData(context);
        Log.d(TAG, "onReceive: " + wallpaperData.getImageData().getUriList().size());
        if (wallpaperData.getImageData() != null && wallpaperData.getUri() != null) {
            wallpaperData.changeWallpaper(context);
            wallpaperData.saveData(context);
        }
    }
}

