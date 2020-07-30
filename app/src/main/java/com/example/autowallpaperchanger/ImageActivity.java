package com.example.autowallpaperchanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;


public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ImageActivity";

    public static final String IMAGE_DATA = "IMAGE_DATA";
    public static final String IMAGE_POSITION = "IMAGE_POSITION";

    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
//    private List<Uri> imageUri = new ArrayList<>();
    private int startPosition;
    private ImageData imageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setupButtons();

//        ClipData clipData = getIntent().getClipData();
//        if (clipData != null) {
//            for (int i = 0; i < clipData.getItemCount(); i++) {
//                imageUri.add(clipData.getItemAt(i).getUri());
//            }
//        }
        if (getIntent().hasExtra(IMAGE_POSITION))
            startPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);
        if (getIntent().hasExtra(IMAGE_DATA))
            imageData = getIntent().getParcelableExtra(IMAGE_DATA);

        viewPager = findViewById(R.id.image_viewpager);
        imagePagerAdapter = new ImagePagerAdapter(this, imageData.getUriList());
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(startPosition);
    }

    private void setupButtons(){
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.delete_button).setOnClickListener(this);
        findViewById(R.id.set_as_wallpaper_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button: {
                onBackPressed();
                break;
            }
            case R.id.delete_button:{
                //todo implement delete action
            }
            case R.id.set_as_wallpaper_button:{
                setWallpaperThread();
                Toast.makeText(getApplicationContext(), "Wallpaper Changed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setWallpaperThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageData.getUriList().get(viewPager.getCurrentItem()));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}