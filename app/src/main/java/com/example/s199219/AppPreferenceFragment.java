package com.example.s199219;

// Importerer TimePickerDialog for å velge tid
import android.app.TimePickerDialog;
// Importerer Intent for å starte aktiviteter og tjenester
import android.content.Intent;
// Importerer SharedPreferences for å lagre data lokalt
import android.content.SharedPreferences;
// Importerer Bundle for å lagre tilstandsinformasjon
import android.os.Bundle;

// Importerer Preference og PreferenceFragmentCompat for å håndtere innstillinger
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

// Klasse som håndterer appens preferansefragment
public class AppPreferenceFragment extends PreferenceFragmentCompat {

    // Metode som blir kalt når preferansefragmentet blir opprettet
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        // Setter preferanser fra en XML-ressurs
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Finner preferanse ved hjelp av nøkkel
        Preference timePreference = findPreference("sms_time");
        if (timePreference != null) {

            // Setter en klikklytter for denne preferansen
            timePreference.setOnPreferenceClickListener(preference -> {

                // Henter lagret tid fra SharedPreferences
                SharedPreferences sharedPreferences = preference.getSharedPreferences();
                int savedHour = sharedPreferences.getInt("sms_time_hour", 6); // Standard er 6
                int savedMinute = sharedPreferences.getInt("sms_time_minute", 0); // Standard er 0

                // Oppretter og viser en TimePickerDialog med den lagrede tiden
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getContext(),
                        (view, hourOfDay, minute) -> {

                            // Håndterer valgt tid og lagrer den i SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("sms_time_hour", hourOfDay);
                            editor.putInt("sms_time_minute", minute);
                            editor.apply();

                            // Utløser en kringkasting etter at SharedPreferences er oppdatert
                            Intent intent = new Intent("com.example.s199219.SETTING_CHANGED");
                            getActivity().sendBroadcast(intent);

                        },
                        savedHour,
                        savedMinute,
                        true // Bruker 24-timers format
                );
                timePickerDialog.show();
                return true;
            });
        }
    }
}