package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.Constants;
import com.example.shreyas.missingpersons.R;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button testButton;
    private TextView resultText;
    private SharedPreferences sharedpreferences;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        testButton = findViewById(R.id.test);
        resultText = findViewById(R.id.result_text);
        testButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.test:
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.TEST,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                resultText.setText(response);
                            }
                        }, new ResponseErrorListener()) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        String userId = sharedpreferences.getString("user-id", "Default");
                        String password = sharedpreferences.getString("password", "Default");
                        data.put("user-id", userId);
                        data.put("password",password);
                        return data;
                    }
                };
                queue.add(stringRequest);
                break;
        }
    }
}
