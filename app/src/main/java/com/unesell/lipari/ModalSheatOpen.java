package com.unesell.lipari;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ModalSheatOpen extends BottomSheetDialogFragment {
    SharedPreferences sPref;
    TextView UserNameAccount; MaterialCardView card_money;
    TextView menu_xp; TextView level_text;
    TextView money; ProgressBar progressBar5;
    View view; ImageView UserAvatar;

    Boolean go = true;

    public static ModalSheatOpen newInstance() {
        return new ModalSheatOpen();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_menu, container, false);

        final MaterialCardView accountcard = view.findViewById(R.id.accountcard);
        UserNameAccount = view.findViewById(R.id.UserNameAccount);
        menu_xp = view.findViewById(R.id.menu_xp);
        money = view.findViewById(R.id.money);
        level_text = view.findViewById(R.id.level_text);
        card_money = (MaterialCardView) view.findViewById(R.id.card_money);
        progressBar5 = (ProgressBar) view.findViewById(R.id.progressBar5);
        UserAvatar = view.findViewById(R.id.UserAvatar);
        sPref = view.getContext().getSharedPreferences("Account", view.getContext().MODE_PRIVATE);
        String ID = sPref.getString("ID", "");
        String avatar_id = sPref.getString("avatar_id", "");
        Picasso.get().load("https://unesell.com/data/users/avatar/" + avatar_id).into(UserAvatar);
        UpdateData();

        Button acc_exit = (Button) view.findViewById(R.id.acc_exit);
        Button moneyAdButtom = (Button) view.findViewById(R.id.moneyAdButtom);

        MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(view.getContext());
        mDialogBuilder.setTitle(getResources().getString(R.string.exitAccount_text_1));
        mDialogBuilder.setMessage(getResources().getString(R.string.exitAccount_text_2));
        acc_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                mDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.exitAccount_text_3),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        sPref = view.getContext().getSharedPreferences("Account", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor ed = sPref.edit();

                                        ed.putString("ID", "");
                                        ed.commit();

                                        Toast.makeText(view.getContext(),getResources().getString(R.string.succses_exit), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.Cansel),
                                new android.content.DialogInterface.OnClickListener() {
                                    public void onClick(android.content.DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                mDialogBuilder.show();

            }
        });

        moneyAdButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                Intent intent = new Intent(view.getContext(), RewardActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        accountcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                Intent intent = new Intent(view.getContext(), Profile.class);
                intent.putExtra("user_ui", ID); intent.putExtra("noDestroy", "no");
                view.getContext().startActivity(intent);
            }
        });

        return view;
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

                            String nameAccount = jsonObject.getString("name");
                            String xp_now = jsonObject.getString("xp_now");
                            String money_json = jsonObject.getString("money");
                            String avatar_u = jsonObject.getString("avatar");

                            UserNameAccount.setText(nameAccount);
                            menu_xp.setText(xp_now + " XP");
                            money.setText(money_json + " " + getResources().getString(R.string.money));

                            progressBar5.setMax(1000); progressBar5.setProgress(Integer.parseInt( xp_now.substring(xp_now.length() - 3, xp_now.length()) ));
                            String level = xp_now.substring(0, xp_now.length() - 3);
                            if (level.length() == 0){ level = "0"; }
                            level_text.setText(getResources().getString(R.string.level) + ": " + level);
                            go = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (go){
                        UpdateData();
                    }
                }
            }
        }).start();
    }
}