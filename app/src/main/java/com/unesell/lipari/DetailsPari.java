package com.unesell.lipari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.unesell.lipari.databinding.ActivityDetailsPariBinding;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DetailsPari extends AppCompatActivity {

    private ActivityDetailsPariBinding binding;
    String APIuri = "https://unesell.com/api/lipari/info.pari.php?id=";

    TextView pari_name; TextView UserName_One;
    TextView pari_info; TextView UserName_Two;
    TextView pari_win;
    TextView pari_lose;
    TextView xp;
    String ID;
    Context context;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsPariBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        ExtendedFloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Inocialithing
        pari_name = (TextView) findViewById(R.id.pari_name);
        pari_info = (TextView) findViewById(R.id.pari_info);
        pari_win = (TextView) findViewById(R.id.pari_win);
        pari_lose = (TextView) findViewById(R.id.pari_lose);
        xp = (TextView) findViewById(R.id.xp);
        UserName_One = (TextView) findViewById(R.id.UserName_One);
        UserName_Two = (TextView) findViewById(R.id.UserName_Two);

        // Logic pari activity (pari_id)
        Bundle arguments = getIntent().getExtras();
        ID = arguments.get("pari_id").toString();
        infoPari();
    }

    public void infoPari(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    //s.miroshnichenko.mail@gmail.com
                    url = new URL(APIuri + ID);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);

                            String Name = jsonObject.getString("Name");
                            String Info = jsonObject.getString("Info").replace("<br/>", "\n");
                            String win = jsonObject.getString("win");
                            String lost = jsonObject.getString("lost");
                            String expiriens = jsonObject.getString("xp_boost");

                            String autor_id = jsonObject.getString("autor_id");
                            String executor_id = jsonObject.getString("executor_id");

                            // Output info in app
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pari_name.setText(Name);
                                    pari_info.setText(Info);
                                    pari_win.setText(win);
                                    pari_lose.setText(lost);
                                    xp.setText(expiriens + " XP");

                                    ImageView avatarOne = (ImageView) findViewById(R.id.UserPari_One);
                                    ImageView avatarTwo = (ImageView) findViewById(R.id.UserPari_Two);

                                    if(Integer.parseInt(autor_id) != 0){
                                        Picasso.get().load("https://unesell.com/data/users/avatar/" + autor_id + ".png").into(avatarOne);
                                    }else{
                                        Picasso.get().load("https://unesell.com/assets/img/default.png").into(avatarOne);
                                    }

                                    if(Integer.parseInt(executor_id) != 0){
                                        Picasso.get().load("https://unesell.com/data/users/avatar/" + executor_id + ".png").into(avatarTwo);
                                    }else{
                                        Picasso.get().load("https://unesell.com/assets/img/default.png").into(avatarTwo);
                                    }

                                    users_name_pari(autor_id, executor_id);
                                }
                            });

                        } catch (JSONException e) {
                            Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void users_name_pari(String autor_id, String executor_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;
                try {
                    url = new URL("https://unesell.com/api/lipari/user.pari.name.php?a_id=" + autor_id + "&e_id=" + executor_id);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);
                            String autor_name = jsonObject.getString("autor");
                            String executor_name = jsonObject.getString("executor");

                            // Output info in app
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(autor_name == null || autor_name == "null"){
                                        UserName_One.setText(getResources().getString(R.string.zero_autor));
                                    }else{
                                        UserName_One.setText(autor_name);
                                    }

                                    if(executor_name == null || executor_name == "null"){
                                        UserName_Two.setText(getResources().getString(R.string.zero_executor));
                                    }else{
                                        UserName_Two.setText(executor_name);
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Ошибка обратобки", Toast.LENGTH_SHORT).show();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

}