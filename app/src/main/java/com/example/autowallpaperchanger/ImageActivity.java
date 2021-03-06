package com.example.autowallpaperchanger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;


public class ImageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ImageActivity";

    ProgressDialog pd;

    //    public static final String IMAGE_DATA = "IMAGE_DATA";
    public static final String IMAGE_POSITION = "IMAGE_POSITION";

    private ViewPager viewPager;
    private ImagePagerAdapter imagePagerAdapter;
    private int startPosition;
    private ImageData imageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setupButtons();
        pd = new ProgressDialog(ImageActivity.this);
        imageData = ImageData.getInstance();

        if (getIntent().hasExtra(IMAGE_POSITION))
            startPosition = getIntent().getIntExtra(IMAGE_POSITION, 0);

        viewPager = findViewById(R.id.image_viewpager);
        imagePagerAdapter = new ImagePagerAdapter(this, imageData.getUriList());
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(startPosition);
    }

    private void setupButtons() {
        findViewById(R.id.back_button).setOnClickListener(this);
        findViewById(R.id.back_icon).setOnClickListener(this);
        findViewById(R.id.delete_button).setOnClickListener(this);
        findViewById(R.id.delete_icon).setOnClickListener(this);
        findViewById(R.id.set_as_wallpaper_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_icon: {
                Log.d(TAG, "onClick: back");
                onBackPressed();
                break;
            }
            case R.id.back_button:{
                onBackPressed();
                break;
            }
            case R.id.delete_button | R.id.delete_icon: {
                Log.d(TAG, "onClick: delete");
                deleteImage();
                break;
            }
            case R.id.set_as_wallpaper_button: {
                pd.setMessage("Loading to set wallpaper");
                pd.show();
                setWallpaperThread();
                equalizeQueuePointer();
                Toast.makeText(ImageActivity.this, "Wallpaper will change soon", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setWallpaperThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver()
                            , imageData.getUriList().get(viewPager.getCurrentItem()));
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                    } else {
                        wallpaperManager.setBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pd.cancel();
            }
        });
        thread.start();
    }

    private void equalizeQueuePointer(){
        imageData.setQueueIndex((viewPager.getCurrentItem() + 1) % imageData.getUriList().size());
    }

    private void deleteImage() {
        Log.d(TAG, "deleteImage: index ::" + viewPager.getCurrentItem());
        int uriListSize = imageData.getUriList().size();
        if (uriListSize > 0) {
            int pos = viewPager.getCurrentItem();
            imagePagerAdapter.removeItem(pos);
            if (imageData.getUriList().size() == 0) {
                onBackPressed();
            }
        } else {
            onBackPressed();
        }
    }
}