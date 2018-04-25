package com.example.shreyas.missingpersons;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Created by shreyas on 23/3/18.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {

    public TextView imageIdTextView;
    public ImageView imageView;
    public TextView imageDescriptionTextView;


    private ImageItem imageItem;

    public ImageViewHolder(View itemView, final Context context) {
        super(itemView);

        imageIdTextView = itemView.findViewById(R.id.image_id);
        imageView = itemView.findViewById(R.id.image);
        imageDescriptionTextView = itemView.findViewById(R.id.image_desc);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(imageItem.toString());
                alertDialogBuilder.setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {}
                            });
                alertDialogBuilder.setNegativeButton("Show on Map",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s,%s(%s)",imageItem.getLatitude(), imageItem.getLongitude(),"Last Known Location");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setPackage("com.google.android.apps.maps");
                                context.startActivity(intent);
                            }
                        });
                alertDialogBuilder.setPositiveButton("Call",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+imageItem.getPhone()));

                        if (ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        context.startActivity(callIntent);
                    }

                });
                alertDialogBuilder.setTitle("Image Details");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //Toast.makeText(context,imageItem.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setImageItem(ImageItem imageItem) {
        this.imageItem = imageItem;
    }

    public void setImage() {
        Picasso.get().load(imageItem.getImageUrl()).networkPolicy(NetworkPolicy.NO_CACHE).fit().centerCrop().placeholder(R.drawable.progress).into(imageView);
    }

}
