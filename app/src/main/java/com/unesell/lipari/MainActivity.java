package com.unesell.lipari;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;
import androidx.fragment.app.FragmentManager;

import com.squareup.picasso.Target;
import com.unesell.lipari.databinding.ActivityMainBinding;
import android.view.Menu;
import android.view.MenuItem;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import android.content.Context;
import com.google.android.material.navigation.NavigationView;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import com.squareup.picasso.Picasso;

import javax.net.ssl.HttpsURLConnection;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    private static final String JSON_URL = "https://unesell.com/api/lipari/all.pari.json.php"; // UTF-8
    ListView listView;
    Chip chipAll; Chip chipMy;
    SharedPreferences sPref;

    RecyclerView recyclerView;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> info = new ArrayList<>();
    ArrayList<String> xp = new ArrayList<>();
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> paribackground = new ArrayList<>();
    ArrayList<String> ID = new ArrayList<>();
    Context context;
    ImageView avatar;
    NavigationView nav_view;
    DrawerLayout drawerLayout;

    private boolean isCardExpanded = false;
    private boolean isIconRotated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_view.getMenu().clear();
        View navContent = LayoutInflater.from(this).inflate(R.layout.main_navmenu, nav_view, false);
        nav_view.addView(navContent);

        // Проверка на авторизацию
        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        String ID = sPref.getString("ID", "");
        avatar = (ImageView) findViewById(R.id.Avatar);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        MaterialCardView mainPariCard = findViewById(R.id.MainPariCard);

        if(ID.equals("null") || ID == ""){
            Intent intent = new Intent(context, SplashScreen.class);
            startActivity(intent);
            finish();
        }else{
            TextView name = (TextView) findViewById(R.id.UserName);
            name.setText(sPref.getString("NAME", ""));

            recyclerView = findViewById(R.id.recyclerView);
            chipAll = (Chip) findViewById(R.id.chip_1);
            chipMy = (Chip) findViewById(R.id.chip2);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createPostIntent = new Intent(context, Create_Pari_Post.class);
                    startActivity(createPostIntent);
                }
            });

            chipAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chipAll.setChecked(true); clearRecurseViev();
                    chipMy.setChecked(false);
                    loadJSONFromURL(JSON_URL);
                }
            });

            chipMy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chipMy.setChecked(true); clearRecurseViev();
                    chipAll.setChecked(false);
                    loadJSONFromURL(JSON_URL + "?sort=" + ID);
                }
            });

            BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
            bottomAppBar.setOnMenuItemClickListener(new androidx.appcompat.widget.Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.main:
                            // Вызов профиля
                            ModalSheatOpen OpenLogin = ModalSheatOpen.newInstance();
                            OpenLogin.show(getSupportFragmentManager(), "open_login");
                            break;
                        case R.id.nav_open:
                             // Вызов меню
                            drawerLayout.openDrawer(GravityCompat.START);
                            break;
                    }
                    return false;
                }
            });

            MaterialButton btnFullscreen = findViewById(R.id.btnfullscren);
            MaterialCardView levelCard = findViewById(R.id.levelCard);
            btnFullscreen.setOnClickListener(view -> {
                animateCardExpansion();
                animateIconRotation(btnFullscreen);
            });

            new Handler().postDelayed(() -> {
                animateMarginChange(levelCard, -100);
            }, 3000);

            loadJSONFromURL(JSON_URL);

            UpdateData();
        }
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
                            String avatar_u = jsonObject.getString("avatar");
                            String xp_now = jsonObject.getString("xp_now");
                            String money = jsonObject.getString("money");
                            String premium = jsonObject.getString("premium");
                            String background = jsonObject.getString("imgBackground");

                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    TextView name = (TextView) findViewById(R.id.UserName);
                                    name.setText(nameAccount);

                                    TextView moneyAccount = (TextView) findViewById(R.id.moneyAccount);
                                    TextView level_now = (TextView) findViewById(R.id.level_now);
                                    TextView xp_main = (TextView) findViewById(R.id.xp_main);
                                    ProgressBar level_progress = (ProgressBar) findViewById(R.id.level_progress);
                                    moneyAccount.setText(money + " " + context.getResources().getString(R.string.money));

                                    if (premium.equals("none")){
                                        MaterialCardView pro_account_card = (MaterialCardView) findViewById(R.id.pro_account_card);
                                        pro_account_card.setVisibility(View.GONE);
                                    }

                                    xp_main.setText(xp_now + " XP");

                                    level_progress.setMax(1000);
                                    level_progress.setProgress(Integer.parseInt( xp_now.substring(xp_now.length() - 3, xp_now.length())));
                                    String level = xp_now.substring(0, xp_now.length() - 3);
                                    if (level.length() == 0){ level = "0"; }
                                    level_now.setText(getResources().getString(R.string.level) + ": " + level);

                                    Picasso.get().load("https://unesell.com/data/users/avatar/" + avatar_u).into(avatar);

                                    ImageView imageAccountBack = findViewById(R.id.imageAccountBack);

                                    try {
                                        Glide.with(context)
                                                .asBitmap()
                                                .load(background)
                                                .apply(new RequestOptions()
                                                        .override(200, 200) // Размер изображения
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
                                                                int alpha = (int) (255 * Math.pow((float) x / imageWidth, 3)); // Рассчитываем альфа-канал

                                                                int newPixelColor = Color.argb(alpha, Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
                                                                resultBitmap.setPixel(x, y, newPixelColor);
                                                            }
                                                        }

                                                        imageAccountBack.setImageBitmap(resultBitmap);
                                                    }

                                                    @Override
                                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                                        // Handle the case where the resource is cleared.
                                                    }
                                                });
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    SharedPreferences.Editor ed = sPref.edit();
                                    ed.putString("avatar_id", avatar_u);
                                    ed.commit();

                                    if(xp_now.isEmpty()){
                                        MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(context);
                                        mDialogBuilder.setTitle(getResources().getString(R.string.welcome_modal_title));
                                        mDialogBuilder.setMessage(getResources().getString(R.string.welcome_modal_sutitle));
                                        mDialogBuilder
                                                .setCancelable(false)
                                                .setPositiveButton(getResources().getString(R.string.man),
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                SexChence("man");
                                                                dialog.cancel();
                                                            }
                                                        })
                                                .setNegativeButton(getResources().getString(R.string.woomen),
                                                        new android.content.DialogInterface.OnClickListener() {
                                                            public void onClick(android.content.DialogInterface dialog,int id) {
                                                                SexChence("woman");
                                                                dialog.cancel();
                                                            }
                                                        });

                                        mDialogBuilder.show();
                                    }
                                }
                            });

                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putString("NAME", nameAccount);
                            ed.commit();
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

        TextView textHello = findViewById(R.id.textHello);
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hourOfDay >= 6 && hourOfDay < 12) {
            greeting = getResources().getString(R.string.hello_1);
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greeting = getResources().getString(R.string.hello_2);
        } else {
            greeting = getResources().getString(R.string.hello_3);
        }

        textHello.setText(greeting);
    }

    public void SexChence(String sex)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;
                try {
                    url = new URL("https://unesell.com/api/lipari/chenge.sex.php?id=" + sPref.getString("ID", "") + "&sex=" + sex);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.getResponseCode();

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

    private void  loadJSONFromURL(String url){
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ListView.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String>(){
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("post");
                            for (int i=0;i< jsonArray.length();i++){
                                JSONObject userData=jsonArray.getJSONObject(i);
                                ID.add(userData.getString("identify"));
                                name.add(userData.getString("Name"));
                                info.add(userData.getString("Info").replace("<br/>", "\n"));
                                xp.add(userData.getString("xp_boost") + " XP");
                                status.add(userData.getString("stasus"));
                                paribackground.add(userData.getString("backgroundImg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HelperAdapter helperAdapter = new HelperAdapter(name, info, xp, ID, status, paribackground, MainActivity.this);
                        recyclerView.setAdapter(helperAdapter);
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
        name.clear(); info.clear(); xp.clear(); ID.clear(); status.clear();
    }

    public void closeMenu(View view) {
        nav_view.setVisibility(View.GONE);
    }

    public void Open_cursus(View view) {
        String url = "https://unesell.com/app/cursus/";
        Intent openPage= new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(openPage);
    }

    public void Open_store(View view) {
        Intent store = new Intent(context, Store.class);
        startActivity(store);
    }

    private void animateCardExpansion() {
        MaterialCardView mainPariCard = findViewById(R.id.MainPariCard);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mainPariCard.getLayoutParams();

        int targetTopMarginPx = isCardExpanded ? (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics()) : 0;

        ValueAnimator animator = ValueAnimator.ofInt(params.topMargin, targetTopMarginPx);
        animator.setDuration(500);
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            params.topMargin = animatedValue;
            mainPariCard.setLayoutParams(params);
            mainPariCard.requestLayout(); // Force layout update
        });
        animator.start();
        isCardExpanded = !isCardExpanded;
    }

    private void animateIconRotation(MaterialButton btnFullscreen) {
        ObjectAnimator rotationAnimator;

        if (isIconRotated) {
            rotationAnimator = ObjectAnimator.ofFloat(btnFullscreen, "rotation", 45, 0);
        } else {
            rotationAnimator = ObjectAnimator.ofFloat(btnFullscreen, "rotation", 0, 45);
        }

        rotationAnimator.setDuration(500);
        rotationAnimator.start();
        isIconRotated = !isIconRotated;
    }

    private void animateMarginChange(View view, int targetMarginDp) {
        int targetMarginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, targetMarginDp, getResources().getDisplayMetrics());

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(params.bottomMargin, targetMarginPx);
        animator.setDuration(500);

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            params.bottomMargin = animatedValue;
            view.setLayoutParams(params);
            view.requestLayout();
        });

        animator.start();
    }
}