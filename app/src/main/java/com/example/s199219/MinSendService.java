package com.example.s199219;

// Importerer de nødvendige bibliotekene
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import java.time.LocalDate;
import java.util.List;

public class MinSendService extends Service {
    // Antatt at du har en måte å få FriendDao og AppointmentDao
    private FriendDao friendDao;
    private AppointmentDao appointmentDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Henter databasens instanse
        AppDatabase database = AppDatabase.getInstance(this);
        // Initialiserer FriendDao
        friendDao = database.friendDao();
        // Initialiserer AppointmentDao
        appointmentDao = database.appointmentDao();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            // Henter delte preferanser for appen
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean isSMSEnabled = sharedPreferences.getBoolean("sms_service", false);
            String smsTime = sharedPreferences.getString("sms_time", "06:00");
            String defaultSMSMessage = sharedPreferences.getString("default_sms_message", "Påminnelse: Du har en avtale");

            // Henter dagens dato i "YYYY-MM-DD" format
            String currentDate = LocalDate.now().toString();

            // Henter kommende avtaler
            List<Appointment> appointments = appointmentDao.getUpcomingAppointments(currentDate);

            // Sjekker om SMS er aktivert og om det er noen kommende avtaler
            if (isSMSEnabled && appointments != null && !appointments.isEmpty()) {
                for (Appointment appointment : appointments) {
                    int friendId = appointment.getFriendId();
                    Friend friend = friendDao.getFriendById(friendId);
                    String phoneNumber = friend.getPhoneNumber();
                    String message = defaultSMSMessage;

                    // Sender SMS
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                    // Viser en notifikasjon
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    // Oppretter en notifikasjonskanal for Android Oreo og nyere versjoner
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("default", "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    // Bygger notifikasjonen
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle("Avtale Påminnelse")
                            .setContentText(message);

                    // Viser notifikasjonen
                    notificationManager.notify(0, builder.build());
                }
            }
        }).start();

        // Returnerer START_NOT_STICKY for å indikere at tjenesten ikke trenger å starte på nytt hvis den blir stoppet av systemet
        return START_NOT_STICKY;
    }
}
