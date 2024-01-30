package com.example.s199219;

// Importerer nødvendige biblioteker og pakker
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

// Definerer en ViewModel-klasse for Friend-objekter
public class FriendViewModel extends AndroidViewModel {

    // Deklarerer LiveData-objekt for å holde en liste med Friend-objekter
    private final LiveData<List<Friend>> friends;
    // Deklarerer en FriendDao for å utføre databasenoperasjoner
    private final FriendDao friendDao;

    // Konstruktør for FriendViewModel
    public FriendViewModel(Application application) {
        super(application);
        // Henter databaseninstans
        AppDatabase db = AppDatabase.getInstance(application);
        // Initialiserer LiveData-objektet med data fra databasen
        friends = db.friendDao().getAllFriendsLive();
        // Initialiserer FriendDao-objektet
        friendDao = db.friendDao();
    }

    // Metode for å legge til en ny venn i databasen
    public void insert(Friend friend) {
        // Utfører databasenoperasjon i en ny tråd
        new Thread(() -> friendDao.insert(friend)).start();
    }

    // Metode for å hente LiveData-objektet som holder vennelisten
    public LiveData<List<Friend>> getFriends() {
        return friends;
    }

    // Metode for å oppdatere en eksisterende venn i databasen
    public void update(Friend friend) {
        // Utfører databasenoperasjon i en ny tråd
        new Thread(() -> friendDao.update(friend)).start();
    }
}

