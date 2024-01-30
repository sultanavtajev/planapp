package com.example.s199219;

// Importer nødvendige pakker og klasser
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

// Hovedklasse for adapteren som håndterer venneliste
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    // Liste for å lagre vennene
    private List<Friend> friends;

    // Konstruktør for adapteren
    public FriendAdapter(List<Friend> friends) {
        this.friends = friends;
    }

    // Indre klasse for ViewHolder
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView phoneNumberTextView;
        public FloatingActionButton editButton;
        public FloatingActionButton deleteButton;

        // ViewHolder-konstruktør
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Metode for å opprette ViewHolder
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    // Metode for å binde data til ViewHolder
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend currentFriend = friends.get(position);
        holder.nameTextView.setText(currentFriend.getName());
        holder.phoneNumberTextView.setText(currentFriend.getPhoneNumber());

        // Kode for redigeringsknappen
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendDialogFragment friendDialogFragment = new FriendDialogFragment();
                friendDialogFragment.setEditMode(currentFriend);

                AppCompatActivity activity = getActivityFromContext(v.getContext());
                if (activity != null) {
                    friendDialogFragment.show(activity.getSupportFragmentManager(), "editFriend");
                }
            }
        });

        // Kode for sletteknappen
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                Friend currentFriend = friends.get(currentPosition);

                // Opprette en AlertDialog for å bekrefte sletting
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Slett venn")
                        .setMessage("Er du sikker på at du vil slette denne vennen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Slett vennen fra databasen i en separat tråd
                                new Thread(() -> {
                                    AppDatabase db = AppDatabase.getInstance(v.getContext());
                                    db.friendDao().delete(currentFriend);
                                    // Kjør UI-oppdateringer på hovedtråden
                                    AppCompatActivity activity = getActivityFromContext(v.getContext());
                                    if (activity != null) {
                                        activity.runOnUiThread(() -> {
                                            // Fjern vennen fra listen og oppdater RecyclerView
                                            friends.remove(currentPosition);
                                            notifyDataSetChanged();
                                        });
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Nei", null)
                        .show();
            }
        });
    }

    // Metode for å hente antall elementer i listen
    @Override
    public int getItemCount() {
        return friends.size();
    }

    // Metode for å oppdatere vennelisten
    public void setFriends(List<Friend> newFriends) {
        this.friends = newFriends;
        notifyDataSetChanged();
    }

    // Hjelpemetode for å hente AppCompatActivity fra en gitt kontekst
    public static AppCompatActivity getActivityFromContext(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
