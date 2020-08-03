package com.example.autowallpaperchanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import static com.example.autowallpaperchanger.ImageActivity.IMAGE_DATA;
import static com.example.autowallpaperchanger.ImageActivity.IMAGE_POSITION;

public class MainActivity extends AppCompatActivity implements ImageRecyclerViewAdapter.OnImageClickListener {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_PICK_FROM_GALLERY = 1;

    private RecyclerView wallpaperRecyclerView;
    private ImageRecyclerViewAdapter adapter;
    private ImageData imageData;


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
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveImageData();
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
//                    getContentResolver().takePersistableUriPermission(clipData.getItemAt(i).getUri() , Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        }
    }

    @Override
    public void OnImageClicked(int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(IMAGE_POSITION, position);
//        intent.setClipData(imageClipData);
        intent.putExtra(IMAGE_DATA, imageData);
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
                //todo go to settings
        }
        return super.onOptionsItemSelected(item);
    }


    //Load & Save Image URIs As String Json/////////////////////////////////////////////////////////
    private void loadImageData() {
        SharedPreferences sharedPreferences = getSharedPreferences(IMAGE_DATA, MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(ImageData.URI_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        List<String> uris = gson.fromJson(jsonString, type);
        imageData = new ImageData();
        if (uris != null) {
//            ContentResolver contentResolver = getContentResolver();
//            String[] projection = {MediaStore.MediaColumns.DATA};
            for (String uriString :
                    uris) {
                Uri uri = Uri.parse(uriString);
                imageData.addUri(uri);
//                Cursor cursor = contentResolver.query(uri, projection, null, null, null);
//                if (cursor != null && cursor.moveToFirst()){
////                    imageData.addUri(uri);
//
//                    for (String string :
//                            cursor.getColumnNames()) {
//                        Log.d(TAG, "loadImageData: " + string + " ::: " + cursor.getString(cursor.getColumnIndex(string)));
//                    }
//                    cursor.close();
////                    String path = cursor.getString(0);
////                    Log.d(TAG, "loadImageData: " + path);
////                    File file = new File(path);
////                    if (file != null && file.exists()){
////                        imageData.addUri(uri);
////                    }
//                }
            }
        }
    }

    private void saveImageData() {
        SharedPreferences sharedPreferences = getSharedPreferences(IMAGE_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        List<String> uris = new ArrayList<>();
        for (Uri uri :
                imageData.getUriList()) {
            uris.add(uri.toString());
        }
        String jsonString = gson.toJson(uris);
        editor.putString(ImageData.URI_LIST, jsonString);
        editor.apply();
    }

    //Moving image items////////////////////////////////////////////////////////////////////////////
    private void defineOnItemDragNDrop(){
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.DOWN | ItemTouchHelper.UP
                | ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition() >= adapter.getItemCount() ? adapter.getItemCount() - 1: target.getAdapterPosition();
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
}