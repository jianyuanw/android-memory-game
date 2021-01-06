package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListItemClickListener {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ProgressBar progressBar;
    TextView textView;
    Thread imageProcess = null;
    Button startButton;
    Button fetchButton;
    boolean imageReady = false;
    Handler mHandler = new Handler();
    TextInputEditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(recyclerAdapter);

        progressBar = findViewById(R.id.progressBar);

        input = findViewById(R.id.textInputEditText);

        textView = findViewById(R.id.textView);

        fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        if (buttonId == R.id.fetchButton) {
            // TODO: Scrape website and populate GridView
            hideKeyboard(this);
            if (input.getText() != null) {
                if (imageProcess != null) {
                    imageProcess.interrupt();
                    try {
                        imageProcess.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                imageProcess = new Thread(new LoadImagesThread(input.getText().toString()));
                imageProcess.start();
                // new LoadImagesLinksTask(this).execute(input.getText().toString());
            }

            // Starts game activity
            //Intent intent = new Intent(getApplicationContext(), GameActivity.class);
            //startActivity(intent);
        } else if (buttonId == R.id.startButton) {
            // TODO: Start button implementation
        }
    }

    @Override
    public void onListItemClick(int position) {
        ImageView imageView = recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.imageView);
        if (imageView.getBackground() == null) {
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.green_border));
        } else {
            imageView.setBackground(null);
        }

        if (recyclerAdapter.getSelectedUrls().size() == 6) {
            startButton.setEnabled(true);
        } else if (startButton.isEnabled()) {
            startButton.setEnabled(false);
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
            try {
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
                Document doc = Jsoup.connect(url).get();
                images = doc.select("img[src~=(?i).(gif|png|jpe?g)]");
                progressBar.setMax(images.size());
                for (int i = 0; i < images.size(); i++) {
                    // NOTE: Slight sleep to ensure sequential loading is working, consider removing in final
                    Thread.sleep(100);
                    if (Thread.interrupted()) {
                        closeThread(false);
                    }
                    Element e = images.get(i);

                    String sourceAttribute = e.attr("src");
                    if (!sourceAttribute.startsWith("http")) {
                        sourceAttribute = url + sourceAttribute;
                    }
                    updateUI(i + 1, sourceAttribute);

                    Log.e("LOADIMAGE", sourceAttribute);

                    closeThread(true);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                closeThread(false);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }

        private void startUI() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRecycledViewPool().clear();
                    recyclerAdapter.clearUrls();
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("Starting to process images..");
                    textView.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                }
            });
        }

        private void updateUI(int progress, String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    textView.setText(String.format(Locale.ENGLISH, "Processing img element %d of %d...", progress, images.size()));
                    recyclerAdapter.addUrl(url);
                }
            });

        }

        private void closeThread(boolean success) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        Toast.makeText(getApplicationContext(), "Images processed!", Toast.LENGTH_SHORT).show();
                        startButton.setVisibility(View.VISIBLE);
                        imageReady = true;
                    } else {
                        recyclerAdapter.clearUrls();
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

    /*public class LoadImagesLinksTask extends AsyncTask<String, Integer, Void> {
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

                        updateUI(i);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            progressBar.setMax(imageLinks.size());

//            recyclerAdapter.setUrls(imageLinks);

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
    }*/
}