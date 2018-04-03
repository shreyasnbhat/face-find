package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.R;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button reportButton, findMatchButton, viewImagesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        reportButton = findViewById(R.id.report);
        findMatchButton = findViewById(R.id.find_match);
        viewImagesButton = findViewById(R.id.view_button);

        reportButton.setOnClickListener(this);
        findMatchButton.setOnClickListener(this);
        viewImagesButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report:
                Intent report = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(report);
                break;
            case R.id.find_match:
                Intent intent2 = new Intent(HomeActivity.this, MatchActivity.class);
                startActivity(intent2);
                break;
            case R.id.view_button:
                Intent intent3 = new Intent(HomeActivity.this, ImageDisplayActivity.class);
                startActivity(intent3);
                break;
        }
    }

}
