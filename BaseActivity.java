package com.example.planningmeeting;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Создайте новую разметку activity_base.xml

        progressBar = findViewById(R.id.progressBar);
    }
    // Метод для показа индикатора ожидания
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Метод для скрытия индикатора ожидания
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
