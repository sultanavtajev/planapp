package com.example.s199219;

// Importerer Application for å få tilgang til applikasjonens kontekst
import android.app.Application;
// Importerer Log for logging av informasjon
import android.util.Log;

// Importerer AndroidViewModel for ViewModel med applikasjonskontekst
import androidx.lifecycle.AndroidViewModel;
// Importerer LifecycleOwner for å håndtere livssyklus-observasjon
import androidx.lifecycle.LifecycleOwner;
// Importerer LiveData for dataobservasjon
import androidx.lifecycle.LiveData;
// Importerer Observer for å observere LiveData-objekter
import androidx.lifecycle.Observer;

// Importerer Java's List for å håndtere lister
import java.util.List;

// Klasse som fungerer som ViewModel for avtaler
public class AppointmentViewModel extends AndroidViewModel {

    // LiveData-objekt for å observere listen av avtaler
    private final LiveData<List<Appointment>> appointments;
    // Dao-objekt for å utføre databasemanipulasjoner
    private final AppointmentDao appointmentDao;

    // Konstruktør som initialiserer databasen og Dao
    public AppointmentViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        appointments = db.appointmentDao().getAllAppointments();
        appointmentDao = db.appointmentDao();
    }

    // Metode for å sette inn en ny avtale i databasen
    public void insert(Appointment appointment) {
        new Thread(() -> appointmentDao.insert(appointment)).start();
    }

    // Metode for å hente LiveData-objektet for avtaler
    public LiveData<List<Appointment>> getAppointments() {
        return appointments;
    }

    // Metode for å oppdatere en eksisterende avtale i databasen
    public void update(Appointment appointment) {
        new Thread(() -> appointmentDao.update(appointment)).start();
    }

    // Metode for å logge innholdet i databasen
    public void logDatabaseContents(final LifecycleOwner owner) {
        Observer<List<Appointment>> dbObserver = new Observer<List<Appointment>>() {
            @Override
            public void onChanged(List<Appointment> appointments) {
                for (Appointment appointment : appointments) {
                    Log.d("DatabaseContent", appointment.toString());
                }
                // VIKTIG: Fjerner observatøren for å forhindre flere logger
                getAppointments().removeObserver(this);
            }
        };
        getAppointments().observe(owner, dbObserver);
    }
}
