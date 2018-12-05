package com.example.android.whatevertrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class source {
    String title;
    String description;
    double latitude;
    double longitude;
    float[] distance = {999999, 0, 0};
    public source(String title, String description, double latitude, double longitude) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


