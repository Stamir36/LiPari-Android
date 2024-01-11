package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.card.MaterialCardView;
import android.view.MenuItem;
import com.squareup.picasso.MemoryPolicy;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.unesell.lipari.databinding.ActivityDetailsPariBinding;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Profile extends AppCompatActivity {
    Context context;
    String ID;
    ImageView user_avatar_profile;
    ImageView ProfilemainBackground;
    SharedPreferences sPref;
    ProgressBar loading_profile;
    LinearLayout p1, p2;

    Boolean exit = false; // Если нужно перезапускать основное активити, то поставить true

    private RecyclerView recyclerView;
    private RewardAdapter rewardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); context = this;
        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        Bundle arguments = getIntent().getExtras();
        ID = arguments.get("user_ui").toString();

        String destroy = arguments.get("noDestroy").toString();
        if (destroy.equals("yes")){
            exit = false;
        }

        user_avatar_profile = (ImageView) findViewById(R.id.user_avatar_profile);
        ProfilemainBackground = (ImageView) findViewById(R.id.ProfilemainBackground);
        loading_profile = (ProgressBar) findViewById(R.id.loading_profile);
        p1 = (LinearLayout) findViewById(R.id.p1); p2 = (LinearLayout) findViewById(R.id.p2);

        loading_profile.setVisibility(View.VISIBLE);
        p1.setVisibility(View.GONE);
        p2.setVisibility(View.GONE);
        UpdateData();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rewardAdapter = new RewardAdapter();
        recyclerView.setAdapter(rewardAdapter);

        String RewardAPI = "https://unesell.com/api/lipari/user.pari.reward.php?id=" + ID;
        new FetchRewardsTask().execute(RewardAPI);
    }

    public void UpdateData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    url = new URL("https://unesell.com/api/lipari/account.lipari.php?id=" + ID);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String res = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                        try {
                            // Reading the main JSON.
                            JSONObject jsonObject = new JSONObject(res);

                            String nameAccount = jsonObject.getString("name");
                            String imgBackground = jsonObject.getString("imgBackground");
                            String about_me = jsonObject.getString("about_me");
                            String xp_now = jsonObject.getString("xp_now");
                            String money_json = jsonObject.getString("money");
                            String avatar_u = jsonObject.getString("avatar");
                            String Reg_Date = jsonObject.getString("Reg_Date");


                            TextView Profile_name = (TextView) findViewById(R.id.Profile_name);
                            Profile_name.setText(nameAccount);
                            TextView about_me_t = (TextView) findViewById(R.id.about_me);
                            about_me_t.setText(about_me);
                            TextView profile_level = (TextView) findViewById(R.id.profile_level);
                            String level = "0";
                            int xp = Integer.parseInt(xp_now);
                            if (xp >= 1000) {
                                int levelValue = (xp / 1000) + 1;
                                level = String.valueOf(levelValue);
                            }
                            profile_level.setText(getResources().getString(R.string.level) + ": " + level);
                            TextView reg_date = (TextView) findViewById(R.id.reg_date);
                            reg_date.setText(getResources().getString(R.string.regdate) + " " + Reg_Date);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.get().load("https://unesell.com/data/users/avatar/" + avatar_u).into(user_avatar_profile);
                                    if(imgBackground.equals("/assets/img/background/Surface.jpg")){
                                        Picasso.get().load("https://unesell.com/assets/img/background/Surface.jpg").memoryPolicy(MemoryPolicy.NO_CACHE).into(ProfilemainBackground);
                                    }else {
                                        Picasso.get().load(imgBackground).memoryPolicy(MemoryPolicy.NO_CACHE).into(ProfilemainBackground);
                                    }
                                    p1.setVisibility(View.VISIBLE);
                                    p2.setVisibility(View.VISIBLE);
                                    loading_profile.setVisibility(View.GONE);
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

    private class FetchRewardsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }

                reader.close();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                List<Reward> rewardList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject rewardObj = jsonArray.getJSONObject(i);
                    String userId = rewardObj.getString("user_id");
                    String icon = rewardObj.getString("icon");
                    String name = rewardObj.getString("name");
                    String info = rewardObj.getString("info");

                    Reward reward = new Reward(userId, icon, name, info);
                    rewardList.add(reward);
                }

                rewardAdapter.setRewardList(rewardList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause(){
        if(exit){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
        super.onPause();
    }
}