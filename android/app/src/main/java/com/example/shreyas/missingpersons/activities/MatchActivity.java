package com.example.shreyas.missingpersons.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.*;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView rv;
    private RequestQueue queue;

    private ArrayList<ImageItem> imageList = new ArrayList<>();
    private ImageAdapter adapter;
    private SharedPreferences sharedpreferences;
    private Button processButton;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String requestType;
    private String[] permissionList = {Manifest.permission.CALL_PHONE};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        requestAllPermissions();

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

        spinner = findViewById(R.id.file_select);

        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Found");
        spinnerItems.add("Missing");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                spinnerItems);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);


        processButton.setOnClickListener(this);

        adapter = new ImageAdapter(imageList, this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        rv.setHasFixedSize(true);
        rv.setVisibility(View.INVISIBLE);

        queue = Volley.newRequestQueue(this);

    }

    private void requestAllPermissions() {
        com.example.shreyas.missingpersons.PermissionManager.grantAllPermissions(MatchActivity.this, permissionList);
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
                                Log.e("Response",response);
                                if (!files[0].isEmpty()) {
                                    imageList.clear();
                                    for (int i = 0; i < files.length; i++) {
                                        Log.e("TAG", files[i]);
                                        String[] userData = files[i].split("\\|", -2);
                                        String[] userIdOfImageMatches = userData[0].split("_", -2);

                                        ImageItem imageItem = new ImageItem(Constants.IMAGE_REQUEST + '/' + userData[0], (i + 1) + "", userIdOfImageMatches[0], userData[1], userData[2], userData[3], userData[4], userData[5], userData[6]);
                                        imageList.add(imageItem);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);
                                    rv.setVisibility(View.VISIBLE);
                                    rv.getRecycledViewPool().clear();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MatchActivity.this,"No Match!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new ResponseErrorListener()) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        String userId = sharedpreferences.getString("user-id", "adi");
                        String password = sharedpreferences.getString("password", "qwerty");
                        data.put("user-id", userId);
                        data.put("password", password);
                        data.put("request-type", requestType);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        requestType = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        requestType = (String) parent.getItemAtPosition(0);
    }
}
