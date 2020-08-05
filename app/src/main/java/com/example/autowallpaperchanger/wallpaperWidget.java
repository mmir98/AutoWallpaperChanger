package com.example.autowallpaperchanger;

import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class wallpaperWidget extends AppWidgetProvider {
    private static final String TAG = "WidgetReceiver";

    private String strPower = "Power";
    private String strNext = "Next";
    private String strShuffle = "Shuffle";
    private String strTime = "Time";
    private String strGallery = "Gallery";

    private Drawable times[] = new Drawable[6];

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, wallpaperWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wallpaper_widget);

        remoteViews.setOnClickPendingIntent(R.id.power_button, getPendingSelfIntent(context, strPower));
        remoteViews.setOnClickPendingIntent(R.id.next_button, getPendingSelfIntent(context, strNext));
        remoteViews.setOnClickPendingIntent(R.id.time_button, getPendingSelfIntent(context, strTime));
        remoteViews.setOnClickPendingIntent(R.id.shuffle_button, getPendingSelfIntent(context, strShuffle));
        remoteViews.setOnClickPendingIntent(R.id.gallery_button, getPendingSelfIntent(context, strGallery));

        for (int widgetId : allWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.wallpaper_widget);

        if (strPower.equals(intent.getAction())) {
            // your onClick action is here
            Toast.makeText(context, "power", Toast.LENGTH_SHORT).show();
            Log.w("Widget", "Clicked button1");
        } else if (strNext.equals(intent.getAction())) {
            Toast.makeText(context, "next", Toast.LENGTH_SHORT).show();
            Log.w("Widget", "Clicked button2");
            WallpaperData wallpaperData = new WallpaperData();
            wallpaperData.loadData(context);
            Log.d(TAG, "onReceive: " + wallpaperData.getImageData().getUriList().size());
            if (wallpaperData.getImageData() != null && wallpaperData.getUri() != null) {
                wallpaperData.changeWallpaper(context);
                wallpaperData.saveData(context);
            }
        } else if (strTime.equals(intent.getAction())) {
            Toast.makeText(context, "time", Toast.LENGTH_SHORT).show();
            Log.w("Widget", "Clicked button3");
            remoteViews.setImageViewResource(R.id.time_button, R.drawable.ic_widget_10m);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids) {
                appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
            }
        } else if (strShuffle.equals(intent.getAction())) {
            Toast.makeText(context, "shuffle", Toast.LENGTH_SHORT).show();
            Log.w("Widget", "Clicked button4");
        } else if (strGallery.equals(intent.getAction())) {
            Toast.makeText(context, "gallery", Toast.LENGTH_SHORT).show();
            Log.w("Widget", "Clicked button5");
            Intent appIntent = new Intent(context, MainActivity.class);
            context.startActivity(appIntent);
        }
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance()
    };

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

