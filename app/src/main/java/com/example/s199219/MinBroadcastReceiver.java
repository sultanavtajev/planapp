package com.example.s199219;

// Importerer nødvendige biblioteker og pakker
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

// Definerer en BroadcastReceiver-klasse kalt MinBroadcastReceiver
public class MinBroadcastReceiver extends BroadcastReceiver {

    // Metode som blir kalt når en kringkasting mottas
    @Override
    public void onReceive(Context context, Intent intent) {

        // Henter handlingen fra den mottatte intenten
        String action = intent.getAction();
        if (action != null) {
            // Sjekker hvilken handling som er mottatt
            switch (action) {
                // Hvis enheten har fullført oppstart eller innstillingene har endret seg
                case Intent.ACTION_BOOT_COMPLETED:
                case "com.example.s199219.SETTING_CHANGED":
                    // Starter MinPeriodiskService som en forgrunnstjeneste
                    Intent serviceIntent = new Intent(context, MinPeriodiskService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                    break;
            }
        }
    }
}

