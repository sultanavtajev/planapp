package com.example.s199219;

// Importerer de nødvendige bibliotekene
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Starter en ny transaksjon med FragmentManager og erstatter innholdet
        // i den nåværende aktiviteten med et nytt AppPreferenceFragment.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AppPreferenceFragment())
                .commit();
    }
}

