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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedpreferences;
    private RequestQueue queue;
    private ImageView targetImage;
    private FloatingActionButton addImageButton;
    private Button postImageButton, listImageButton, matchButton;
    private Bitmap imageBitmap;
    private String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestAllPermissions();

        addImageButton = findViewById(R.id.add_image);
        targetImage = findViewById(R.id.target_image);
        postImageButton = findViewById(R.id.upload_image);
        listImageButton = findViewById(R.id.list_image);
        matchButton = findViewById(R.id.find_match);

        addImageButton.setOnClickListener(this);
        postImageButton.setOnClickListener(this);
        listImageButton.setOnClickListener(this);
        matchButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }

    private void requestAllPermissions() {
        PermissionManager.grantAllPermissions(HomeActivity.this, permissionList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_image:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
                break;

            case R.id.upload_image:
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] imageBytes = baos.toByteArray();
                    final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    StringRequest request = new StringRequest(Request.Method.POST, Constants.UPLOAD_IMAGE,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Snackbar.make(findViewById(R.id.home_layout),
                                            "Image was uploaded successfully", Snackbar.LENGTH_SHORT).show();

                                }
                            }, new ResponseErrorListener()) {
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<>();
                            String userId = sharedpreferences.getString("user-id", "Default");
                            String password = sharedpreferences.getString("password", "Default");
                            data.put("user-id", userId);
                            data.put("password", password);
                            data.put("image", imageString);
                            return data;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(
                            0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(request);
                    break;


                } else {
                    Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.list_image:
                Intent intent1 = new Intent(HomeActivity.this, ImageDisplayActivity.class);
                startActivity(intent1);
                break;
            case R.id.find_match:
                Intent intent2 = new Intent(HomeActivity.this, MatchActivity.class);
                startActivity(intent2);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            try {
                imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(imageBitmap);
                targetImage.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
