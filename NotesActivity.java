package com.example.planningmeeting;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AppCompatButton buttonAddMeet;

    private DatabaseReference meetingsRef;
    private ValueEventListener meetingsListener;
    private RecyclerAdapter adapter;
    private List<Meeting> meetingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        recyclerView = findViewById(R.id.recycler_view);
        buttonAddMeet = findViewById(R.id.buttonAddMeet);
        buttonAddMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, CardEdit.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(NotesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        meetingList = new ArrayList<>();
        adapter = new RecyclerAdapter(NotesActivity.this, (ArrayList<Meeting>) meetingList);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        meetingsRef = database.getReference("users").child(userId).child("meetings");

        meetingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                meetingList.clear();

                Log.d("NotesActivity", "Data received from Firebase: " + dataSnapshot.toString()); // Отладочное сообщение

                for (DataSnapshot meetingSnapshot : dataSnapshot.getChildren()) {
                    Meeting meeting = meetingSnapshot.getValue(Meeting.class);
                    meetingList.add(meeting);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read meetings.", databaseError.toException());
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        meetingsRef.addValueEventListener(meetingsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        meetingsRef.removeEventListener(meetingsListener);
    }
}
