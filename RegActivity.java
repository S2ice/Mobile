package com.example.planningmeeting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegActivity extends AppCompatActivity {
    private static final String COOKIE_FILE_NAME = "LoginCredentials";

    private EditText editTextMail, editTextPass, editTextPassVerify;
    private TextView choose;
    private AppCompatButton buttonRegister;
    private ConstraintLayout registrationLayout;

    private String email, password;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressBar = findViewById(R.id.progressBar);
        editTextMail = findViewById(R.id.editTextMail);
        editTextPass = findViewById(R.id.editTextPass);
        editTextPassVerify = findViewById(R.id.editTextPassVerify);
        buttonRegister = findViewById(R.id.buttonRegister);
        choose = findViewById(R.id.choose);
        registrationLayout = findViewById(R.id.Registration);

        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        registrationLayout.startAnimation(slideUpAnimation);

        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(this)
                .asGif()
                .load(R.drawable.flow)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        resource.setLoopCount(1);
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                // Анимация завершена
                                // Можно выполнить дополнительные действия после окончания анимации
                            }
                        });
                        resource.start();
                        return false;
                    }
                })
                .into(imageView);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextMail.getText().toString().trim();
                password = editTextPass.getText().toString().trim();
                registerUser(email, password);
            }
        });

        checkCookieFile();
    }

    private void checkCookieFile() {
        File cookieFile = new File(getFilesDir(), COOKIE_FILE_NAME);
        if (cookieFile.exists()) {
            autoLoginWithCookie(cookieFile);
        } else {
            checkLoginCredentialsFile();
        }
    }

    private void autoLoginWithCookie(File cookieFile) {
        showProgressBar();

        StringBuilder cookieBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(cookieFile));
            String line;
            while ((line = reader.readLine()) != null) {
                cookieBuilder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cookie = cookieBuilder.toString();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCustomToken(cookie)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegActivity.this, "Автоматический вход выполнен", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            Toast.makeText(RegActivity.this, "Ошибка автоматического входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkLoginCredentialsFile() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginCredentials", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        if (!email.isEmpty() && !password.isEmpty()) {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        showProgressBar();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            Toast.makeText(RegActivity.this, "Ошибка входа: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(String email, String password) {
        showProgressBar();
        int code = generateVerificationCode();
        showVerificationDialog(code);
    }

    private void showVerificationDialog(int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение почты");
        builder.setMessage("Введите 4-значный код, который пришел на вашу почту");

        final EditText editTextVerificationCode = new EditText(this);
        editTextVerificationCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextVerificationCode.setText(String.valueOf(code));
        builder.setView(editTextVerificationCode);

        builder.setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String verificationCode = editTextVerificationCode.getText().toString().trim();
                if (verificationCode.equals(String.valueOf(code))) {
                    createUserWithEmailAndPassword(email, password);
                } else {
                    Toast.makeText(RegActivity.this, "Неверный код подтверждения", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Обработка отмены ввода кода подтверждения
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void createUserWithEmailAndPassword(String email, String password) {
        showProgressBar();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressBar();
                        if (task.isSuccessful()) {
                            Toast.makeText(RegActivity.this, "Пользователь успешно создан", Toast.LENGTH_SHORT).show();
                            String username = generateUniqueUsername();
                            saveUserDetailsToDatabase(email, password, username);
                            goToMainActivity();
                        } else {
                            Toast.makeText(RegActivity.this, "Ошибка создания пользователя: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDetailsToDatabase(String email, String password, String username) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference usersReference = firebaseDatabase.getReference("users");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        String photoUrl = "https://firebasestorage.googleapis.com/v0/b/planning-meet.appspot.com/o/BasicPhoto%2Fmaster.jpg?alt;
        String photoback = "https://firebasestorage.googleapis.com/v0/b/planning-meet.appspot.com/o/BasicPhoto%2Fsplash.jpg?alt";

        User user = new User(email, "My Bio", photoUrl, username, password, photoback);

        Map<String, Boolean> following = new HashMap<>();
        Map<String, Boolean> followers = new HashMap<>();
        user.setSubscriptions(following);
        user.setFollowers(followers);

        usersReference.child(currentUserId).setValue(user);
    }

    private int generateVerificationCode() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    private String generateUniqueUsername() {
        String baseUsername = "@user";
        int uniqueNumber = generateVerificationCode();
        String username = baseUsername + uniqueNumber;
        return username;
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

