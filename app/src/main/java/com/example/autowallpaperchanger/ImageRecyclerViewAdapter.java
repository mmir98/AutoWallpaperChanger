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


public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ImageData data;
    private OnImageClickListener onImageClickListener;

    ImageRecyclerViewAdapter(Context context, ImageData data, OnImageClickListener onImageClickListener){
        this.context = context;
        this.data = data;
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);

        return new MyViewHolder(view, context, onImageClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        holder.imageView.setImageURI(data.get(position));
        Glide.with(context)
                .load(data.getUriList().get(position))
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.getUriList().size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageView;
        private DisplayMetrics displayMetrics = new DisplayMetrics();
        private OnImageClickListener onImageClickListener;

        public MyViewHolder(@NonNull View itemView, Context context, OnImageClickListener onImageClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
            ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            imageView.getLayoutParams().width = displayMetrics.widthPixels / 3;
            imageView.getLayoutParams().height = displayMetrics.widthPixels / 3 * 16 / 9;
            imageView.requestLayout();

            itemView.setOnClickListener(this);
            this.onImageClickListener = onImageClickListener;
        }

        @Override
        public void onClick(View v) {
            onImageClickListener.OnImageClicked(getAdapterPosition());
        }
    }

    public interface OnImageClickListener{
        void OnImageClicked(int position);
    }
}
