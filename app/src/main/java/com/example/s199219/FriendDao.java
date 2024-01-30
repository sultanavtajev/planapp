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
public interface FriendDao {

    // Metode for å sette inn en ny venn i databasen
    @Insert
    void insert(Friend friend);

    // Metode for å oppdatere en eksisterende venn i databasen
    @Update
    void update(Friend friend);

    // Metode for å slette en venn fra databasen
    @Delete
    void delete(Friend friend);

    // Metode for å hente alle venner fra databasen
    @Query("SELECT * FROM friend_table")
    List<Friend> getAllFriends();

    // Metode for å hente alle venner fra databasen som LiveData
    @Query("SELECT * FROM friend_table")
    LiveData<List<Friend>> getAllFriendsLive();

    // Metode for å hente en spesifikk venn basert på ID
    @Query("SELECT * FROM friend_table WHERE id = :friendId")
    Friend getFriendById(int friendId);

    // Metode for å finne venner basert på navnesøk
    @Query("SELECT * FROM friend_table WHERE name LIKE :search")
    List<Friend> findFriendsByName(String search);

    // Metode for å slette alle venner fra databasen
    @Query("DELETE FROM friend_table")
    void deleteAllFriends();
}
