package com.example.s199219;

// Importerer LiveData for dataobservasjon
import androidx.lifecycle.LiveData;
// Importerer Dao for å definere dataaksessobjektet
import androidx.room.Dao;
// Importerer de forskjellige Room-operasjonene
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

// Importerer Java's List for å håndtere lister
import java.util.List;

// Annotasjon for å indikere at dette er et Room Dao
@Dao
public interface AppointmentDao {

    // Metode for å sette inn en ny avtale i databasen
    @Insert
    void insert(Appointment appointment);

    // Metode for å oppdatere en eksisterende avtale i databasen
    @Update
    void update(Appointment appointment);

    // Metode for å slette en avtale fra databasen
    @Delete
    void delete(Appointment appointment);

    // Metode for å hente alle avtaler fra databasen
    @Query("SELECT * FROM appointment_table")
    LiveData<List<Appointment>> getAllAppointments();

    // Metode for å hente en spesifikk avtale basert på ID
    @Query("SELECT * FROM appointment_table WHERE id = :appointmentId")
    Appointment getAppointmentById(int appointmentId);

    // Metode for å hente avtaler basert på en spesifikk dato
    @Query("SELECT * FROM appointment_table WHERE date = :date")
    List<Appointment> getAppointmentsByDate(String date);

    // Metode for å slette alle avtaler fra databasen
    @Query("DELETE FROM appointment_table")
    void deleteAllAppointments();

    // Metode for å hente kommende avtaler basert på dagens dato
    @Query("SELECT * FROM appointment_table WHERE date >= :currentDate")
    List<Appointment> getUpcomingAppointments(String currentDate);

    // Metode for å hente avtaler basert på en venns ID
    @Query("SELECT * FROM appointment_table WHERE friendId = :friendId")
    List<Appointment> getAppointmentsByFriendId(int friendId);
}