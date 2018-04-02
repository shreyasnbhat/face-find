package com.example.shreyas.missingpersons.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.Constants;
import com.example.shreyas.missingpersons.PermissionManager;
import com.example.shreyas.missingpersons.R;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity  implements View.OnClickListener {

    private SharedPreferences sharedpreferences;
    private RequestQueue queue;
    private Button reportMissingButton, reportFoundButton, findMatchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reportMissingButton = findViewById(R.id.report_missing);
        reportFoundButton = findViewById(R.id.report_found);
        findMatchButton = findViewById(R.id.find_match);

        reportMissingButton.setOnClickListener(this);
        reportFoundButton.setOnClickListener(this);
        findMatchButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_found:
                Intent intent_found = new Intent(HomeActivity.this, ReportFoundActivity.class);
                startActivity(intent_found);
                break;

            case R.id.report_missing:
                Intent intent_missing = new Intent(HomeActivity.this, ReportMissingActivity.class);
                startActivity(intent_missing);
                break;
            case R.id.find_match:
                Intent intent2 = new Intent(HomeActivity.this, MatchActivity.class);
                startActivity(intent2);
                break;
        }
    }

}
