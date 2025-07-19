package com.onurtas.fleettracker.Helper;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkBridge extends Application {
    private static NetworkBridge instance;
    private RequestQueue requestQueue;

    public static synchronized NetworkBridge getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
