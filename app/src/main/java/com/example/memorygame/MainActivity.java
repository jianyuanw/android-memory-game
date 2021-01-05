package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(recyclerAdapter);

        progressBar = findViewById(R.id.progressBar);

        textView = findViewById(R.id.textView);

        findViewById(R.id.fetchButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fetchButton) {
            // TODO: Scrape website and populate GridView
            TextInputEditText input = findViewById(R.id.textInputEditText);
            if (input.getText() != null) {
                new LoadImagesLinksTask(this).execute(input.getText().toString());
            }

            // Starts game activity
            //Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            //startActivity(intent);
        }
    }

    @Override
    public void processFinish(boolean output) {
        if (output) {
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    public class LoadImagesLinksTask extends AsyncTask<String, Integer, Void> {
        public AsyncResponse delegate;
        private final ArrayList<String> imageLinks = new ArrayList<>();
        private int size;

        public LoadImagesLinksTask(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected Void doInBackground(String... urls) {
            for (String url : urls) {
                // Trim trailing slashes
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements images = doc.getElementsByTag("img");
                    size = images.size();
                    progressBar.setMax(images.size());
                    for (int i = 0; i < images.size(); i++) {
                        Element e = images.get(i);
                        publishProgress(i + 1);
                        String sourceAttribute = e.attr("src");
                        if (!sourceAttribute.startsWith("http")) {
                            imageLinks.add(url + sourceAttribute);
                        } else {
                            imageLinks.add(sourceAttribute);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            progressBar.setMax(imageLinks.size());

            recyclerAdapter.setUrls(imageLinks);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("");
            progressBar.setProgress(0);
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
            textView.setText(String.format(Locale.ENGLISH, "Processing img element %d of %d...", progress[0], size));
        }

        @Override
        protected void onPostExecute(Void result) {
            for (String s : imageLinks) {
                Log.d("test", s);
            }
            textView.setText("Processing URL elements done!");
            delegate.processFinish(imageLinks.size() > 0);
        }
    }
}