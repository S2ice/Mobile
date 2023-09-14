package com.example.planningmeeting;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private TextView textViewDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Set the minimum date to today's date
        long minDate = c.getTimeInMillis();

        // Set the maximum date to the last day of the current year
        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.set(Calendar.YEAR, year);
        maxDateCalendar.set(Calendar.MONTH, 11); // December
        maxDateCalendar.set(Calendar.DAY_OF_MONTH, 31);
        long maxDate = maxDateCalendar.getTimeInMillis();

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.getDatePicker().setMaxDate(maxDate);

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Update the corresponding TextView with the selected date
        textViewDate.setText(day + "/" + (month + 1) + "/" + year);
    }

    public void setTextViewDate(TextView textViewDate) {
        this.textViewDate = textViewDate;
    }
}
