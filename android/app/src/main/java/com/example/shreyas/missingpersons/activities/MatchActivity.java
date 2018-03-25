package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.Constants;
import com.example.shreyas.missingpersons.ImageAdapter;
import com.example.shreyas.missingpersons.ImageItem;
import com.example.shreyas.missingpersons.R;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rv;
    private RequestQueue queue;

    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private ImageAdapter adapter;
    private SharedPreferences sharedpreferences;
    private Button processButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rv = findViewById(R.id.rv);
        processButton = findViewById(R.id.process);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);

        processButton.setOnClickListener(this);

        adapter = new ImageAdapter(imageList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);
        rv.setVisibility(View.INVISIBLE);

        queue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.process:
                rv.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MATCH,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String[] files = response.split(",", -2);
                                imageList.clear();
                                for (int i = 0; i < files.length; i++) {
                                    Log.e("TAG", files[i]);
                                    ImageItem imageItem = new ImageItem(Constants.IMAGE_REQUEST + '/' + files[i], (i + 1) + "");
                                    String[] userIdOfImageMatches = files[i].split("_", -2);
                                    imageItem.setImageDescription(userIdOfImageMatches[0]);
                                    imageList.add(imageItem);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                rv.setVisibility(View.VISIBLE);
                                rv.getRecycledViewPool().clear();
                                adapter.notifyDataSetChanged();
                            }
                        }, new ResponseErrorListener()) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        String userId = sharedpreferences.getString("user-id", "Default");
                        String password = sharedpreferences.getString("password", "Default");
                        data.put("user-id", userId);
                        data.put("password", password);
                        data.put("filename", "shre_2.jpg");
                        return data;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                queue.add(stringRequest);
                break;
        }
    }
}
