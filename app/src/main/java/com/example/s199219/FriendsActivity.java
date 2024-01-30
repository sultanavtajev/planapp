package com.example.s199219;

// Importerer nødvendige klasser og pakker
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// Definerer en aktivitet for vennelisten
public class FriendsActivity extends AppCompatActivity {

    // Deklarerer variabler for ViewModel, RecyclerView og Adapter
    private FriendViewModel friendViewModel;
    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;

    // Metoden som kjøres når aktiviteten opprettes
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        // Initialiserer FriendViewModel
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        // Initialiserer RecyclerView
        recyclerView = findViewById(R.id.friendRecyclerView);

        // Initialiserer og setter opp FriendAdapter
        friendAdapter = new FriendAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(friendAdapter);

        // Observerer endringer i vennelisten og oppdaterer RecyclerView
        friendViewModel.getFriends().observe(this, friends -> {
            friendAdapter.setFriends(friends);
        });

        // Initialiserer Floating Action Button for å legge til ny venn
        FloatingActionButton addFriendFAB = findViewById(R.id.addFriendFAB);
        addFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Åpner dialog for å legge til eller redigere en venn
                FriendDialogFragment dialogFragment = new FriendDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "friendDialog");
            }
        });

        // Initialiserer tilbakeknapp for å gå tilbake til avtaleoversikten
        FloatingActionButton backToAppointmentsButton = findViewById(R.id.backToAppointmentsButton);
        backToAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigerer tilbake til MainActivity
                Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}