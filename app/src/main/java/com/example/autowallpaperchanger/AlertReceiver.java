package com.example.autowallpaperchanger;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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

