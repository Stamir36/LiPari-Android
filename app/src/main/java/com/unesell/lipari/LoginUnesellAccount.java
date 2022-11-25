package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import android.widget.Toast;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginUnesellAccount extends AppCompatActivity {

    TextInputLayout email;
    TextInputLayout password;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_unesell_account);

        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);


        //String text = textInputLayout.getEditText().getText();
    }

    public void LoginAccount(View view) {
        //Обработка авторизации в аккаунте.
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                sPref = getSharedPreferences("Account", MODE_PRIVATE);

                try {
                    //s.miroshnichenko.mail@gmail.com
                    url = new URL("https://unesell.com/api/applogin.php?email=" + email.getEditText().getText() + "&password=" + password.getEditText().getText());
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);

                            String identify = jsonObject.getString("id");
                            String name = jsonObject.getString("name");
                            String email_u = jsonObject.getString("email");

                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("ID", identify);
                            ed.putString("NAME", name);
                            ed.putString("EMAIL", email_u);
                            ed.commit();

                            try {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.welcome) + ", " + name, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }catch (Exception e){

                            }
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(view, getResources().getString(R.string.error_login_data), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(view, getResources().getString(R.string.connect_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    Snackbar.make(view, getResources().getString(R.string.host_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}