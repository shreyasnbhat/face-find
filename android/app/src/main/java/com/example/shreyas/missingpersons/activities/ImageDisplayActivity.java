package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.shreyas.missingpersons.Constants;
import com.example.shreyas.missingpersons.ImageAdapter;
import com.example.shreyas.missingpersons.ImageItem;
import com.example.shreyas.missingpersons.R;

import java.util.ArrayList;

public class ImageDisplayActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private ImageAdapter adapter;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rv = findViewById(R.id.rv);
        adapter = new ImageAdapter(imageList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);

        String userId = sharedpreferences.getString("user-id", "Default");

        for (int i = 1; i <= 5; i++) {
            imageList.add(new ImageItem(Constants.IMAGE_REQUEST + "/" + userId + "_" + i + ".jpg", i + ""));
        }
        adapter.notifyDataSetChanged();
    }
}
