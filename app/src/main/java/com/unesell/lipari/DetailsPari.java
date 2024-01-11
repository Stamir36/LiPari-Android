package com.unesell.lipari;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.card.MaterialCardView;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DetailsPari extends AppCompatActivity {

    private ActivityDetailsPariBinding binding;
    String APIuri = "https://unesell.com/api/lipari/info.pari.php?id=";

    SharedPreferences sPref;
    TextView pari_name; TextView UserName_One; TextView voiting_user_one;
    TextView pari_info; TextView UserName_Two; TextView voiting_user_two;
    TextView pari_win; TextView status;
    TextView pari_lose; TextView nocomm;
    TextView xp; ScrollView Information;
    String ID;
    String live_stream;
    Context context;
    ExtendedFloatingActionButton fab;
    ConstraintLayout commentsViev;
    ConstraintLayout Social;
    MaterialCardView controlcard;
    MaterialCardView pari_card_files;
    ImageView mainBackground;
    MaterialCardView autor_card, executer_card;

    String Cursus = "0";
    
    RecyclerView recurceComments;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> avatar = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    Boolean fabVisible = true;
    private AdView mAdView;

    // File See
    RecyclerView fileView;
    ArrayList<String> namefile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsPariBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        fab = binding.fab;

        // ADS
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Inocialithing
        pari_name = (TextView) findViewById(R.id.pari_name);
        pari_info = (TextView) findViewById(R.id.pari_info);
        pari_win = (TextView) findViewById(R.id.pari_win);
        pari_lose = (TextView) findViewById(R.id.pari_lose);
        xp = (TextView) findViewById(R.id.xp);
        nocomm = (TextView) findViewById(R.id.nocomm);
        UserName_One = (TextView) findViewById(R.id.UserName_One);
        UserName_Two = (TextView) findViewById(R.id.UserName_Two);
        voiting_user_one = (TextView) findViewById(R.id.voiting_user_one);
        voiting_user_two = (TextView) findViewById(R.id.voiting_user_two);
        status = (TextView) findViewById(R.id.status);

        recurceComments = (RecyclerView) findViewById(R.id.recurceComments);
        fileView = (RecyclerView) findViewById(R.id.fileView2);
        LinearLayoutManager filesMeneger = new LinearLayoutManager(getApplicationContext());
        fileView.setLayoutManager(filesMeneger);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recurceComments.setLayoutManager(linearLayoutManager);

        commentsViev = (ConstraintLayout) findViewById(R.id.commentsViev);
        Social = (ConstraintLayout) findViewById(R.id.S_SEND);
        controlcard = (MaterialCardView) findViewById(R.id.controlcard);
        pari_card_files = (MaterialCardView) findViewById(R.id.pari_card_files); pari_card_files.setVisibility(View.GONE);
        autor_card = (MaterialCardView) findViewById(R.id.autor_card);
        executer_card = (MaterialCardView) findViewById(R.id.executer_card);
        mainBackground = (ImageView) findViewById(R.id.mainBackground);
        // Logic pari activity (pari_id)

        controlcard.setVisibility(View.GONE);
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
                    Social.setVisibility(View.GONE);

                    // Вкладка - информация.
                    fab.setVisibility(View.GONE);
                    if(fabVisible){
                        fab.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                if (id == R.id.page_2) {
                    fab.setVisibility(View.GONE);
                    Information.setVisibility(View.GONE);
                    nocomm.setVisibility(View.GONE);
                    Social.setVisibility(View.GONE);
                    commentsViev.setVisibility(View.VISIBLE);

                    // Вкладка - комментарии.
                    clearRecurseViev();
                    loadJSONComments("https://unesell.com/api/lipari/comments.pari.php?id=" + ID);
                    return true;
                }
                if (id == R.id.page_3) {
                    fab.setVisibility(View.GONE);
                    Information.setVisibility(View.GONE);
                    nocomm.setVisibility(View.GONE);
                    commentsViev.setVisibility(View.GONE);
                    Social.setVisibility(View.VISIBLE);

                    // Вкладка - социальное.

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
                if(Long.parseLong(ID_autor) != 0){ JoinIniciator.setVisibility(View.GONE); }
                if(ID_Executor.equals(sPref.getString("ID", ""))){ JoinOpponent.setVisibility(View.GONE); }
                if(Long.parseLong(ID_Executor) != 0){ JoinOpponent.setVisibility(View.GONE); }

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
                            String backgroundImg = jsonObject.getString("backgroundImg");
                            live_stream = jsonObject.getString("live_stream");

                            String autor_id = jsonObject.getString("autor_id");
                            String executor_id = jsonObject.getString("executor_id");

                            int autor_voting = Integer.parseInt(jsonObject.getString("autor_voting"));
                            int executor_voting = Integer.parseInt(jsonObject.getString("executor_voting"));

                            int allVoite = autor_voting + executor_voting;

                            // Output info in app
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pari_name.setText(Name);
                                    pari_info.setText(Info);
                                    pari_win.setText(win);
                                    pari_lose.setText(lost);
                                    xp.setText(expiriens + " XP");
                                    TextView voitingAutor = (TextView) findViewById(R.id.voitingAutor);
                                    voitingAutor.setText(getResources().getString(R.string.voitCount) + ": " + autor_voting);
                                    TextView voitingExecutor = (TextView) findViewById(R.id.voitingExecutor);
                                    voitingExecutor.setText(getResources().getString(R.string.voitCount) + ": " + executor_voting);
                                    if (!"default".equals(backgroundImg)) {
                                        try {
                                            Glide.with(context)
                                                    .asBitmap()
                                                    .load(backgroundImg)
                                                    .apply(new RequestOptions()
                                                            .override(1000, 200) // Размер изображения
                                                            .format(DecodeFormat.PREFER_RGB_565) // Формат декодирования
                                                            .priority(Priority.HIGH) // Приоритет загрузки
                                                    )
                                                    .into(new CustomTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                            int imageWidth = resource.getWidth();
                                                            int imageHeight = resource.getHeight();

                                                            Bitmap resultBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);

                                                            for (int x = 0; x < imageWidth; x++) {
                                                                for (int y = 0; y < imageHeight; y++) {
                                                                    int pixelColor = resource.getPixel(x, y);

                                                                    double progress = (double) y / imageHeight; // Прогресс снизу вверх
                                                                    int alpha = (int) (255 * Math.pow(1 - progress, 4)); // Увеличиваем эффект прозрачности

                                                                    int newPixelColor = Color.argb(alpha, Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
                                                                    resultBitmap.setPixel(x, y, newPixelColor);
                                                                }
                                                            }

                                                            mainBackground.setImageBitmap(resultBitmap);
                                                        }

                                                        @Override
                                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                                            // Handle the case where the resource is cleared.
                                                        }
                                                    });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        //mainBackground.setColorFilter(Color.argb(128, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
                                    }else {
                                        mainBackground.setImageResource(R.drawable.m3_red);
                                    }
                                    if(Objects.equals(live_stream, "none")){
                                        MaterialCardView liveplayercard = findViewById(R.id.liveplayercard);
                                        liveplayercard.setVisibility(View.GONE);
                                    }else{
                                        MaterialCardView liveplayercard = findViewById(R.id.liveplayercard);
                                        liveplayercard.setVisibility(View.VISIBLE);
                                    }
                                    if(stasusPari.equals("search")){ status.setText(getResources().getString(R.string.status_1)); }
                                    if(stasusPari.equals("go")){ status.setText(getResources().getString(R.string.status_2)); }
                                    if(stasusPari.equals("executor_lose")){ status.setText(getResources().getString(R.string.status_3)); }
                                    if(stasusPari.equals("executor_send_check")){ status.setText(getResources().getString(R.string.status_4)); }
                                    if(stasusPari.equals("autor_win_check")){ status.setText(getResources().getString(R.string.status_5)); }
                                    if(stasusPari.equals("end")){
                                        status.setText(getResources().getString(R.string.status_6));
                                        pari_card_files.setVisibility(View.VISIBLE);
                                        loadJSONFromURL("https://unesell.com/api/lipari/files.pari.php?id=" + ID);
                                    }

                                    ImageView avatarOne = (ImageView) findViewById(R.id.UserPari_One);
                                    ImageView avatarTwo = (ImageView) findViewById(R.id.UserPari_Two);

                                    ProgressBar pVoitingAutor = (ProgressBar) findViewById(R.id.progressBar2);
                                    pVoitingAutor.setMax(allVoite); pVoitingAutor.setProgress(autor_voting);
                                    ProgressBar pVoitingExecutor = (ProgressBar) findViewById(R.id.progressBar3);
                                    pVoitingExecutor.setMax(allVoite); pVoitingExecutor.setProgress(executor_voting);

                                    if(Long.parseLong(autor_id) != 0){
                                        Picasso.get().load("https://unesell.com/data/users/avatar/" + autor_id + ".png").into(avatarOne);
                                        autor_card.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View views) {
                                                Intent intent = new Intent(context, Profile.class);
                                                intent.putExtra("user_ui", autor_id); intent.putExtra("noDestroy", "yes");
                                                context.startActivity(intent);
                                            }
                                        });
                                    }else{
                                        Picasso.get().load("https://unesell.com/assets/img/default.png").into(avatarOne);
                                    }

                                    if(Long.parseLong(executor_id) != 0){
                                        Picasso.get().load("https://unesell.com/data/users/avatar/" + executor_id + ".png").into(avatarTwo);
                                        executer_card.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View views) {
                                                Intent intent = new Intent(context, Profile.class);
                                                intent.putExtra("user_ui", executor_id); intent.putExtra("noDestroy", "yes");
                                                context.startActivity(intent);
                                            }
                                        });
                                    }else{
                                        Picasso.get().load("https://unesell.com/assets/img/default.png").into(avatarTwo);
                                    }

                                    if(Long.parseLong(executor_id) != 0 && Long.parseLong(autor_id) != 0){
                                        fab.setVisibility(View.GONE);
                                        fabVisible = false;
                                    }else{

                                    }
                                    if( autor_id.equals(sPref.getString("ID", "")) || executor_id.equals(sPref.getString("ID", ""))){
                                        if(autor_id.equals(sPref.getString("ID", ""))){
                                            Cursus = autor_id;
                                        }else {
                                            Cursus = executor_id;
                                        }

                                        // Social Card Settings
                                        controlcard.setVisibility(View.VISIBLE);
                                        ConstraintLayout waiting = (ConstraintLayout) findViewById(R.id.waiting);
                                        Button cursus = (Button) findViewById(R.id.cursus);
                                        Button send_win = (Button) findViewById(R.id.send_win);
                                        Button send_lose = (Button) findViewById(R.id.send_lose);
                                        Button send_lose2 = (Button) findViewById(R.id.send_lose2);
                                        Button check_ansver = (Button) findViewById(R.id.check_ansver);
                                        send_win.setVisibility(View.GONE);
                                        send_lose.setVisibility(View.GONE);
                                        send_lose2.setVisibility(View.GONE);
                                        check_ansver.setVisibility(View.GONE);

                                        if (Long.parseLong(autor_id) == 0 || Long.parseLong(executor_id) == 0){
                                            // Пари не началось.
                                            waiting.setVisibility(View.VISIBLE);
                                            cursus.setVisibility(View.GONE);
                                        }else{
                                            // Пари началось.
                                            waiting.setVisibility(View.GONE);
                                            cursus.setVisibility(View.VISIBLE);

                                            // Если мы автор.
                                            if(Long.parseLong(autor_id) == Long.parseLong(sPref.getString("ID", ""))){
                                                if(stasusPari.equals("autor_win_check")){
                                                    check_ansver.setVisibility(View.VISIBLE);
                                                }
                                                if(stasusPari.equals("executor_send_check")){
                                                    check_ansver.setVisibility(View.VISIBLE);
                                                }
                                            }
                                            // Если мы оппонент.
                                            if(Long.parseLong(executor_id) == Long.parseLong(sPref.getString("ID", ""))){
                                                if(stasusPari.equals("go")){
                                                    send_win.setVisibility(View.VISIBLE);
                                                    send_lose.setVisibility(View.VISIBLE);
                                                }
                                                if(stasusPari.equals("executor_lose")){
                                                    send_lose2.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }

                                        // Fab settings
                                        fab.setVisibility(View.GONE);
                                        fabVisible = false;
                                    }
                                    users_name_pari(autor_id, executor_id);

                                    fab.setVisibility(View.GONE);
                                    if(fabVisible){
                                        fab.setVisibility(View.VISIBLE);
                                    }

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
                                        voiting_user_one.setText(getResources().getString(R.string.zero_autor));
                                    }else{
                                        UserName_One.setText(autor_name);
                                        voiting_user_one.setText(autor_name);
                                    }

                                    if(executor_name == null || executor_name == "null"){
                                        UserName_Two.setText(getResources().getString(R.string.zero_executor));
                                        voiting_user_two.setText(getResources().getString(R.string.zero_executor));
                                    }else{
                                        UserName_Two.setText(executor_name);
                                        voiting_user_two.setText(executor_name);
                                    }

                                    fab.setVisibility(View.GONE);
                                    if(fabVisible){
                                        fab.setVisibility(View.VISIBLE);
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

    public void openPariBrowser(View view) {
        String url = "https://unesell.com/app/lipari/info/?id=" + ID;
        Intent openPage= new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(openPage);
    }

    public void SendExe(View view) {
        // Запуск активити подтверждения
        Intent intent = new Intent(context, CheckPari.class);
        intent.putExtra("pari_id", ID);
        context.startActivity(intent);
        finish();
    }

    public void SendExeLose(View view) {
        MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(this);
        mDialogBuilder.setTitle(getResources().getString(R.string.LoseTitle));
        mDialogBuilder.setMessage(getResources().getString(R.string.LoseSubTitle));
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.OkLoseExecute),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(android.content.DialogInterface dialog,int id) {
                                // Отправка команды поражения.
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        URL url;
                                        HttpsURLConnection connection = null;
                                        try {
                                            url = new URL("https://unesell.com/api/lipari/punishment.php?id_user=" + sPref.getString("ID", "") + "&id_post=" + ID);
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
                        })
                .setNegativeButton(getResources().getString(R.string.Cansel),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(android.content.DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        mDialogBuilder.show();
    }

    // File see
    private void  loadJSONFromURL(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("files");
                            for (int i=0;i< jsonArray.length();i++){
                                JSONObject userData=jsonArray.getJSONObject(i);
                                namefile.add(userData.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FileAdapter fileAdapter = new FileAdapter(namefile, DetailsPari.this, ID);
                        fileView.setAdapter(fileAdapter);
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
    // END LOAD FILE SEE

    public void CursesGo(View view) {
        String url = "https://unesell.com/app/cursus/?chat=" + ID;
        Intent openPage= new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(openPage);
    }

    public void OpenLiveStream(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + live_stream));
            intent.putExtra("force_fullscreen", true);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + live_stream));
            context.startActivity(intent);
        }

        /* Intent intent = new Intent(context, LivePlayer.class); intent.putExtra("StreamID", live_stream); context.startActivity(intent); */
    }

    public void CopyLink(View view) {
        String link = "https://unesell.com/app/lipari/info/?id=" + ID;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(link);
        Resources resources = context.getResources();
        String toastText = resources.getString(R.string.copy_link_toast);
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }

    public void ShareLink(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String textToShare = getString(R.string.share_message) + " " + "https://unesell.com/app/lipari/info/?id=" + ID;
        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
    }
}