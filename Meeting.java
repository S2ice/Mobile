package com.example.planningmeeting;

public class Meeting {
    private String date;
    private String description;
    private String attendees;
    private String time;

    public Meeting() {
        // Конструктор без аргументов
    }

    public Meeting(String date, String description, String attendees, String time) {
        this.date = date;
        this.description = description;
        this.attendees = attendees;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getAttendees() {
        return attendees;
    }

    public String getTime() {
        return time;
    }
}
