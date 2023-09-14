package com.example.planningmeeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textViewUserName, textViewUserBio, count_followers, count_following;
    private Button button_notes;
    private AppCompatImageButton button_option;
    private ShapeableImageView image;
    private ImageView imgback;
    private User currentUser; // Поле для хранения информации о текущем пользователе


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_notes = findViewById(R.id.swipe);
        button_option = findViewById(R.id.options);

        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserBio = findViewById(R.id.textViewUserBio);
        count_followers = findViewById(R.id.count_followers);
        count_following = findViewById(R.id.count_following);
        image = findViewById(R.id.imvCircularWithStroke);
        imgback = findViewById(R.id.imgback);

        button_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                    startActivity(intent);
            }
        });

        button_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference usersReference = firebaseDatabase.getReference("users").child(currentUserId);

            usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUser = dataSnapshot.getValue(User.class);

                        if (currentUser != null) {
                            int followingCount = currentUser.getSubscriptions() != null ? currentUser.getSubscriptions().size() : 0;
                            int followersCount = currentUser.getFollowers() != null ? currentUser.getFollowers().size() : 0;

                            textViewUserBio.setText(currentUser.getBiography());
                            textViewUserName.setText(currentUser.getLogin());
                            count_following.setText(String.valueOf(followingCount));
                            count_followers.setText(String.valueOf(followersCount));



                            if (currentUser.getPhotoUrl() != null && !currentUser.getPhotoUrl().isEmpty()
                                    && currentUser.getBackground() != null && !currentUser.getBackground().isEmpty()) {
                                Glide.with(MainActivity.this)
                                        .load(currentUser.getPhotoUrl())
                                        .into(image);
                                Glide.with(MainActivity.this)
                                        .load(currentUser.getBackground())
                                        .into(imgback);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
