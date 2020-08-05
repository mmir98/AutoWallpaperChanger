package com.example.autowallpaperchanger;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


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


    static class RecyclerViewOnItemClickedListener implements RecyclerView.OnItemTouchListener{
        private GestureDetector gestureDetector;
        private ItemClickListener clickListener;

        RecyclerViewOnItemClickedListener(Context context, final RecyclerView recyclerView, final ItemClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(child != null && clickListener != null){
                        clickListener.OnLongClicked(child, recyclerView.getChildLayoutPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)){
                clickListener.OnClicked(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface ItemClickListener {
        void OnClicked(View view, int position);

        void OnLongClicked(View view, int position);
    }
}
