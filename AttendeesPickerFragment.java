package com.example.planningmeeting;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AttendeesPickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of AlertDialog and return it
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_attendees, null);
        EditText editTextAttendees = dialogView.findViewById(R.id.editTextAttendees);
        builder.setView(dialogView)
                .setTitle("Enter Attendees")
                .setPositiveButton("OK", (dialog, which) -> {
                    String attendees = editTextAttendees.getText().toString().trim();
                    // Update the corresponding TextView with the selected attendees
                    TextView textViewAttendees = getActivity().findViewById(R.id.textViewAttendees);
                    textViewAttendees.setText(attendees);
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }
}
