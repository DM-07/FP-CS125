package com.example.android.whatevertrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class aboutpage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutpage);
        final TextView about = findViewById(R.id.about);
        about.setText("Powered by NEWS API");
    }
}