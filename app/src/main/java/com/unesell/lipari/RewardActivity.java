package com.unesell.lipari;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.AdError;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class RewardActivity extends AppCompatActivity {
    private RewardedAd mRewardedAd;
    SharedPreferences sPref;
    private final String TAG = "MenuMoneyAd";
    Context context; Button moneyAdButtom;
    ProgressBar progressBar6;
    TextView ErrorTextAd;
    Boolean exit = false; // Если нужно перезапускать основное активити, то поставить true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        context = this;

        moneyAdButtom = (Button) findViewById(R.id.moneyButtomShow);
        progressBar6 = (ProgressBar) findViewById(R.id.progressBar6);
        ErrorTextAd = (TextView) findViewById(R.id.ErrorTextAd);
        sPref = getSharedPreferences("Account", MODE_PRIVATE);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-2744119478858191/4603856770",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        progressBar6.setVisibility(View.GONE);
                        ErrorTextAd.setVisibility(View.VISIBLE);
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        progressBar6.setVisibility(View.GONE);
                        moneyAdButtom.setVisibility(View.VISIBLE);
                    }
                });

        moneyAdButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                if (mRewardedAd != null) {
                    android.app.Activity activityContext = RewardActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            exit = true;
                            SendMoneyAdd();
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                            progressBar6.setVisibility(View.VISIBLE);
                            moneyAdButtom.setVisibility(View.GONE);
                        }
                    });
                    exit = false;
                } else {
                    Toast.makeText(context, "The rewarded ad wasn't ready yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        UpdateData();
    }

    public void UpdateData(){
        // Обновление данных аккаунта
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    url = new URL("https://unesell.com/api/lipari/account.lipari.php?id=" + sPref.getString("ID", ""));
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);

                            String money = jsonObject.getString("money");

                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    TextView moneyAccount = (TextView) findViewById(R.id.money);
                                    moneyAccount.setText(money + " " + context.getResources().getString(R.string.money));
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UpdateData();
                }
            }
        }).start();
    }


    public void onPause(){
        if(exit){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
        super.onPause();
    }

    public void SendMoneyAdd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                SharedPreferences sPref = getSharedPreferences("Account", MODE_PRIVATE);
                String ID = sPref.getString("ID", "");

                try {
                    url = new URL("https://unesell.com/api/lipari/money.ads.php?id=" + ID);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(context);
                            mDialogBuilder.setTitle(getResources().getString(R.string.ad_ok_title));
                            mDialogBuilder.setMessage(getResources().getString(R.string.ad_ok_sub));
                            mDialogBuilder
                                    .setCancelable(false)
                                    .setNegativeButton(getResources().getString(R.string.thanks),
                                            new android.content.DialogInterface.OnClickListener() {
                                                public void onClick(android.content.DialogInterface dialog,int id) {
                                                    progressBar6.setVisibility(View.GONE);
                                                    moneyAdButtom.setVisibility(View.VISIBLE);
                                                    dialog.cancel();
                                                }
                                            });

                            mDialogBuilder.show();
                            UpdateData();
                            reloadAD();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public void reloadAD(){
        progressBar6.setVisibility(View.VISIBLE);
        moneyAdButtom.setVisibility(View.GONE);
        ErrorTextAd.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-2744119478858191/4603856770",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        progressBar6.setVisibility(View.GONE);
                        ErrorTextAd.setVisibility(View.VISIBLE);
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        progressBar6.setVisibility(View.GONE);
                        moneyAdButtom.setVisibility(View.VISIBLE);
                    }
                });
    }
    public void openStore(View view) {
        Intent openStore = new Intent(this, Store.class);
        startActivity(openStore);
    }
}