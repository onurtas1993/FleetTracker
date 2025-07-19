package com.onurtas.fleettracker.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.onurtas.fleettracker.Helper.LocaleHelper;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply locale before anything else
        applySavedLocale();
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view -> {
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                String versionLabel = getString(R.string.app_version_label);
                String message = versionLabel + versionName;

                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null)
                        .show();
            } catch (PackageManager.NameNotFoundException e) {
                Snackbar.make(view, getString(R.string.error_getting_version), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void applySavedLocale() {
        SharedPreferences sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        // Provide a default(en) language if none is saved
        String lang = sharedPref.getString("LANGUAGE", "en");
        LocaleHelper.updateResources(this, lang);
    }
}