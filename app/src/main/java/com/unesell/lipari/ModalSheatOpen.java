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

    public static ModalSheatOpen newInstance() {
        return new ModalSheatOpen();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_menu, container, false);

        final ImageView UserAvatar = view.findViewById(R.id.UserAvatar);
        final MaterialCardView accountcard = view.findViewById(R.id.accountcard);
        UserNameAccount = view.findViewById(R.id.UserNameAccount);
        menu_xp = view.findViewById(R.id.menu_xp);
        money = view.findViewById(R.id.money);
        level_text = view.findViewById(R.id.level_text);
        card_money = (MaterialCardView) view.findViewById(R.id.card_money);
        progressBar5 = (ProgressBar) view.findViewById(R.id.progressBar5);

        sPref = view.getContext().getSharedPreferences("Account", view.getContext().MODE_PRIVATE);
        String ID = sPref.getString("ID", "");
        Picasso.get().load("https://unesell.com/data/users/avatar/" + ID + ".png").into(UserAvatar);

        UpdateData();
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

                            UserNameAccount.setText(nameAccount);
                            menu_xp.setText(xp_now + " XP");
                            money.setText(money_json + " " + getResources().getString(R.string.money));

                            progressBar5.setMax(1000); progressBar5.setProgress(Integer.parseInt( xp_now.substring(xp_now.length() - 3, xp_now.length()) ));
                            String level = xp_now.substring(0, xp_now.length() - 3);

                            level_text.setText(getResources().getString(R.string.level) + ": " + level);

                            card_money.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            menu_xp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UpdateData();
                }finally {
                    card_money.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    menu_xp.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                }
            }
        }).start();
    }
}