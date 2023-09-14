package com.example.planningmeeting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {
    private EditText editTextPassLogin, editTextMailLogin;
    private AppCompatButton buttonLogin;
    private TextView chooseLogin;

    private ImageView imageViewLogin;
    private ConstraintLayout Authorization;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextPassLogin = findViewById(R.id.editTextPassLogin);
        editTextMailLogin = findViewById(R.id.editTextMailLogin);

        buttonLogin = findViewById(R.id.buttonLogin);

        imageViewLogin = findViewById(R.id.imageViewLogin);

        chooseLogin = findViewById(R.id.chooseLogin);
        chooseLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthActivity.this, RegActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Authorization = findViewById(R.id.Authorization);

        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        Authorization.startAnimation(slideUpAnimation);

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
                        // Воспроизводим анимацию
                        resource.setLoopCount(1); // Устанавливаем количество повторений на 1
                        resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                // Анимация завершена
                                // Можно выполнить дополнительные действия после окончания анимации
                            }
                        });
                        resource.start(); // Запускаем анимацию
                        return false;
                    }
                })
                .into(imageViewLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextMailLogin.getText().toString();
                String password = editTextPassLogin.getText().toString();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Аутентификация прошла успешно
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        // Сохранение куки-файлов для автоматического входа в будущем
                                        saveLoginCredentials(email, password);
                                    }
                                    Toast.makeText(AuthActivity.this, "Пользователь успешно авторизовался", Toast.LENGTH_SHORT).show();
                                    // Переход на другую активность (например, MainActivity)
                                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Ошибка аутентификации
                                    Toast.makeText(AuthActivity.this, "Неверные данные для входа", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    private void saveLoginCredentials(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginCredentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}
