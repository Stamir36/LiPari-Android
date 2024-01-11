package com.unesell.lipari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;

public class ImageViewGallary extends AppCompatActivity {

    ActionBar actionBar;
    boolean backPressedOnce = false;
    SubsamplingScaleImageView imageView;
    String ImageURI;
    String Name;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Добавляем ActionBar в активити
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Показываем кнопку "Назад"
            actionBar.setTitle(R.string.Title_imageView); // Устанавливаем заголовок
        }

        setContentView(R.layout.image_view_gallary);

        imageView = findViewById(R.id.imageView);
        Bundle arguments = getIntent().getExtras();
        ImageURI = arguments.getString("link");
        Name = extractFileNameFromUri(ImageURI);

        TextView imageName = (TextView) findViewById(R.id.imageName);
        int maxLength = 20;

        if (Name.length() > maxLength) {
            String trimmedName = Name.substring(0, maxLength) + "...";
            imageName.setText(trimmedName);
        } else {
            imageName.setText(Name);
        }

        Glide.with(this)
                .asBitmap()
                .load(ImageURI)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // Очистка ресурсов
                    }
                });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActionBarVisibility();
            }
        });
    }

    private void toggleActionBarVisibility() {
        if (actionBar != null) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }

        backPressedOnce = true;
        toggleActionBarVisibility();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 1000); // Задержка 1 секунда
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String extractFileNameFromUri(String uri) {
        Uri imageUri = Uri.parse(uri);
        return imageUri.getLastPathSegment();
    }

    public void DownloadImage(View view) {
        // Проверка наличия разрешения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // В Android 10 и выше разрешение не требуется, используем MediaStore для сохранения
            saveImageUsingMediaStore();
        } else {
            // В Android 9 и ниже запрашиваем разрешение на запись в хранилище
            requestWriteStoragePermission();
        }
    }

    private void requestWriteStoragePermission() {
        // Запрос разрешения у пользователя
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveImageUsingMediaStore() {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, Name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LiPari");

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try {
            OutputStream outputStream = resolver.openOutputStream(imageUri);
            Glide.with(this)
                    .asBitmap()
                    .load(ImageURI)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            Toast.makeText(ImageViewGallary.this, R.string.image_downloaded_successfully, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLoadCleared(Drawable placeholder) {
                            // Очистка ресурсов
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.failed_to_download_image, Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для обработки результата запроса разрешения
    @SuppressLint({"NewApi", "MissingSuperCall"})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, сохраняем изображение
                saveImageUsingMediaStore();
            } else {
                // Разрешение не предоставлено, обработайте это соответствующим образом
                Toast.makeText(this, R.string.permission_denied_cannot_download_image, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
