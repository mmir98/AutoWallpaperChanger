package com.example.autowallpaperchanger;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<Uri> imageUri;

    ImagePagerAdapter(Context context, List<Uri> data){
        this.context = context;
        imageUri = data;
    }

    @Override
    public int getCount() {
        return imageUri.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setImageURI(imageUri.get(position));
        Glide.with(context)
                .load(imageUri.get(position))
                .centerCrop()
                .into(imageView);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(((ImageView) object));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void removeItem(int position){
        imageUri.remove(position);
        notifyDataSetChanged();
    }
}
