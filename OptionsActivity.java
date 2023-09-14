package com.example.planningmeeting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OptionsActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE_BACKGROUND = 1;
    private static final int REQUEST_IMAGE_PICK_BACKGROUND = 2;
    private static final int REQUEST_IMAGE_CAPTURE_AVATAR = 3;
    private static final int REQUEST_IMAGE_PICK_AVATAR = 4;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    TextView editTextLogin, editTextBio, editTextEmail, editTextPassword;
    Button buttonCancel, buttonApply;
    ImageView imageViewBackground;
    ShapeableImageView imageViewAvatar;

    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        User currentUser = intent.getParcelableExtra("user");

        editTextLogin = findViewById(R.id.editTextLogin);
        editTextLogin.setText(currentUser.getLogin());

        editTextBio = findViewById(R.id.editTextBio);
        editTextBio.setText(currentUser.getBiography());

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextEmail.setText(currentUser.getEmail());

        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPassword.setText(currentUser.getPassword());

        imageViewBackground = findViewById(R.id.image_background);
        Glide.with(this)
                .load(currentUser.getBackground())
                .into(imageViewBackground);
        imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSelectionDialog(REQUEST_IMAGE_PICK_BACKGROUND, REQUEST_IMAGE_CAPTURE_BACKGROUND);
            }
        });

        imageViewAvatar = findViewById(R.id.img_avatar);
        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .into(imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageSelectionDialog(REQUEST_IMAGE_PICK_AVATAR, REQUEST_IMAGE_CAPTURE_AVATAR);
            }
        });

        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(OptionsActivity.this);
            }
        });

        buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newLogin = editTextLogin.getText().toString();
                String newBio = editTextBio.getText().toString();
                String newEmail = editTextEmail.getText().toString();
                String newPassword = editTextPassword.getText().toString();

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userRef = database.getReference("users").child(userId);

                    // Обновление данных пользователя в базе данных
                    userRef.child("login").setValue(newLogin);
                    userRef.child("biography").setValue(newBio);
                    userRef.child("email").setValue(newEmail);

                    // Изменение пароля в Firebase Authentication
                    currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(OptionsActivity.this, "Пароль обновлен", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OptionsActivity.this, "Ошибка при обновлении пароля", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    uploadAndUpdatePhotos(userId);

                    Toast.makeText(OptionsActivity.this, "Информация обновлена", Toast.LENGTH_SHORT).show();

                    // Переход на MainActivity
                    Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Пользователь не аутентифицирован, обработайте эту ситуацию по вашему усмотрению
                }
            }
        });
    }

    private void showImageSelectionDialog(int pickRequestCode, int captureRequestCode) {
        String[] options = {"Сделать фото", "Выбрать из галереи"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Выберите изображение");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Пользователь выбрал "Сделать фото"
                if (ContextCompat.checkSelfPermission(OptionsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OptionsActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    openCamera(captureRequestCode);
                }
            } else if (which == 1) {
                // Пользователь выбрал "Выбрать из галереи"
                openGallery(pickRequestCode);
            }
        });
        builder.show();
    }

    private void openCamera(int requestCode) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Создаем временный файл, в который будет сохранено изображение
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(cameraIntent, requestCode);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openGallery(int requestCode) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, запускаем камеру
                openCamera(REQUEST_IMAGE_CAPTURE_BACKGROUND);
            } else {
                // Разрешение не получено, выводим сообщение или предоставляем альтернативный путь для выбора изображения
                Toast.makeText(this, "Доступ к камере отклонен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE_BACKGROUND || requestCode == REQUEST_IMAGE_PICK_BACKGROUND) {
                if (requestCode == REQUEST_IMAGE_CAPTURE_BACKGROUND && cameraImageUri != null) {
                    handleCameraImageResult(data, imageViewBackground);
                } else {
                    handleImageSelectionResult(data, imageViewBackground);
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE_AVATAR || requestCode == REQUEST_IMAGE_PICK_AVATAR) {
                if (requestCode == REQUEST_IMAGE_CAPTURE_AVATAR && cameraImageUri != null) {
                    handleCameraImageResult(data, imageViewAvatar);
                } else {
                    handleImageSelectionResult(data, imageViewAvatar);
                }
            }
        }
    }

    private void handleCameraImageResult(Intent data, ImageView imageView) {
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraImageUri);
            imageView.setImageBitmap(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleImageSelectionResult(Intent data, ImageView imageView) {
        if (data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadAndUpdatePhotos(String userId) {
        // Получите ссылку на Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Загрузка и обновление фонового изображения
        Uri backgroundUri = getImageUri(imageViewBackground);
        if (backgroundUri != null) {
            StorageReference backgroundRef = storageRef.child("users").child(userId).child("background.jpg");
            backgroundRef.putFile(backgroundUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Фоновое изображение успешно загружено
                        // Получите ссылку на загруженное изображение
                        backgroundRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String backgroundUrl = uri.toString();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            userRef.child("background").setValue(backgroundUrl);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Произошла ошибка при загрузке фонового изображения
                        Toast.makeText(OptionsActivity.this, "Ошибка при загрузке фонового изображения", Toast.LENGTH_SHORT).show();
                    });
        }

        // Загрузка и обновление аватара
        Uri avatarUri = getImageUri(imageViewAvatar);
        if (avatarUri != null) {
            StorageReference avatarRef = storageRef.child("users").child(userId).child("avatar.jpg");
            avatarRef.putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Аватар успешно загружен
                        // Получите ссылку на загруженное изображение
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String avatarUrl = uri.toString();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            userRef.child("photoUrl").setValue(avatarUrl);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Произошла ошибка при загрузке аватара
                        Toast.makeText(OptionsActivity.this, "Ошибка при загрузке аватара", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private Uri getImageUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
                return Uri.parse(path);
            }
        }
        return null;
    }
}
