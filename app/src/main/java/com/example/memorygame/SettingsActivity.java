package com.example.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinner;
    Button saveButton;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fetch_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String fetchPreference = sharedPreferences.getString("fetch", null);

        if (fetchPreference != null) {
            spinner.setSelection(adapter.getPosition(fetchPreference));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveButton) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fetch", spinner.getSelectedItem().toString());
            editor.apply();
            finish();
        }
    }
}