package com.example.shreyas.missingpersons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by shreyas on 20/1/18.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    private ArrayList<ImageItem> imageList;
    private Context context;

    public ImageAdapter(ArrayList<ImageItem> imageList, Context context) {
        this.imageList = imageList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.image_item_format, parent, false);
        return new ImageViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ImageItem item = imageList.get(position);
        holder.setImageItem(item);
        holder.setImage();
        holder.imageIdTextView.setText(item.getImageId());
        holder.imageDescriptionTextView.setText(item.getImageDescription());
    }
}
