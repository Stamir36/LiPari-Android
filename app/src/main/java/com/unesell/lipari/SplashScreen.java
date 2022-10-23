package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Context;
import android.net.Uri;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sPref;
    ImageView MainBackground; // MainBackground
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_form);
        context = this;
        MainBackground = (ImageView) findViewById(R.id.MainBackground);
        MainBackground.setImageResource(R.drawable.m3_red);

    }

    public void OpenAuthPage(View view) {
        Intent intent = new Intent(context, LoginUnesellAccount.class);
        startActivity(intent);
    }

    public void WebReg(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://unesell.com/registration/"));
        startActivity(intent);
    }
}