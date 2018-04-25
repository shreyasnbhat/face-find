package com.example.shreyas.missingpersons.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private SharedPreferences sharedpreferences;
    private RequestQueue queue;
    private ImageView targetImage;
    private FloatingActionButton addImageButton;
    private Button postImageButton, listImageButton;
    private Bitmap imageBitmap;
    private String[] permissionList = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private EditText nameText, ageText, genderText, locationText, phoneText;
    private String childStatus;
    private Spinner spinner;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        requestAllPermissions();

        locationManager = (LocationManager) ReportActivity.this.getSystemService(Context.LOCATION_SERVICE);
        addImageButton = findViewById(R.id.add_image);
        targetImage = findViewById(R.id.target_image);
        postImageButton = findViewById(R.id.upload_image);
        nameText = findViewById(R.id.edit_name);
        ageText = findViewById(R.id.edit_age);
        genderText = findViewById(R.id.edit_gender);
        locationText = findViewById(R.id.edit_location);
        phoneText = findViewById(R.id.edit_phone);
        spinner = findViewById(R.id.child_status_match);

        Location location = null;
        if (PermissionManager.checkPermission(ReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                PermissionManager.checkPermission(ReportActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    0,
                    0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location!=null)
                locationText.setText(location.getLatitude()+","+location.getLongitude());
            else{
                locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
                        0,
                        0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location!=null)
                    locationText.setText(location.getLatitude()+","+location.getLongitude());
                else
                    locationText.setText("Location Coordinates");
            }
        }
        else{
            locationText.setText("Location Coordinates");
        }


        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Found");
        spinnerItems.add("Missing");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                spinnerItems);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);

        addImageButton.setOnClickListener(this);
        postImageButton.setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences("Session", Context.MODE_PRIVATE);

    }

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void requestAllPermissions() {
        PermissionManager.grantAllPermissions(ReportActivity.this, permissionList);
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
                if (isEmpty(nameText) || isEmpty(ageText) || isEmpty(genderText) || isEmpty(locationText)) {
                    Toast.makeText(this, "Please fill all the details.", Toast.LENGTH_SHORT).show();
                } else if (imageBitmap != null) {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] imageBytes = baos.toByteArray();
                    final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    Toast.makeText(this,locationText.getText().toString().split(",")[0]+","+ locationText.getText().toString().split(",")[1], Toast.LENGTH_SHORT).show();

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
                            data.put("name", nameText.getText().toString());
                            data.put("age", ageText.getText().toString());
                            data.put("gender", genderText.getText().toString());
                            data.put("latitude", locationText.getText().toString().split(",")[0]);
                            data.put("longitude", locationText.getText().toString().split(",")[1]);
                            data.put("phone",phoneText.getText().toString());
                            data.put("child_status", childStatus);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        childStatus = (String) parent.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        childStatus = (String) parent.getItemAtPosition(0);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}