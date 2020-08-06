package com.example.autowallpaperchanger;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class WallpaperData {
    private static final String TAG = "AlertReceiver";

//    public static final String URI = "URI";
//    public static final String IS_SHUFFLE = "SHUFFLE_STATUS";

    private ImageData imageData;
    private Uri uri;
    private int position;
    private int listSize;
    boolean shuffle;

    public void loadData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ImageData.IMAGE_DATA, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(ImageData.URI_LIST, null);
        position = sharedPreferences.getInt(ImageData.CURRENT_URI, 0);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        shuffle = preferences.getBoolean("shuffle", false);
        Log.d(TAG, "loadData: shuffle :: " + shuffle);


        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> uris = gson.fromJson(jsonString, type);
        imageData = ImageData.getInstance();
        imageData.setQueueIndex(position);
        if (uris != null) {
            if (shuffle) {
                ArrayList<String> uriArrayList = new ArrayList<>(uris);
                int index = position - 1 < 0 ? uris.size() - 1 : position - 1;
                uriArrayList.remove(index);
                Collections.shuffle(uriArrayList);
                uri = Uri.parse(uriArrayList.get(0));
            } else {
                listSize = uris.size();
                this.uri = Uri.parse(uris.get(position));
            }
        }
    }

    public void saveData(Context context) {
        if (!shuffle) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(ImageData.IMAGE_DATA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(ImageData.CURRENT_URI, (imageData.getQueueIndex() + 1) % listSize);
            editor.apply();
        }
    }

    public void changeWallpaper(final Context context) {
        Log.d(TAG, "changeWallpaper: setting wallpaper");
        final ContentResolver contentResolver = context.getContentResolver();
        new Thread(new Runnable() {
            Bitmap bitmap = null;

            @Override
            public void run() {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    } else {
                        wallpaperManager.setBitmap(bitmap);
                    }
                    Log.d(TAG, "run: wallpaper Changed.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ImageData getImageData() {
        return imageData;
    }

    public Uri getUri() {
        return uri;
    }
}
