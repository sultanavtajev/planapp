package com.example.s199219;

// Importerer de nødvendige bibliotekene
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MultiStepDialogFragment extends DialogFragment {
    // Initialiserer variabler for å holde styr på nåværende trinn i dialogen og totalt antall trinn
    private int currentStep = 1;
    private int totalSteps = 6;

    // Variabler for å håndtere redigeringsmodus og eksisterende avtale
    private boolean isEditMode = false;
    private Appointment existingAppointment;

    // Layout-variabler for hvert trinn i multi-step dialogen
    private LinearLayout step1Layout, step2Layout, step3Layout, step4Layout, step5Layout, step6Layout;

    // Knapper for navigasjon mellom trinnene
    private FloatingActionButton nextButton, backButton;

    // Spinner og adapter for å velge en venn
    private Spinner friendSpinner;
    private ArrayAdapter<Friend> friendAdapter;

    // ViewModel for vennene
    private FriendViewModel friendViewModel;

    // Liste for å holde vennene som skal vises i spinneren
    List<Friend> friendList = new ArrayList<>();

    public MultiStepDialogFragment() {
        // Tom konstruktør
    }

    // Metode for å sette dialogen i redigeringsmodus
    public void setEditMode(Appointment existingAppointment) {
        isEditMode = true;
        this.existingAppointment = existingAppointment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Bygger dialogen
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.multi_step_dialog, null);

        // Initialiserer ViewModel for avtaler
        AppointmentViewModel appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        // Initialiserer ViewModel for venner
        friendViewModel = new ViewModelProvider(requireActivity()).get(FriendViewModel.class);

        // Kobler layout-elementene til variablene
        step1Layout = view.findViewById(R.id.step1Layout);
        step2Layout = view.findViewById(R.id.step2Layout);
        step3Layout = view.findViewById(R.id.step3Layout);
        step4Layout = view.findViewById(R.id.step4Layout);
        step5Layout = view.findViewById(R.id.step5Layout);
        step6Layout = view.findViewById(R.id.step6Layout);
        nextButton = view.findViewById(R.id.nextButton);
        backButton = view.findViewById(R.id.backButton);

        // Viser det nåværende trinnet i dialogen
        showStep(currentStep);

        // Initialiserer spinner og dets adapter
        friendSpinner = view.findViewById(R.id.friendSpinner);
        friendAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, friendList);
        friendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        friendSpinner.setAdapter(friendAdapter);

        // Laster venner fra databasen
        loadFriendsFromDatabase();

        if (isEditMode && existingAppointment != null) {
            // Hvis vi er i redigeringsmodus og har en eksisterende avtale å jobbe med

            // Setter DatePicker til den eksisterende datoen for avtalen
            String[] existingDateParts = existingAppointment.getDate().split("-");
            DatePicker datePicker = view.findViewById(R.id.datePicker);
            datePicker.updateDate(
                    Integer.parseInt(existingDateParts[0]),  // År
                    Integer.parseInt(existingDateParts[1]) - 1,  // Måned (0-basert)
                    Integer.parseInt(existingDateParts[2])  // Dag
            );

            // Setter TimePicker til den eksisterende tiden for avtalen
            String[] existingTimeParts = existingAppointment.getTime().split(":");
            TimePicker timePicker = view.findViewById(R.id.timePicker);
            timePicker.setCurrentHour(Integer.parseInt(existingTimeParts[0]));  // Time
            timePicker.setCurrentMinute(Integer.parseInt(existingTimeParts[1]));  // Minutt

            // Setter stedet (location) i EditText-feltet til den eksisterende lokasjonen for avtalen
            EditText locationEditText = view.findViewById(R.id.locationEditText);
            locationEditText.setText(existingAppointment.getLocation());

            // Setter notatet (note) i EditText-feltet til det eksisterende notatet for avtalen
            EditText noteEditText = view.findViewById(R.id.noteEditText);
            noteEditText.setText(existingAppointment.getNote());

            // Setter påminnelsesstatus i CheckBox til den eksisterende påminnelsesstatusen for avtalen
            CheckBox reminderCheckBox = view.findViewById(R.id.reminderCheckBox);
            reminderCheckBox.setChecked(existingAppointment.getReminder());
        }

        // Set onClickListeners for 'Next' knapp
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hvis vi ikke er på det siste steget, naviger til neste steg
                if (currentStep < totalSteps) {
                    currentStep++;
                    showStep(currentStep);
                    // Hvis vi nå er på det siste steget, endre ikonet til 'Save'
                    if (currentStep == totalSteps) {
                        nextButton.setImageResource(R.drawable.save);
                    }
                } else {
                    // Vi er på det siste steget, samle all data og lagre avtalen

                    // Henter data fra brukergrensesnittet
                    Friend selectedFriend = (Friend) friendSpinner.getSelectedItem();
                    int selectedFriendId = selectedFriend.getId();
                    String selectedFriendName = selectedFriend.getName();

                    DatePicker datePicker = view.findViewById(R.id.datePicker);
                    String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();

                    TimePicker timePicker = view.findViewById(R.id.timePicker);
                    String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

                    EditText locationEditText = view.findViewById(R.id.locationEditText);
                    String location = locationEditText.getText().toString();

                    EditText noteEditText = view.findViewById(R.id.noteEditText);
                    String note = noteEditText.getText().toString();

                    CheckBox reminderCheckBox = view.findViewById(R.id.reminderCheckBox);
                    boolean reminder = reminderCheckBox.isChecked();

                    // Oppretter et Appointment-objekt og lagrer det til databasen
                    if (isEditMode && existingAppointment != null) {
                        // Vi er i redigeringsmodus, så oppdater den eksisterende avtalen
                        existingAppointment.setFriendId(selectedFriendId);
                        existingAppointment.setFriendName(selectedFriendName);
                        existingAppointment.setDate(date);
                        existingAppointment.setTime(time);
                        existingAppointment.setLocation(location);
                        existingAppointment.setNote(note);
                        existingAppointment.setReminder(reminder);

                        // Oppdaterer eksisterende avtale i databasen
                        appointmentViewModel.update(existingAppointment);
                    } else {
                        // Oppretter en ny avtale og lagrer den i databasen
                        Appointment newAppointment = new Appointment(selectedFriendId, selectedFriendName, date, time, location, note, reminder);
                        appointmentViewModel.insert(newAppointment);
                    }

                    // Lukker dialogboksen
                    dismiss();
                }
            }
        });

        // Sett onClickListeners for 'Tilbake'-knappen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviger til forrige trinn hvis vi ikke er på det første trinnet
                if (currentStep > 1) {
                    currentStep--;
                    showStep(currentStep);
                    // Endre ikonet til 'neste' hvis vi ikke er på siste trinn
                    if (currentStep < totalSteps) {
                        nextButton.setImageResource(R.drawable.next);
                    }
                }
            }
        });

        // Angi visningen for dialogen
        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Angi en egendefinert bakgrunn for dialogboksen
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);

        return dialog;
    }

    // Kontrollerer hvilket trinn som skal vises i dialogen basert på verdien av step
    private void showStep(int step) {
        // Gjem alle trinnene først
        step1Layout.setVisibility(View.GONE);
        step2Layout.setVisibility(View.GONE);
        step3Layout.setVisibility(View.GONE);
        step4Layout.setVisibility(View.GONE);
        step5Layout.setVisibility(View.GONE);
        step6Layout.setVisibility(View.GONE);

        // Vis det nåværende trinnet basert på verdien av 'step'
        switch (step) {
            case 1:
                step1Layout.setVisibility(View.VISIBLE);
                break;
            case 2:
                step2Layout.setVisibility(View.VISIBLE);
                break;
            case 3:
                step3Layout.setVisibility(View.VISIBLE);
                break;
            case 4:
                step4Layout.setVisibility(View.VISIBLE);
                break;
            case 5:
                step5Layout.setVisibility(View.VISIBLE);
                break;
            case 6:
                step6Layout.setVisibility(View.VISIBLE);
                break;
        }
    }

    //  Laster vennelisten fra databasen og oppdaterer spinneren (rullegardinlisten) med de nye verdiene
    private void loadFriendsFromDatabase() {
        friendViewModel.getFriends().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
                // Oppdater friendList og gi beskjed til adapteren
                friendList.clear();
                friendList.addAll(friends);
                friendAdapter.notifyDataSetChanged();
                if (existingAppointment != null) {
                    updateFriendSpinner();
                }
            }
        });
    }

    // Finner posisjonen til en eksisterende venn i spinneren og setter den som valgt hvis den finnes
    private void updateFriendSpinner() {
        // Finn posisjonen til den eksisterende vennen i spinneren og sett den som valgt
        String existingFriendName = existingAppointment.getFriendName();
        int spinnerPosition = -1;
        for (int i = 0; i < friendList.size(); i++) {
            if (friendList.get(i).getName().equals(existingFriendName)) {
                spinnerPosition = i;
                break;
            }
        }
        if (spinnerPosition != -1) {
            friendSpinner.setSelection(spinnerPosition);
        }
    }

}
