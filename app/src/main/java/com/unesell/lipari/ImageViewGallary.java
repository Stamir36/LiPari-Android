package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewGallary extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view_gallary);

        imageView = (ImageView)  findViewById(R.id.imageView);
        Bundle arguments = getIntent().getExtras();
        String URI = arguments.get("link").toString();

        Picasso.get().load(URI).into(imageView);
    }

    public void closeGallary(View view) {
        finish();
    }
}