package com.example.shreyas.missingpersons.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.*;
import com.example.shreyas.missingpersons.PermissionManager;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageDisplayActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private ImageAdapter adapter;
    private SharedPreferences sharedpreferences;
    private ProgressBar progressBar;
    private String[] permissionList = {Manifest.permission.CALL_PHONE};

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        requestAllPermissions();

        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rv = findViewById(R.id.rv);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);


        adapter = new ImageAdapter(imageList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);

        queue = Volley.newRequestQueue(this);
        request();
    }

    private void requestAllPermissions() {
        PermissionManager.grantAllPermissions(ImageDisplayActivity.this, permissionList);
    }



    public void request() {
        rv.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_IMAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (!response.equals("Auth Failed")) {
                            String[] data = response.split(",", -2);

                            String[] imageCountsString = data[0].split("\\|", -2);
                            int imageCountMissing = Integer.valueOf(imageCountsString[0]);
                            int imageCountFound = Integer.valueOf(imageCountsString[1]);
                            Log.e("TAG1", imageCountMissing + " " + imageCountFound);

                            String userId = sharedpreferences.getString("user-id", "adi");

                            int i = 1;
                            for (; i <= imageCountMissing; i++) {
                                String[] userData = data[i].split("\\|", -2);
                                Log.e("URL", Integer.toString(userData.length));
                                String imageUrl = Constants.IMAGE_REQUEST + "/" + userId + "_" + "M" + i + ".jpg";
                                Log.e("URL", imageUrl);
                                ImageItem imageData = new ImageItem(imageUrl, i + "", userId, userData[0], userData[1], userData[2], userData[3], userData[4], userData[5]);
                                imageList.add(imageData);
                            }

//                            imageList.add(new ImageItem("http://google.com", ""));

                            for (i = imageCountMissing + 1; i <= imageCountMissing + imageCountFound; i++) {
                                Log.e("TAGMISS", data[i] + " " + i);
                                String[] userData = data[i].split("\\|", -2);
                                String imageUrl = Constants.IMAGE_REQUEST + "/" + userId + "_" + "F" + (i - imageCountMissing) + ".jpg";
                                Log.e("URLMISS", imageUrl);
                                ImageItem imageData = new ImageItem(imageUrl, (i - imageCountMissing) + "", userId, userData[0], userData[1], userData[2], userData[3], userData[4], userData[5]);
                                imageList.add(imageData);
                            }

                            progressBar.setVisibility(View.INVISIBLE);
                            rv.setVisibility(View.VISIBLE);
                            rv.getRecycledViewPool().clear();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }, new ResponseErrorListener()) {
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                String userId = sharedpreferences.getString("user-id", "adi");
                String password = sharedpreferences.getString("password", "qwerty");
                data.put("user-id", userId);
                data.put("password", password);
                return data;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


}
