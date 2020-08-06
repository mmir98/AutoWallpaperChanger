package com.example.autowallpaperchanger;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Implementation of App Widget functionality.
 */
public class BigWallpaperWidget extends AppWidgetProvider {
    private static final String TAG = "WidgetReceiver";

    private String strPower = "Power";
    private String strNext = "Next";
    private String strShuffle = "Shuffle";
    private String strTime = "Time";
    private String strGallery = "Gallery";

    private static boolean enableState = FALSE;
    private static boolean shuffleState = FALSE;
    private static String timeState = "5m";
    private String[] stringTimes = {"5m", "10m", "30m", "1h", "5h", "24h"};
    private int[] times = {R.drawable.ic_widget_5m,
            R.drawable.ic_widget_10m,
            R.drawable.ic_widget_30m,
            R.drawable.ic_widget_1h,
            R.drawable.ic_widget_5h,
            R.drawable.ic_widget_24h};
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        ComponentName thisWidget = new ComponentName(context, BigWallpaperWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.big_wallpaper_widget);

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

        if (strPower.equals(intent.getAction())) {
            enableAndDisable(context);
        } else if (strNext.equals(intent.getAction())) {
            nextWallpaper(context);
        } else if (strTime.equals(intent.getAction())) {
            changeTime(context);
        } else if (strShuffle.equals(intent.getAction())) {
            shuffleChange(context);
        } else if (strGallery.equals(intent.getAction())) {
            openApp(context);
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
        Log.d(TAG, "onReceive: " + wallpaperData.getImageData().getUriList().size());
        if (wallpaperData.getImageData() != null && wallpaperData.getUri() != null) {
            wallpaperData.changeWallpaper(context);
            wallpaperData.saveData(context);
        }
    }

    private void changeTime(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.big_wallpaper_widget);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for(int i = 0 ; i < 6 ; i++){
            if(stringTimes[i].equals(timeState)){
                timeState = stringTimes[(i + 1) % 6];
                remoteViews.setImageViewResource(R.id.time_button, times[(i + 1) % 6]);
                editor.putString("time_interval", context.getResources().getStringArray(R.array.time_interval_values)[i + 1]);
                editor.apply();
                break;
            }
        }

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID: ids) {
            appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
        }
    }

    private void enableAndDisable(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.big_wallpaper_widget);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(enableState == FALSE){
            editor.putBoolean("feature_status", TRUE);
            editor.apply();

            Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
            enableState = TRUE;
            remoteViews.setInt(R.id.power_button, "setColorFilter", ContextCompat.getColor(context, R.color.active_green));
        } else {
            editor.putBoolean("feature_status", FALSE);
            editor.apply();

            Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
            enableState = FALSE;
            remoteViews.setInt(R.id.power_button, "setColorFilter", ContextCompat.getColor(context, R.color.black));
        }

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID: ids) {
            appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
        }
    }

    private void shuffleChange(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.big_wallpaper_widget);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(shuffleState == FALSE){
            editor.putBoolean("shuffle", TRUE);
            editor.apply();
            Toast.makeText(context, "Shuffle enabled", Toast.LENGTH_SHORT).show();
            shuffleState = TRUE;
            remoteViews.setInt(R.id.shuffle_button, "setColorFilter", ContextCompat.getColor(context, R.color.active_green));
        } else {
            editor.putBoolean("shuffle", FALSE);
            editor.apply();
            Toast.makeText(context, "Shuffle disabled", Toast.LENGTH_SHORT).show();
            shuffleState = FALSE;
            remoteViews.setInt(R.id.shuffle_button, "setColorFilter", ContextCompat.getColor(context, R.color.black));
        }

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID: ids) {
            appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
        }
    }

    private void openApp(Context context){
        Intent appIntent = new Intent(context, MainActivity.class);
        context.startActivity(appIntent);
    }
}

