package com.example.shreyas.missingpersons.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

    private RequestQueue queue;

    private EditText nameText, userIdText, passwordText, ageText, genderText;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nameText = findViewById(R.id.name_text);
        passwordText = findViewById(R.id.password_text);
        ageText = findViewById(R.id.age_text);
        genderText = findViewById(R.id.gender_text);
        confirmButton = findViewById(R.id.confirm_button);
        userIdText = findViewById(R.id.user_id_text);

        confirmButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.confirm_button:
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_USER,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Snackbar.make(findViewById(R.id.add_user),
                                        response, Snackbar.LENGTH_SHORT).show();
                            }
                        }, new ResponseErrorListener()) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("username", nameText.getText().toString());
                        data.put("user-id", userIdText.getText().toString());
                        data.put("password", passwordText.getText().toString());
                        data.put("age", ageText.getText().toString());
                        data.put("gender", genderText.getText().toString());
                        return data;
                    }
                };
                queue.add(stringRequest);
                break;
        }
    }
}
