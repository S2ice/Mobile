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

import com.example.planningmeeting.R;

public class DescriptionPickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of AlertDialog and return it
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_description, null);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        builder.setView(dialogView)
                .setTitle("Enter Description")
                .setPositiveButton("OK", (dialog, which) -> {
                    String description = editTextDescription.getText().toString().trim();
                    // Update the corresponding TextView with the selected description
                    TextView textViewDescription = getActivity().findViewById(R.id.textViewDescription);
                    textViewDescription.setText(description);
                })
                .setNegativeButton("Cancel", null);
        return builder.create();
    }
}
