package com.unesell.lipari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.card.MaterialCardView;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.widget.ScrollView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import com.unesell.lipari.databinding.ActivityDetailsPariBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class DetailsPari extends AppCompatActivity {

    private ActivityDetailsPariBinding binding;
    String APIuri = "https://unesell.com/api/lipari/info.pari.php?id=";

    SharedPreferences sPref;
    TextView pari_name; TextView UserName_One;
    TextView pari_info; TextView UserName_Two;
    TextView pari_win; TextView status;
    TextView pari_lose; TextView nocomm;
    TextView xp; ScrollView Information;
    String ID;
    Context context;
    ExtendedFloatingActionButton fab;
    ConstraintLayout commentsViev;

    RecyclerView recurceComments;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> avatar = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    Boolean fabVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsPariBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        fab = binding.fab;

        // Inocialithing
        pari_name = (TextView) findViewById(R.id.pari_name);
        pari_info = (TextView) findViewById(R.id.pari_info);
        pari_win = (TextView) findViewById(R.id.pari_win);
        pari_lose = (TextView) findViewById(R.id.pari_lose);
        xp = (TextView) findViewById(R.id.xp);
        nocomm = (TextView) findViewById(R.id.nocomm);
        UserName_One = (TextView) findViewById(R.id.UserName_One);
        UserName_Two = (TextView) findViewById(R.id.UserName_Two);
        status = (TextView) findViewById(R.id.status);

        recurceComments = (RecyclerView) findViewById(R.id.recurceComments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recurceComments.setLayoutManager(linearLayoutManager);

        commentsViev = (ConstraintLayout) findViewById(R.id.commentsViev);
        // Logic pari activity (pari_id)
        Bundle arguments = getIntent().getExtras();
        ID = arguments.get("pari_id").toString();
        infoPari();

        Information = (ScrollView) findViewById(R.id.Information);
        BottomNavigationView bottom_navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.page_1) {
                    Information.setVisibility(View.VISIBLE);
                    commentsViev.setVisibility(View.GONE);

                    if(fabVisible){
                        fab.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                if (id == R.id.page_2) {
                    fab.setVisibility(View.GONE);
                    Information.setVisibility(View.GONE);
                    nocomm.setVisibility(View.GONE);
                    commentsViev.setVisibility(View.VISIBLE);
                    //CommentsAdapter
                    clearRecurseViev();
                    loadJSONComments("https://unesell.com/api/lipari/comments.pari.php?id=" + ID);
                    return true;
                }

                return true; // return true;
            }
        });

    }

    private void  loadJSONComments(String url){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String>(){
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("comments");
                            if (jsonArray.length() > 0){
                                for (int i=0;i< jsonArray.length();i++){
                                    JSONObject userData=jsonArray.getJSONObject(i);
                                    if(userData.getString("user_name").isEmpty() || userData.getString("user_name") == null || userData.getString("user_name") == "null"){
                                        nocomm.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                    name.add(userData.getString("user_name"));
                                    comments.add(userData.getString("text"));
                                    avatar.add(userData.getString("user_id"));
                                    date.add(userData.getString("Data"));
                                }
                            }else{
                                nocomm.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            nocomm.setVisibility(View.VISIBLE);
                        }
                        CommentsAdapter comAdapter = new CommentsAdapter(name, comments, avatar, date, DetailsPari.this);
                        recurceComments.setAdapter(comAdapter);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private ArrayList< JSONObject> getArrayListFromJSONArray(JSONArray jsonArray){
        ArrayList< JSONObject> aList = new ArrayList< JSONObject>();
        try {
            if(jsonArray!= null){
                for(int i = 0; i< jsonArray.length();i++){
                    aList.add(jsonArray.getJSONObject(i));
                }
            }
        }catch (JSONException js){
            js.printStackTrace();
        }
        return aList;
    }

    public  static  String EncodingToUTF8(String response){
        try {
            byte[] code = response.toString().getBytes("ISO-8859-1");
            response = new String(code, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
        return response;
    }

    public void clearRecurseViev(){
        name.clear(); comments.clear(); avatar.clear(); date.clear();
    }

    public void SettingsFab(String ID_autor, String ID_Executor){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.view.LayoutInflater li = android.view.LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_join_pari, null);
                MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(context);
                mDialogBuilder.setView(promptsView);
                mDialogBuilder.setTitle(getResources().getString(R.string.connect_pari));

                final MaterialCardView JoinIniciator = (MaterialCardView) promptsView.findViewById(R.id.initCard);
                final MaterialCardView JoinOpponent = (MaterialCardView) promptsView.findViewById(R.id.opponentCard);

                if(ID_autor.equals(sPref.getString("ID", ""))){ JoinIniciator.setVisibility(View.GONE); }
                if(Integer.parseInt(ID_autor) != 0){ JoinIniciator.setVisibility(View.GONE); }
                if(ID_Executor.equals(sPref.getString("ID", ""))){ JoinOpponent.setVisibility(View.GONE); }
                if(Integer.parseInt(ID_Executor) != 0){ JoinOpponent.setVisibility(View.GONE); }

                JoinIniciator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Join("autor");
                    }
                });

                JoinOpponent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Join("executor");
                    }
                });

                mDialogBuilder.show();
            }
        });
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
                            String stasusPari = jsonObject.getString("stasus");

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

                                    if(stasusPari.equals("search")){ status.setText(getResources().getString(R.string.status_1)); }
                                    if(stasusPari.equals("go")){ status.setText(getResources().getString(R.string.status_2)); }
                                    if(stasusPari.equals("executor_lose")){ status.setText(getResources().getString(R.string.status_3)); }
                                    if(stasusPari.equals("executor_send_check")){ status.setText(getResources().getString(R.string.status_4)); }
                                    if(stasusPari.equals("autor_win_check")){ status.setText(getResources().getString(R.string.status_5)); }
                                    if(stasusPari.equals("end")){ status.setText(getResources().getString(R.string.status_6)); }

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

                                    if(Integer.parseInt(executor_id) != 0 && Integer.parseInt(autor_id) != 0){
                                        fab.setVisibility(View.GONE);
                                        fabVisible = false;
                                    }else{

                                    }
                                    if( autor_id.equals(sPref.getString("ID", "")) || executor_id.equals(sPref.getString("ID", ""))){
                                        fab.setVisibility(View.GONE);
                                        fabVisible = false;
                                    }
                                    users_name_pari(autor_id, executor_id);
                                    SettingsFab(autor_id, executor_id);
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

    public void Join(String role){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;
                try {
                    url = new URL("https://unesell.com/api/lipari/join.pari.php?id=" + sPref.getString("ID", "") + "&id_pari=" + ID + "&rule=" + role);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context, DetailsPari.class);
                                intent.putExtra("pari_id", ID);
                                context.startActivity(intent);
                                finish();
                            }
                        });
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

    public void sendComments(View view) {
        TextInputLayout text = (TextInputLayout) findViewById(R.id.comments_text);
        String string_comments = text.getEditText().getText().toString().replace(" ", "%20");

        if(string_comments.length() > 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url;
                    HttpsURLConnection connection = null;
                    try {
                        url = new URL("https://unesell.com/api/lipari/send.comment.php?id_user=" + sPref.getString("ID", "") + "&id_post=" + ID + "&text=" + string_comments);
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.getResponseCode();

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_comment_send), Toast.LENGTH_SHORT).show();
                                    text.getEditText().setText("");
                                    fab.setVisibility(View.GONE);
                                    Information.setVisibility(View.GONE);
                                    nocomm.setVisibility(View.GONE);
                                    commentsViev.setVisibility(View.VISIBLE);
                                    //CommentsAdapter
                                    clearRecurseViev();
                                    loadJSONComments("https://unesell.com/api/lipari/comments.pari.php?id=" + ID);
                                }
                            });

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
}