package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Create_Pari_Post extends AppCompatActivity {

    Context context;
    SharedPreferences sPref;
    ArrayAdapter<?> adapter;
    TextView xp;
    ProgressBar loadingBar;
    ScrollView form;
    TextView server_connect_text;
    int XP_user = 10;

    TextInputLayout Post_Name;
    TextInputLayout Post_Init;
    TextInputLayout Post_Opponent;
    Spinner spinner;
    TextInputLayout Post_Info;
    SeekBar seekBar;
    MaterialSwitch switch_visible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_pari_post);
        context = this;
        // Настройка полей
        Post_Name = (TextInputLayout) findViewById(R.id.Post_Name);
        Post_Init = (TextInputLayout) findViewById(R.id.Post_Init);
        Post_Opponent = (TextInputLayout) findViewById(R.id.Post_Opponent);
        Post_Info = (TextInputLayout) findViewById(R.id.Post_Info);
        switch_visible = (MaterialSwitch) findViewById(R.id.switch_visible);
        // Настройка прогрузки
        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        spinner = findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                xp.setText(XP_user + " XP - " + String.valueOf(seekBar.getProgress() + 10) + " XP");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        xp = (TextView) findViewById(R.id.xp);

        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
        server_connect_text = (TextView) findViewById(R.id.server_connect_text);
        form = (ScrollView) findViewById(R.id.PostFirm);
        form.setVisibility(View.GONE);
        // Получение информации из сервера.
        ServerInformation();
    }

    public void ServerInformation(){
// Обновление данных аккаунта
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    url = new URL("https://unesell.com/api/lipari/user.pari.data.php?id=" + sPref.getString("ID", ""));
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);

                            String xp_now = jsonObject.getString("xp_now");
                            String banned = jsonObject.getString("banned");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(banned.equals("Yes")){
                                        MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(context);
                                        mDialogBuilder.setTitle(getResources().getString(R.string.ban_create));
                                        mDialogBuilder.setMessage(getResources().getString(R.string.ban_create_sub));
                                        mDialogBuilder
                                                .setCancelable(false)
                                                .setNegativeButton(getResources().getString(R.string.OK),
                                                        new android.content.DialogInterface.OnClickListener() {
                                                            public void onClick(android.content.DialogInterface dialog,int id) {
                                                                dialog.cancel();
                                                                finish();
                                                            }
                                                        });

                                        mDialogBuilder.show();
                                    }

                                    SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
                                    seekBar.setMax(Integer.parseInt(xp_now) - 10);
                                    XP_user = Integer.parseInt(xp_now);
                                    xp.setText(xp_now + " XP - " + String.valueOf(seekBar.getProgress() + 10) + " XP");

                                    loadingBar.setVisibility(View.GONE);
                                    server_connect_text.setVisibility(View.GONE);
                                    form.setVisibility(View.VISIBLE);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ServerInformation();
                }
            }
        }).start();
    }

    public void CreatePari(View view) {
        if (Post_Name.getEditText().getText().length() != 0 && Post_Init.getEditText().getText().length() != 0 && Post_Opponent.getEditText().getText().length() != 0 && Post_Info.getEditText().getText().length() != 0){
            loadingBar.setVisibility(View.VISIBLE);
            server_connect_text.setVisibility(View.VISIBLE);
            form.setVisibility(View.GONE);

            final String name = Post_Name.getEditText().getText().toString().replace(" ", "%20");
            final String win = Post_Init.getEditText().getText().toString().replace(" ", "%20");
            final String lose = Post_Opponent.getEditText().getText().toString().replace(" ", "%20");
            final String info = Post_Info.getEditText().getText().toString().replace(" ", "%20");
            final String sex = Integer.toString(spinner.getSelectedItemPosition());

            String vis;
            if(switch_visible.isChecked()){
                vis = "all";
            }else{
                vis = "private";
            }

            final String visibles = vis;
            final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
            final String expiriense = Integer.toString(seekBar.getProgress() + 10);

            String getUri = "https://unesell.com/api/lipari/create.pari.GET.php?user_id=" + sPref.getString("ID", "") + "&name=" + name + "&win=" + win + "&lose=" + lose + "&sex=" + sex + "&content=" + info + "&autor_id=0" + "&visibles=" + visibles + "&expiriense=" + expiriense + "&time_complite=infinite";

            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url;
                    HttpsURLConnection connection = null;

                    try {
                        url = new URL(getUri);

                        connection = (HttpsURLConnection) url.openConnection();
                        connection.getResponseCode();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                            try {
                                // Reading the main JSON.
                                JSONObject jsonObject = new JSONObject(res);

                                String id = jsonObject.getString("identify");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(context, DetailsPari.class);
                                        intent.putExtra("pari_id", id);
                                        context.startActivity(intent);
                                        finish();
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                loadingBar.setVisibility(View.GONE);
                                server_connect_text.setVisibility(View.GONE);
                                form.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        loadingBar.setVisibility(View.GONE);
                        server_connect_text.setVisibility(View.GONE);
                        form.setVisibility(View.VISIBLE);
                    }
                }
            }).start();
        }else{
            Toast.makeText(context, getResources().getString(R.string.error_text_length), Toast.LENGTH_SHORT).show();
        }
    }
}