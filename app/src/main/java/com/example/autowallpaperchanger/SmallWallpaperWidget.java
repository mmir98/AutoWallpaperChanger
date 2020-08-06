package com.example.autowallpaperchanger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class SmallWallpaperWidget extends AppWidgetProvider {
    private String strNext = "Next";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ComponentName thisWidget = new ComponentName(context, SmallWallpaperWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.small_wallpaper_widget);

        remoteViews.setOnClickPendingIntent(R.id.small_widget_next_button, getPendingSelfIntent(context, strNext));

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (strNext.equals(intent.getAction())) {
            nextWallpaper(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void nextWallpaper(Context context){
        Toast.makeText(context, "Wallpaper will change soon", Toast.LENGTH_SHORT).show();
        WallpaperData wallpaperData = new WallpaperData();
        wallpaperData.loadData(context);
        if (wallpaperData.getImageData() != null && wallpaperData.getUri() != null) {
            wallpaperData.changeWallpaper(context);
            wallpaperData.saveData(context);
        }
    }
}

