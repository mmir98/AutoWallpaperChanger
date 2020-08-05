package com.example.autowallpaperchanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.autowallpaperchanger.ImageActivity.IMAGE_POSITION;

public class MainActivity extends AppCompatActivity implements ImageRecyclerViewAdapter.OnImageClickListener {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_PICK_FROM_GALLERY = 1;

    private RecyclerView wallpaperRecyclerView;
    private ImageRecyclerViewAdapter adapter;
    private ImageData imageData;

    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private boolean isShuffle = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: CALLED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadImageData();

        adapter = new ImageRecyclerViewAdapter(this, imageData, this);
        wallpaperRecyclerView = findViewById(R.id.wallpaperRecyclerView);
        wallpaperRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        wallpaperRecyclerView.setAdapter(adapter);
        defineOnItemDragNDrop();

        initializePrefChangeListener();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: Called.");
        adapter.notifyDataSetChanged();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: called.");
        super.onStop();
        saveImageData();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    imageData.addUri(clipData.getItemAt(i).getUri());
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void OnImageClicked(int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(IMAGE_POSITION, position);
//        intent.setClipData(imageClipData);
//        intent.putExtra(IMAGE_DATA, imageData);
        startActivity(intent);
    }

    // Create Option Menu /////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Photos."), REQUEST_PICK_FROM_GALLERY);
                return true;
            case R.id.setting:
                startActivity(SettingsActivity.getSettingIntent(this));
        }
        return super.onOptionsItemSelected(item);
    }


    //Load & Save Image URIs As String Json/////////////////////////////////////////////////////////
    private void loadImageData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ImageData.IMAGE_DATA, MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(ImageData.URI_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> uris = gson.fromJson(jsonString, type);
        imageData = ImageData.getInstance();
        ArrayList<Uri> uriList = new ArrayList<>();
        if (uris != null) {
            for (String uriString :
                    uris) {
                Uri uri = Uri.parse(uriString);
                uriList.add(uri);
            }
        }
        imageData.setUriList(uriList);
    }

    private void saveImageData() {
        SharedPreferences sharedPreferences = getSharedPreferences(ImageData.IMAGE_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        List<String> uris = new ArrayList<>();
        for (Uri uri :
                imageData.getUriList()) {
            uris.add(uri.toString());
        }
        String jsonString = gson.toJson(uris);
        editor.putString(ImageData.URI_LIST, jsonString);
        editor.putInt(ImageData.CURRENT_URI, imageData.getQueueIndex());
        editor.apply();
    }

    //Moving image items////////////////////////////////////////////////////////////////////////////
    private void defineOnItemDragNDrop() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP
                        | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition() >= adapter.getItemCount() ? adapter.getItemCount() - 1 : target.getAdapterPosition();
                Collections.swap(imageData.getUriList(), from, to);
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(wallpaperRecyclerView);
    }


    // Enabling the Alarm Manager for Changing wallpapers //////////////////////////////////////////
    private void setAlarmManager(long time) {
        Log.d(TAG, "setAlarmManager: setting Alarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null && pendingIntent != null) {
            Log.d(TAG, "setAlarmManager: Alarm SET :: " + SystemClock.elapsedRealtime());
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), time, pendingIntent);
        }
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    private void initializePrefChangeListener(){
        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: changed :: " + key);
                if (key.equals("feature_status")){
                    Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.getBoolean(key, false));
                    if (sharedPreferences.getBoolean(key, false)){
                        String prefTimeString = sharedPreferences.getString("time_interval", "1000*60*30");
                        long time = parseTime(prefTimeString);
                        setAlarmManager(time);
                    }
                    else{
                        cancelAlarm();
                    }
                }
                else if (key.equals("time_interval")){
                    Log.d(TAG, "onSharedPreferenceChanged: " + sharedPreferences.getString(key, String.valueOf(1000*60*30)));
                    String preferencesTimeString = sharedPreferences.getString(key, String.valueOf(1000*60*30));
                    long time = parseTime(preferencesTimeString);
                    setAlarmManager(time);
                }
            }

            private long parseTime(String prefTimeString){
                long time = 1000 * 60 * 30;
                switch(prefTimeString){
                    case "1000*60*5":{
                        time = 1000 * 60 * 5;
                        break;
                    }
                    case "1000*60*10":{
                        time = 1000 * 60 * 10;
                        break;
                    }
                    case "1000*60*30": {
                        time = 1000 * 60 * 30;
                        break;
                    }
                    case "1000*60*60*1":{
                        time = 1000 * 60 * 60;
                        break;
                    }
                    case "1000*60*60*5":{
                        time = 1000 * 60 * 60 * 5;
                        break;
                    }
                    case "1000*60*60*24":{
                        time = 1000 * 60 * 60 * 24;
                        break;
                    }
                }
                return time;
            }
        };
    }

}