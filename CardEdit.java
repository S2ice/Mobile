package com.example.planningmeeting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CardEdit extends AppCompatActivity {
    private TextView textViewDate, textViewDescription, textViewAttendees, textViewTime;
    private Button Prinat, Otmena;

    private DatabaseReference meetingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardedit);

        // Находим TextView и Button по их идентификаторам
        textViewDate = findViewById(R.id.textViewDate);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewAttendees = findViewById(R.id.textViewAttendees);
        textViewTime = findViewById(R.id.textViewTime);

        Prinat = findViewById(R.id.Prinat);
        Otmena = findViewById(R.id.Otmena);

        // Получаем ссылку на Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        meetingsRef = database.getReference("users").child(userId).child("meetings");


        Prinat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMeeting();
            }
        });
    }

    private void saveMeeting() {
        // Получаем введенные значения из TextView
        String description = textViewDescription.getText().toString().trim();
        String date = textViewDate.getText().toString().trim();
        String attendees = textViewAttendees.getText().toString().trim();
        String time = textViewTime.getText().toString().trim();

        // Проверяем, если значения не изменились, заменяем их на "NW"
        if (description.equals("Description") || date.equals("Date") || attendees.equals("Attendees") || time.equals("Set Time")) {
            description = "textViewDescription";
            date = "Select Date";
            attendees = "textViewAttendees";
            time = "textViewTime";
        }

        if (description.isEmpty() || date.isEmpty() || attendees.isEmpty() || time.isEmpty()) {
            // Выводим сообщение об ошибке, если хотя бы одно поле пустое
            Toast.makeText(this, "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
        } else {
            // Генерируем уникальный ключ для встречи
            String meetingId = meetingsRef.push().getKey();

            // Создаем новый объект Meeting
            Meeting meeting = new Meeting(date, description, attendees, time);
            // Сохраняем встречу в Firebase Realtime Database
            meetingsRef.child(meetingId).setValue(meeting);

            // Выводим сообщение об успешном сохранении
            Toast.makeText(this, "Встреча успешно сохранена", Toast.LENGTH_SHORT).show();

            // Завершаем активити
            finish();
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTextViewDate(textViewDate); // Установка TextView перед отображением DatePickerFragment
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");


    }
    public void showTimePickerDialog(View view) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setTextViewTime(textViewTime); // Replace textViewTime with your actual TextView
        timePickerFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void openDescriptionDialog(View view) {
        DialogFragment descriptionDialogFragment = new DescriptionPickerFragment();
        descriptionDialogFragment.show(getSupportFragmentManager(), "descriptionDialog");
    }

    public void openAttendeesDialog(View view) {
        DialogFragment attendeesDialogFragment = new AttendeesPickerFragment();
        attendeesDialogFragment.show(getSupportFragmentManager(), "attendeesDialog");
    }

}


