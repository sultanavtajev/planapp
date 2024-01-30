package com.example.s199219;

// Importerer nødvendige biblioteker og pakker

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

// Definerer hovedaktiviteten for appen
public class MainActivity extends AppCompatActivity {

    // Deklarerer konstanter og variabler
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private AppointmentAdapter appointmentAdapter;
    private final BroadcastReceiver myReceiver = new MinBroadcastReceiver();

    // Metoden som kjøres når aktiviteten opprettes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Be om SMS-tillatelse fra brukeren
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Snackbar.make(findViewById(android.R.id.content), "SMS permission is needed to send messages.",
                                Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", view -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS))
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        // Registrerer en BroadcastReceiver for spesifikke intent-handlinger
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction("com.example.s199219.SETTING_CHANGED");
        registerReceiver(myReceiver, filter);

        // Sender en kringkasting for å varsle om endring i innstillinger
        Intent intent = new Intent();
        intent.setAction("com.example.s199219.SETTING_CHANGED");
        sendBroadcast(intent);

        // Initialiserer ViewModel og RecyclerView
        AppointmentViewModel appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appointmentAdapter);

        // Observerer dataendringer og oppdaterer RecyclerView
        appointmentViewModel.getAppointments().observe(this, appointments -> appointmentAdapter.setAppointments(appointments));

        // Logger innholdet i databasen (for feilsøking eller visning)
        appointmentViewModel.logDatabaseContents(this);

        // Initialiserer knapper og deres klikkhåndterere
        FloatingActionButton showPreferencesButton = findViewById(R.id.showPreferencesButton);
        showPreferencesButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent1);
        });

        FloatingActionButton showFriendsButton = findViewById(R.id.showFriendsButton);
        showFriendsButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(intent1);
        });

        FloatingActionButton showDialogButton = findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(v -> {
            MultiStepDialogFragment dialogFragment = new MultiStepDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "multiStepDialog");
        });
    }

    // Metoden som kjøres når aktiviteten ødelegges
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Avregistrerer BroadcastReceiver for å unngå minnelekkasjer
        unregisterReceiver(myReceiver);

        // Lukker ExecutorService i AppDatabase for å håndtere ressurser effektivt
        AppDatabase.shutDownExecutorService();
    }
}
