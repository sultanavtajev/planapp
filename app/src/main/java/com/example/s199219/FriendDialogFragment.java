package com.example.s199219;

// Importer nødvendige biblioteker
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Definerer en DialogFragment-klasse for å håndtere venneinformasjon
public class FriendDialogFragment extends DialogFragment {

    // Variabler for å holde styr på stegene i dialogen
    private int currentStep = 1;
    private int totalSteps = 2;
    // Variabler for redigeringsmodus
    private boolean isEditMode = false;
    private Friend existingFriend;

    // Layout-variabler
    private LinearLayout step1FriendLayout, step2FriendLayout;
    private FloatingActionButton friendNextButton, friendBackButton;

    // Tom konstruktør som kreves for fragmenter
    public FriendDialogFragment() {
        // Tom konstruktør
    }

    // Metode for å sette dialogen i redigeringsmodus
    public void setEditMode(Friend existingFriend) {
        isEditMode = true;
        this.existingFriend = existingFriend;
    }

    // Metode som kjøres når dialogen opprettes
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflater layouten for dialogen
        View view = inflater.inflate(R.layout.multi_step_dialog_friend, null);
        // Oppretter ViewModel for Friend
        FriendViewModel friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        // Initierer layoutelementer
        step1FriendLayout = view.findViewById(R.id.step1FriendLayout);
        step2FriendLayout = view.findViewById(R.id.step2FriendLayout);
        friendNextButton = view.findViewById(R.id.friendNextButton);
        friendBackButton = view.findViewById(R.id.friendBackButton);

        // Viser riktig steg i dialogen
        showStep(currentStep);

        // Fyller inn eksisterende vennedata hvis i redigeringsmodus
        if (isEditMode && existingFriend != null) {
            ((EditText) view.findViewById(R.id.friendNameEditText)).setText(existingFriend.getName());
            ((EditText) view.findViewById(R.id.friendPhoneEditText)).setText(existingFriend.getPhoneNumber());
        }

        // Håndterer klikk på "Neste"-knappen
        friendNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < totalSteps) {
                    currentStep++;
                    showStep(currentStep);
                    if (currentStep == totalSteps) {
                        friendNextButton.setImageResource(R.drawable.save);
                    }
                } else {
                        String name = ((EditText) view.findViewById(R.id.friendNameEditText)).getText().toString();
                        String phone = ((EditText) view.findViewById(R.id.friendPhoneEditText)).getText().toString();

                        if (isEditMode && existingFriend != null) {
                            // Oppdater eksisterende venn i databasen
                            existingFriend.setName(name);
                            existingFriend.setPhoneNumber(phone);
                            friendViewModel.update(existingFriend); // Antatt at en slik metode finnes
                        } else {
                            Friend newFriend = new Friend(name, phone);
                            friendViewModel.insert(newFriend);
                        }
                        dismiss();
                    }
                }
        });

        // Håndterer klikk på "Tilbake"-knappen
        friendBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep > 1) {
                    currentStep--;
                    showStep(currentStep);
                    if (currentStep < totalSteps) {
                        friendNextButton.setImageResource(R.drawable.next);
                    }
                }
            }
        });

        // Setter visningen for dialogen og oppretter den
        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Setter en tilpasset bakgrunn for dialogen
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);

        return dialog;
    }

    // Metode for å vise riktig steg i dialogen
    private void showStep(int step) {
        // Gjemmer alle steg
        step1FriendLayout.setVisibility(View.GONE);
        step2FriendLayout.setVisibility(View.GONE);

        // Viser det aktuelle steget
        switch (step) {
            case 1:
                step1FriendLayout.setVisibility(View.VISIBLE);
                break;
            case 2:
                step2FriendLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

}

