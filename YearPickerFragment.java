package com.example.planningmeeting;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.shawnlin.numberpicker.NumberPicker;
import java.util.Calendar;

public class YearPickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of NumberPickerDialog and return it
        NumberPicker numberPicker = new NumberPicker(getActivity());
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        numberPicker.setMinValue(currentYear);
        numberPicker.setMaxValue(currentYear + 10);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Select Year")
                .setView(numberPicker)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedYear = numberPicker.getValue();
                    // Update the corresponding TextView with the selected year
                    TextView textViewYear = getActivity().findViewById(R.id.textViewYear);
                    textViewYear.setText(String.valueOf(selectedYear));
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}
