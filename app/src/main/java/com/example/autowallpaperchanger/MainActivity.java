package com.example.autowallpaperchanger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.autowallpaperchanger.ImageActivity.IMAGE_DATA;
import static com.example.autowallpaperchanger.ImageActivity.IMAGE_POSITION;

public class MainActivity extends AppCompatActivity implements ImageRecyclerViewAdapter.OnImageClickListener {
    private static final String TAG = "MainActivity";

    public static final int REQUEST_PICK_FROM_GALLERY = 1;

    private RecyclerView wallpaperRecyclerView;
    private ImageRecyclerViewAdapter adapter;
//    private List<Uri> uriList;
    private ClipData imageClipData;
    private ImageData imageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageData = new ImageData();
        adapter = new ImageRecyclerViewAdapter(this, imageData, this);
        wallpaperRecyclerView = findViewById(R.id.wallpaperRecyclerView);
        wallpaperRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        wallpaperRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null){
            imageClipData = data.getClipData();
            if (imageClipData != null) {
                for (int i = 0; i < imageClipData.getItemCount(); i++){
                    imageData.addUri(imageClipData.getItemAt(i).getUri());
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
        switch (item.getItemId()){
            case R.id.addImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Choose Photos."), REQUEST_PICK_FROM_GALLERY);
                return true;
            case R.id.setting:
                //todo go to settings
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void OnImageClicked(int position) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(IMAGE_POSITION, position);
//        intent.setClipData(imageClipData);
        intent.putExtra(IMAGE_DATA, imageData);
        startActivity(intent);
    }

    // Write & Read the JSON file to internal storage//////////////////////////////////////////////////////
    private void writeToInternalStorage(){
        File file = new File(getFilesDir(), ImageData.FILE_NAME);
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(imageData.createJsonFile());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private ImageData readFromInternalStorage(){
//        File file = new File(getFilesDir(), ImageData.FILE_NAME);
//        try {
//            FileReader fileReader = new FileReader(file);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//            StringBuilder stringBuilder = new StringBuilder();
//            String line = bufferedReader.readLine();
//            while(line != null){
//                stringBuilder.append(line).append("\n");
//                line = bufferedReader.readLine();
//            }
//            bufferedReader.close();
//            return new ImageData(stringBuilder.toString());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d(TAG, "readFromInternalStorage: IO EXCEPTION");
//        }
//        return null;
//    }

}