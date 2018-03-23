package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

        processButton.setOnClickListener(this);

        adapter = new ImageAdapter(imageList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        queue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.process:
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.MATCH,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String[] files = response.split(",", -2);
                                for (int i = 0; i < files.length; i++) {
                                    Log.e("TAG", files[i]);
                                    imageList.add(new ImageItem(Constants.IMAGE_REQUEST + '/' + files[i], (i + 1) + ""));
                                }
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
                        data.put("filename", "shre_1.jpg");
                        return data;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        3000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                queue.add(stringRequest);
                break;
        }
    }
}
