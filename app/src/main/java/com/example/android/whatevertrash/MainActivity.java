package com.example.android.whatevertrash;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String News_url = "https://newsapi.org/v2/everything?q=apple&from=2018-11-17&to=2018-11-17&sortBy=popularity&apiKey=1725db16d6344228a4de05eda3150d5b";

    source[] newsstream;
    RecyclerView newsrecycleview;
    newsadaptor newsadaptor;
    RecyclerView.LayoutManager newslayoutmanager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView console = findViewById(R.id.console);
        final TextView newstitle = findViewById(R.id.newstitle);
        final TextView newsdescription = findViewById(R.id.newsdescription);
        final FloatingActionButton b = findViewById(R.id.floatingActionButton);
        final SwipeRefreshLayout refresh = findViewById(R.id.swiperefresh);
        final NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(this);
        final Toast toast = Toast.makeText(this, "No Internet Connection, Please refresh", Toast.LENGTH_SHORT);

        createNotificationChannel();

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent aboutintent = new Intent(getApplicationContext(), aboutpage.class);
                startActivity(aboutintent);
            }
        });

        final Notification refreshfailBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID_1")
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle("Sorry")
                .setContentText("No Internet Connection, Please refresh")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new MainActivity.Asynchttptask().execute(News_url);
                if (newsstream == null || newsstream.length == 0) {
                    notificationmanager.notify(10, refreshfailBuilder);
                    refresh.setRefreshing(false);
                    toast.show();
                } else {
                    newsrecycleview = findViewById(R.id.newsrecycleview);
                    newsadaptor = new newsadaptor(newsstream);
                    newslayoutmanager = new LinearLayoutManager(getBaseContext());
                    newsrecycleview.setAdapter(newsadaptor);
                    newsrecycleview.setLayoutManager(newslayoutmanager);
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelid = "CHANNEL_ID_1";
            String channelname = "channel_1";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelid, channelname, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("default channel for notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public class Asynchttptask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                String response = streamtostring(urlConnection.getInputStream());
                parseresult(response);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    String streamtostring(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String data;
        String result = "";
        while ((data = bufferedReader.readLine()) != null) {
            result += data;
        }
        if (stream != null) {
            stream.close();
        }
        return result;
    }

    private void parseresult(String result) {
        JSONObject response = null;
        try {
            response = new JSONObject(result);
            JSONArray articles = response.optJSONArray("articles");
            newsstream = new source[articles.length()];
            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.optJSONObject(i);
                newsstream[i] = new source(article.optString("title"), article.optString("description"), article.optString("url"));
                Log.i("titles  ", newsstream[i].title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

