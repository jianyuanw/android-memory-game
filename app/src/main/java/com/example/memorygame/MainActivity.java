package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ProgressBar progressBar;
    TextView textView;
    Thread imageProcess = null;
    boolean imageReady = false;
    Handler mHandler = new Handler();
    private TextView mTextMessage;
    private Button btn1;

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

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.fetchButton).setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.btn1:
               Intent i= new Intent(MainActivity.this, GameActivity.class);
               startActivity(i);
                //break;
        }
        if (v.getId() == R.id.fetchButton) {
            // TODO: Scrape website and populate GridView
            TextInputEditText input = findViewById(R.id.textInputEditText);
            if (input.getText() != null) {
                if (imageProcess != null) {
                    imageProcess.interrupt();
                }
                imageProcess = new Thread(new LoadImagesThread(input.getText().toString()));
                imageProcess.start();
                // new LoadImagesLinksTask(this).execute(input.getText().toString());
            }

            // Starts GameActivity activity
            //Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            //startActivity(intent);
        }
    }

    private class LoadImagesThread implements Runnable {
        private String url;
        private final ArrayList<String> imageLinks = new ArrayList<>();
        Elements images;

        LoadImagesThread(String url) {
            this.url = url;
        }
        @Override
        public void run() {
            if (!URLUtil.isValidUrl(url)) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Invalid URL, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            startUI();
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            try {
                Document doc = Jsoup.connect(url).get();
                images = doc.select("img[src~=(?i).(gif|png|jpe?g)]");
                progressBar.setMax(images.size());
                for (int i = 0; i < images.size(); i++) {
                    Thread.sleep(150);
                    if (Thread.interrupted()) {
                        closeThread(false);
                    }
                    Element e = images.get(i);
                    updateUI(i + 1);
                    String sourceAttribute = e.attr("src");
                    if (!sourceAttribute.startsWith("http")) {
                        imageLinks.add(url + sourceAttribute);
                    } else {
                        imageLinks.add(sourceAttribute);
                    }
                    Log.e("TESTING", sourceAttribute);
                    recyclerAdapter.setUrls(imageLinks);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                closeThread(false);
            }
            closeThread(true);
        }

        private void startUI() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerAdapter.setUrls(imageLinks);
                    recyclerAdapter.notifyDataSetChanged();
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("Starting to process images..");
                    textView.setVisibility(View.VISIBLE);
                }
            });
        }

        private void updateUI(int progress) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    textView.setText(String.format(Locale.ENGLISH, "Processing img element %d of %d...", progress, images.size()));
                    recyclerAdapter.notifyItemChanged(progress - 1);
                }
            });

        }

        private void closeThread(boolean success) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        Toast.makeText(getApplicationContext(), "Images processed!", Toast.LENGTH_SHORT).show();
                        imageReady = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Image loading failed!", Toast.LENGTH_SHORT).show();
                        imageReady = false;
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    imageProcess = null;
                }
            });
        }
    }

    public class LoadImagesLinksTask extends AsyncTask<String, Integer, Void> {
        private final ArrayList<String> imageLinks = new ArrayList<>();
        private int size;

        @Override
        protected Void doInBackground(String... urls) {
            for (String url : urls) {
                // Trim trailing slashes
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements images = doc.select("img[src~=(?i).(gif|png|jpe?g)]");
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
                        Log.e("TESTING", sourceAttribute);
                        recyclerAdapter.setUrls(imageLinks);
                        updateUI(i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    recyclerAdapter.setUrls(imageLinks);
                }
            }

            progressBar.setMax(imageLinks.size());

//            recyclerAdapter.setUrls(imageLinks);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerAdapter.setUrls(imageLinks);
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
            textView.setText("Done!");
        }

        void updateUI(int position) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerAdapter.notifyItemChanged(position);
                }
            });
        }
    }
}