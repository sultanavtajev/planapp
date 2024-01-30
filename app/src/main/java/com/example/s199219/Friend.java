package com.example.s199219;

// Importerer Entity-annotasjon for Room-database
import androidx.room.Entity;
// Importerer PrimaryKey-annotasjon for å definere primærnøkkel i tabellen
import androidx.room.PrimaryKey;

// Annotasjon for å indikere at denne klassen er en entitet i Room-databasen
@Entity(tableName = "friend_table")
public class Friend {

    // Annotasjon for å indikere at id er en primærnøkkel og skal genereres automatisk
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Felt for vennens navn
    private String name;
    // Felt for vennens telefonnummer
    private String phoneNumber;

    // Konstruktør som tar navn og telefonnummer som argumenter
    public Friend(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // Metode for å returnere en strengrepresentasjon av objektet
    @Override
    public String toString() {
        return name;  // Du kan også inkludere flere felter hvis du vil
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Setter for id
    public void setId(int id) {
        this.id = id;
    }

    // Getter for navn
    public String getName() {
        return name;
    }

    // Setter for navn
    public void setName(String name) {
        this.name = name;
    }

    // Getter for telefonnummer
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setter for telefonnummer
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}