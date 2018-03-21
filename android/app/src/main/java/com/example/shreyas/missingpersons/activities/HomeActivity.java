package com.example.shreyas.missingpersons.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.shreyas.missingpersons.Constants;
import com.example.shreyas.missingpersons.R;
import com.example.shreyas.missingpersons.response.ResponseErrorListener;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button testButton;
    private TextView resultText;
    private SharedPreferences sharedpreferences;
    private RequestQueue queue;
    private ImageView targetImage;
    private Button uploadButton;
    private String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestAllPermissions();

        testButton = findViewById(R.id.test);
        resultText = findViewById(R.id.result_text);
        uploadButton = findViewById(R.id.upload_button);
        targetImage = findViewById(R.id.target_image);

        testButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }

    private void requestAllPermissions() {
        PermissionManager.grantAllPermissions(HomeActivity.this, permissionList);
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
            case R.id.upload_button:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);
                targetImage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
