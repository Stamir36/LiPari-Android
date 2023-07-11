package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LivePlayer extends AppCompatActivity {

    String StreamID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_player);
        Bundle arguments = getIntent().getExtras();
        StreamID = arguments.get("StreamID").toString();
    }
}