package com.example.s199219;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Dette er en abstrakt klasse som utvider RoomDatabase
// Denne klassen representerer databasen vår
@Database(entities = {Friend.class, Appointment.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    // Bruker ExecutorService for bakgrunnsoperasjoner
    private static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    // Callback brukes til å fylle databasen med eksempeldata når den opprettes
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                FriendDao friendDao = instance.friendDao();
                friendDao.insert(new Friend("John Doe", "123456789"));
                friendDao.insert(new Friend("Jane Doe", "987654321"));
            });
        }
    };

    // Denne metoden kjøres når databasen opprettes
    public abstract FriendDao friendDao();
    public abstract AppointmentDao appointmentDao();

    // Metoden getInstance() sørger for at det kun opprettes én instans av databasen
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    // Husk å lukke ExecutorService når den ikke lenger er nødvendig
    public static void shutDownExecutorService() {
        databaseWriteExecutor.shutdown();
    }
}
