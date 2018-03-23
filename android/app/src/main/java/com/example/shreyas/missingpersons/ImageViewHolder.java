package com.example.shreyas.missingpersons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by shreyas on 23/3/18.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {

    public TextView imageIdTextView;
    public ImageView imageView;

    private ImageItem imageItem;

    public ImageViewHolder(View itemView, final Context context) {
        super(itemView);

        imageIdTextView = itemView.findViewById(R.id.image_id);
        imageView = itemView.findViewById(R.id.image);
    }

    public void setImageItem(ImageItem imageItem) {
        this.imageItem = imageItem;
    }

    public void setImage() {
        Picasso.get().load(imageItem.getImageUrl()).networkPolicy(NetworkPolicy.NO_CACHE).fit().centerCrop().placeholder(R.drawable.progress).into(imageView);
    }
}
