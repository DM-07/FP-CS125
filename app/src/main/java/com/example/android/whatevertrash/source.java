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
    String url;
    public source(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }
}
