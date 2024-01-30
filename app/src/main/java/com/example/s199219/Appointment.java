package com.example.s199219;

// Importerer Entity-annotasjon for Room-database
import androidx.room.Entity;

// Importerer PrimaryKey-annotasjon for å definere primærnøkkel i tabellen
import androidx.room.PrimaryKey;

/** @noinspection SpellCheckingInspection */

// Definerer tabellnavnet for denne entiteten
@Entity(tableName = "appointment_table")

// Entitetsklasse for "appointment" i databasen
public class Appointment {

    // Angir at id er primærnøkkel og skal autogenereres
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int friendId;

    private String friendName;
    private String date;
    private String time;
    private String location;
    private String note;
    private Boolean reminder;

    public Appointment(int friendId, String friendName, String date, String time, String location, String note, Boolean reminder) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.date = date;
        this.time = time;
        this.location = location;
        this.note = note;
        this.reminder = reminder;
    }

    // Getters and Setters for alle felt
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getReminder() {
        return reminder;
    }

    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }
}