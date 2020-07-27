package com.example.autowallpaperchanger;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Context context;
    private List<Uri> data;

    MyAdapter(Context context, List<Uri> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.imageView.setImageURI(data.get(position));
        Glide.with(context)
                .load(data.get(position))
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        DisplayMetrics displayMetrics = new DisplayMetrics();

        public MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
            ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            imageView.getLayoutParams().width = displayMetrics.widthPixels / 3;
            imageView.getLayoutParams().height = displayMetrics.widthPixels / 3 * 16 / 9;
            imageView.requestLayout();
        }
    }
}
