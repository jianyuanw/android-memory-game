package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListItemClickListener {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    ProgressBar progressBar;
    TextView textView;
    TextView helpText;
    Thread imageProcess = null;
    Button startButton;
    Button fetchButton;
    Handler mHandler = new Handler();
    AutoCompleteTextView input;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        int gridPreferences = Integer.parseInt(sharedPreferences.getString("grid", "3"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridPreferences));
        recyclerView.setAdapter(recyclerAdapter);

        progressBar = findViewById(R.id.progressBar);

        helpText = findViewById(R.id.helpText);

        // Initializing autocomplete for editText
        input = findViewById(R.id.textInputEditText);
        String[] websites = getResources().getStringArray(R.array.suggested_urls);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, websites);
        input.setAdapter(adapter);

        textView = findViewById(R.id.textView);

        fetchButton = findViewById(R.id.fetchButton);
        fetchButton.setOnClickListener(this);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        int gridPreferences = Integer.parseInt(sharedPreferences.getString("grid", "3"));

        recyclerAdapter.clearImages();
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridPreferences));
        textView.setVisibility(View.INVISIBLE);
        fetchButton.setEnabled(true);
        helpText.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.INVISIBLE);
        startButton.setEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Interrupt before switching to menu
            if (imageProcess != null) {
                imageProcess.interrupt();
            }

            Intent intent = new Intent(this, SettingsActivity.class);

            startActivity(intent);
        }

        if (id == R.id.highscore) {
            // Interrupt before switching to menu
            if (imageProcess != null) {
                imageProcess.interrupt();
            }

            Intent intent = new Intent(this, HighscoreActivity.class);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Deletes files on full exit from application
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        for (int i = 1; i <= 6; i++) {
            File file = new File(dir, "selectedImage" + i + ".jpg");
            if (file.exists()) {
                file.delete();
            }
        }
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
            // Scrape website and populate GridView
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

                imageProcess = new Thread(new LoadImagesTask(input.getText().toString()));
                imageProcess.start();

            }

        } else if (buttonId == R.id.startButton) {
            // Start button implementation
            Thread imageDownload = new Thread(new SaveImagesTask(recyclerAdapter.getSelectedImages()));
            imageDownload.start();

        }
    }

    @Override
    public void onListItemClick(int position) {
        View itemView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView tickBox = itemView.findViewById(R.id.tickBox);

        if (imageView.getBackground() == null) {
            imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.green_border));
            tickBox.setVisibility(View.VISIBLE);
            // activate this to try heart shape
            //imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.heart));
        } else {
            imageView.setBackground(null);
            tickBox.setVisibility(View.INVISIBLE);
        }

        if (recyclerAdapter.getSelectedImages().size() == 6) {
            startButton.setEnabled(true);
        } else if (startButton.isEnabled()) {
            startButton.setEnabled(false);
        }
    }

    private class LoadImagesTask implements Runnable {
        private String url;
        Elements images;

        LoadImagesTask(String url) {
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

                String fetchPreference = sharedPreferences.getString("fetch", "All");

                // For limiting image parsing depending on user preferences
                if (! fetchPreference.equals("All") && Integer.parseInt(fetchPreference) < images.size()) {
                    // Limits images to be parsed by removing excess images
                    images.subList(Integer.parseInt(fetchPreference), images.size()).clear();
                }

                progressBar.setMax(images.size());
                for (int i = 0; i < images.size(); i++) {
                    if (Thread.interrupted()) {
                        concludeUI(false);
                        break;
                    }
                    Element e = images.get(i);

                    String sourceAttribute = e.attr("src");
                    if (!sourceAttribute.startsWith("http")) {
                        sourceAttribute = url + sourceAttribute;
                    }
                    updateUI(i + 1, sourceAttribute);
                    Log.e("LOADIMAGE", sourceAttribute);

                }
                concludeUI(true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                concludeUI(false);
            }

        }

        private void startUI() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRecycledViewPool().clear();
                    recyclerAdapter.clearImages();
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("Starting to process images..");
                    textView.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.INVISIBLE);
                    helpText.setVisibility(View.INVISIBLE);
                }
            });
        }

        private void updateUI(int progress, String url) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(progress);
                                textView.setText(String.format(Locale.ENGLISH, "Downloading image %d of %d...", progress, images.size()));
                                recyclerAdapter.addImage(url, bmp);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).run();


        }

        private void concludeUI(boolean success) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        Toast.makeText(getApplicationContext(), "Images processed!", Toast.LENGTH_SHORT).show();
                    } else {
                        recyclerAdapter.clearImages();
                        Toast.makeText(getApplicationContext(), "Image loading failed!", Toast.LENGTH_SHORT).show();
                    }

                    imageProcess = null;
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    if (success) {
                        startButton.setVisibility(View.VISIBLE);
                        helpText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private class SaveImagesTask implements Runnable {
        private ArrayList<BitmapDrawable> bitmaps;

        SaveImagesTask(ArrayList<BitmapDrawable> bitmaps) {
            this.bitmaps = bitmaps;
        }

        @Override
        public void run() {
            startUI();

            for (int i = 0; i < bitmaps.size(); i++) {
                if (Thread.interrupted()) {
                    concludeUI(false);
                    break;
                }

                updateUI(i + 1);

                if (!save(bitmaps.get(i).getBitmap(), "selectedImage" + (i + 1) + ".jpg")) {
                    Thread.currentThread().interrupt();
                    concludeUI(false);
                    break;
                }
            }

            concludeUI(true);

        }

        public boolean save(Bitmap image, String filename) {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, filename);

            if (file.exists()) {
                file.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // TODO: Remove this if not necessary
        /*public boolean download(String downloadUrl, String filename) {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, filename);

            try {
                URL url = new URL(downloadUrl);
                URLConnection conn = url.openConnection();

                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }

                out.close();
                in.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }*/

        private void startUI() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fetchButton.setEnabled(false);
                    startButton.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(0);
                    progressBar.setMax(bitmaps.size());
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("Starting to download images..");
                    textView.setVisibility(View.VISIBLE);
                }
            });
        }

        private void updateUI(int progress) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    textView.setText(String.format(Locale.ENGLISH, "Saving image %d of %d...", progress, bitmaps.size()));
                }
            });

        }

        private void concludeUI(boolean success) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);

                    if (success) {
                        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                        startActivity(intent);
                    } else {
                        startButton.setVisibility(View.VISIBLE);
                        fetchButton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Downloading of images failed, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}