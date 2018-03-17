package com.example.shreyas.missingpersons.response;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by shreyas on 17/3/18.
 */

public class ResponseErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("TAG", "That didn't work!");
    }
}
