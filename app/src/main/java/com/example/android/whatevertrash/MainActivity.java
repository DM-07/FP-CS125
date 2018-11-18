package com.example.android.whatevertrash;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView console = findViewById(R.id.console);
        final TextView newstitle = findViewById(R.id.newstitle);
        final TextView newsdescription = findViewById(R.id.newsdescription);
        final FloatingActionButton b = findViewById(R.id.floatingActionButton);
        new MainActivity.Asynchttptask().execute(News_url);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (newsstream == null || newsstream.length == 0) {
                    console.setText("No Internet Connection, Please refresh!");
                    new MainActivity.Asynchttptask().execute(News_url);
                } else {
                    console.setText("All is Well.");
                    newsrecycleview = findViewById(R.id.newsrecycleview);
                    newsadaptor = new newsadaptor(newsstream);
                    newslayoutmanager = new LinearLayoutManager(getBaseContext());
                    newsrecycleview.setAdapter(newsadaptor);
                    newsrecycleview.setLayoutManager(newslayoutmanager);
                }
            }
        });
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

