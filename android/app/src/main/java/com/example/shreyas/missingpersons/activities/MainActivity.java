package com.example.shreyas.missingpersons.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userIdText, passwordText;
    private Button loginButton, addUserButton;
    private TextView resultText;
    private RequestQueue queue;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginButton = findViewById(R.id.login);
        addUserButton = findViewById(R.id.add_user);
        userIdText = findViewById(R.id.user_id_text);
        passwordText = findViewById(R.id.password_text);
        resultText = findViewById(R.id.result_text);

        addUserButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);

        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login:
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTHENTICATE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                resultText.setText(response);
                                if (response.contains("success")) {
                                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(i);
                                }
                            }
                        }, new ResponseErrorListener()) {
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("user-id", userIdText.getText().toString());
                        editor.apply();
                        data.put("user-id", userIdText.getText().toString());
                        data.put("password", passwordText.getText().toString());
                        return data;
                    }
                };
                queue.add(stringRequest);
                break;
            case R.id.add_user:
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
                break;
        }
    }
}
