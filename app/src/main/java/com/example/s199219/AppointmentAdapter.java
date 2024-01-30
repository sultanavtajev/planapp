// Pakkenavn for Android-prosjektet
package com.example.s199219;

// Importerer Context-klasse for å få tilgang til applikasjonens kontekst

import android.content.Context;
// Importerer ContextWrapper for å pakke inn den opprinnelige konteksten
import android.content.ContextWrapper;
// Importerer DialogInterface for å håndtere dialoger
import android.content.DialogInterface;
// Importerer LayoutInflater for å generere layout fra XML-filer
import android.view.LayoutInflater;
// Importerer View for å håndtere UI-elementer
import android.view.View;
// Importerer ViewGroup for å håndtere visninger som inneholder andre visninger
import android.view.ViewGroup;
// Importerer TextView for å håndtere tekstvisning
import android.widget.TextView;

// Importerer NonNull-annotasjon for å indikere at en parameter, felt eller metodereturverdi ikke kan være null
import androidx.annotation.NonNull;
// Importerer AlertDialog for å vise varsler
import androidx.appcompat.app.AlertDialog;
// Importerer AppCompatActivity som grunnklasse for aktiviteter som bruker Support Library-handlingslinjen
import androidx.appcompat.app.AppCompatActivity;
// Importerer RecyclerView for å lage lister eller rutenett av elementer
import androidx.recyclerview.widget.RecyclerView;

// Importerer FloatingActionButton for å vise et flytende handlingsknapp
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Importerer List for å håndtere lister av objekter
import java.util.List;

/** @noinspection deprecation, SpellCheckingInspection */

// Definerer en Adapter-klasse for RecyclerView som håndterer avtaleobjekter
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    // Liste over avtaleobjekter som skal vises i RecyclerView
    private List<Appointment> appointments;

    // Konstruktør for AppointmentAdapter som tar en liste av avtaler
    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    // Indre klasse for å definere en ViewHolder for en enkelt avtale
    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        // Definerer TextViews for å vise ulike felter av en avtale
        public TextView friendNameTextView;
        public TextView dateTextView;
        public TextView timeTextView;
        public TextView locationTextView;
        public TextView noteTextView;
        public TextView reminderTextView;

        // Definerer FloatingActionButtons for redigering og sletting
        public FloatingActionButton editButton;
        public FloatingActionButton deleteButton;

        // Konstruktør for ViewHolder, initialiserer visningene
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Binder TextViews og Buttons til deres respektive IDer i layouten
            friendNameTextView = itemView.findViewById(R.id.friendNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            noteTextView = itemView.findViewById(R.id.noteTextView);
            reminderTextView = itemView.findViewById(R.id.reminderTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    // Metoden som binder data til de ulike visningene i ViewHolder
    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {

        // Henter den nåværende avtalen basert på posisjon i listen
        Appointment currentAppointment = appointments.get(position);

        // Setter teksten for de ulike TextViews i ViewHolder
        holder.friendNameTextView.setText("Navn: " + currentAppointment.getFriendName());
        holder.dateTextView.setText("Dato: " + currentAppointment.getDate());
        holder.timeTextView.setText("Tid: " + currentAppointment.getTime());
        holder.locationTextView.setText("Sted: " + currentAppointment.getLocation());
        holder.noteTextView.setText("Notat: " + currentAppointment.getNote());
        holder.reminderTextView.setText("Påminnelse: " + (currentAppointment.getReminder() ? "Ja" : "Nei"));

        // Setter OnClickListener for redigeringsknappen
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Oppretter en ny instans av MultiStepDialogFragment for å redigere avtalen
                MultiStepDialogFragment multiStepDialogFragment = new MultiStepDialogFragment();
                multiStepDialogFragment.setEditMode(currentAppointment);

                // Henter aktiviteten som inneholder denne visningen
                AppCompatActivity activity = getActivityFromContext(v.getContext());
                if (activity != null) {

                    // Viser dialogfragmentet for å redigere avtalen
                    multiStepDialogFragment.show(activity.getSupportFragmentManager(), "editFriend");
                }
            }
        });

        // Setter OnClickListener for sletteknappen
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Henter den nåværende posisjonen i listen og tilhørende avtale
                int currentPosition = holder.getAdapterPosition();
                Appointment currentAppointment = appointments.get(currentPosition);

                // Oppretter en AlertDialog for å bekrefte sletting
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Slett avtale")
                        .setMessage("Er du sikker på at du vil slette denne avtalen?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // Sletter vennen fra databasen i en separat tråd
                                new Thread(() -> {
                                    AppDatabase db = AppDatabase.getInstance(v.getContext());
                                    db.appointmentDao().delete(currentAppointment);

                                    // Kjører UI-oppdateringer på hovedtråden
                                    AppCompatActivity activity = getActivityFromContext(v.getContext());
                                    if (activity != null) {
                                        activity.runOnUiThread(() -> {

                                            // Fjerner vennen fra listen og oppdaterer RecyclerView
                                            appointments.remove(currentPosition);
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

    // Returnerer antall elementer i listen
    @Override
    public int getItemCount() {
        return appointments.size();
    }

    // Metode for å oppdatere listen med nye avtaler
    public void setAppointments(List<Appointment> newAppointments) {
        this.appointments = newAppointments;
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