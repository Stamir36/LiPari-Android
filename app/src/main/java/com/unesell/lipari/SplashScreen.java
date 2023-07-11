package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Context;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import  com.google.android.gms.auth.api.identity.SignInClient;
import  com.google.android.gms.auth.api.identity.BeginSignInRequest;
import  com.google.android.gms.auth.api.identity.*;
import  com.google.android.gms.auth.api.identity.BeginSignInRequest.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.tasks.*;
import com.squareup.picasso.Picasso;

import android.content.IntentSender;
import android.content.ClipboardManager;
import android.content.ClipData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SplashScreen extends AppCompatActivity {

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    SharedPreferences sPref;
    ImageView MainBackground; // MainBackground
    Context context;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private BeginSignInRequest signUpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_form);
        context = this;
        MainBackground = (ImageView) findViewById(R.id.MainBackground);
        sPref = getSharedPreferences("Account", MODE_PRIVATE);

        oneTapClient = Identity.getSignInClient(this);
        // Авторизация
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        // Регистрация
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    String username = credential.getId();
                    String password = credential.getPassword();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with your backend.
                        AuthGo(idToken);
                    } else if (password != null) {
                        // Got a saved username and password. Use them to authenticate with your backend.
                        //Toast.makeText(context, "Got password: ", Toast.LENGTH_SHORT).show();
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case CommonStatusCodes.CANCELED:
                            // Don't re-prompt the user.
                            showOneTapUI = false;
                            break;
                        case CommonStatusCodes.NETWORK_ERROR:
                            // Try again or just ignore.
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
    }

    public void AuthGo(String token){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    url = new URL("https://unesell.com/api/app.google.auth.php?token=" + token);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);
                            String identify = jsonObject.getString("id");
                            SharedPreferences.Editor ed = sPref.edit();

                            ed.putString("ID", identify);
                            ed.putString("NAME", "");
                            ed.putString("EMAIL", "");
                            ed.commit();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.welcome), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void OpenAuthPage(View view) {
        Intent intent = new Intent(context, LoginUnesellAccount.class);
        startActivity(intent);
    }

    public void GoogleAuth(View view) {
        // Вызов входа в одно касание. Этот метод можно вызвать при нажатии кнопки, а не при старте активности.
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Toast.makeText(context, "Couldn't start One Tap UI: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        // Аккаунтов нет. Запуск регистрации в одно касание.
                    }
                });
    }
}