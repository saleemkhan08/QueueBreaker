package com.thnki.queuebreaker.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thnki.queuebreaker.listeners.ResultListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class VolleyUtil {
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;

    public static void sendGetData(Context context, final String url, final Map<String, String> getData, final ResultListener<String> listener) throws UnsupportedEncodingException {
        String customURL = url + "?";
        for (String key : getData.keySet()) {
            customURL += key + "=" + URLEncoder.encode(getData.get(key), "utf-8") + "&";
        }
        customURL = customURL.substring(0, customURL.length() - 1);
        Log.d("sendGetData", customURL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, customURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (listener != null) {
                            listener.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null) {
                            listener.onError(error);
                        }
                    }
                });

        RequestQueue requestQueue = VolleySingleton.getInstance(context).getRequestQueue();
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}

class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private RequestQueue mRequestQueue;

    private VolleySingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static VolleySingleton getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new VolleySingleton(context);
        }
        return sInstance;
    }

    RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}