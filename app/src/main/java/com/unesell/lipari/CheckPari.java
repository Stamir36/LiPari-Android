package com.unesell.lipari;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//File upload
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// ----

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

// ----

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HttpsURLConnection;

public class CheckPari extends AppCompatActivity {

    Context context;
    SharedPreferences sPref;
    ProgressBar loadingBar;
    TextView server_connect_text;
    String ID;
    ConstraintLayout S_SEND;
    MaterialCardView reward_card;
    LinearLayout check_panel_data;

    // File upload
    Button btn;
    ImageView img;
    final int KeyGallery = 100, ReadMediaImagesRequestCode = 200;
    String url_site = "https://unesell.com/";
    File file;
    Boolean fileopen = false;
    String expiriense = "0";
    String pari_no_content = "no";
    // File See
    RecyclerView fileView;
    ArrayList<String> name = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        context = this;
        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        loadingBar = findViewById(R.id.loadingBar2);
        server_connect_text = findViewById(R.id.server_connect_text2);
        S_SEND = findViewById(R.id.S_SEND);
        reward_card = findViewById(R.id.reward_card);
        loadingBar.setVisibility(View.VISIBLE);
        server_connect_text.setVisibility(View.VISIBLE);
        S_SEND.setVisibility(View.GONE);
        reward_card.setVisibility(View.GONE);
        Bundle arguments = getIntent().getExtras();
        ID = arguments.get("pari_id").toString();
        check_panel_data = findViewById(R.id.check_panel_data);
        check_panel_data.setVisibility(View.GONE);

        fileView = findViewById(R.id.fileView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        fileView.setLayoutManager(linearLayoutManager);

        loadingBar.setVisibility(View.VISIBLE);
        server_connect_text.setVisibility(View.VISIBLE);
        ServerInformation();

        // File upload
        btn = findViewById(R.id.btn_upload);
        img = findViewById(R.id.img);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = checkPermission(CheckPari.this);
                if (result) {
                    galleryIntent();
                }
            }
        });
    }

    public void ServerInformation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                HttpsURLConnection connection = null;

                try {
                    //s.miroshnichenko.mail@gmail.com
                    url = new URL("https://unesell.com/api/lipari/info.pari.php?id=" + ID);
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
                            String editing = jsonObject.getString("editing");

                            String autor_id = jsonObject.getString("autor_id");
                            String executor_id = jsonObject.getString("executor_id");

                            // Output info in app
                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    TextView Titles = (TextView) findViewById(R.id.Titles);
                                    TextView check_name_pari = (TextView) findViewById(R.id.check_name_pari);
                                    TextView check_win_pari = (TextView) findViewById(R.id.check_win_pari);
                                    TextView xp_check = (TextView) findViewById(R.id.xp_check);
                                    TextView check_edit_pari = (TextView) findViewById(R.id.check_edit_pari);

                                    if(stasusPari.equals("go")){
                                        Titles.setText(getResources().getString(R.string.check));
                                        check_name_pari.setText(Name + "\n" + Info);
                                        check_win_pari.setText(win); xp_check.setText(expiriens + " XP");
                                        reward_card.setVisibility(View.VISIBLE);
                                    }

                                    if(stasusPari.equals("executor_lose")){
                                        Titles.setText(getResources().getString(R.string.check));
                                        check_name_pari.setText(getResources().getString(R.string.check_lose) + "\n" + lost);
                                        reward_card.setVisibility(View.GONE);
                                    }

                                    if(stasusPari.equals("autor_win_check")){
                                        check_panel_data.setVisibility(View.VISIBLE);
                                        Titles.setText(getResources().getString(R.string.exe_send_check));
                                        check_name_pari.setText(Name + "\n" + Info);
                                        TextView title_reward_sub = (TextView) findViewById(R.id.title_reward_sub);
                                        title_reward_sub.setText(getResources().getString(R.string.rewardAutor));
                                        check_edit_pari.setText(editing);

                                        TextView title_reward = (TextView) findViewById(R.id.title_reward);
                                        title_reward.setText(getResources().getString(R.string.exe_reward_check));

                                        Button create = (Button) findViewById(R.id.create);
                                        create.setText(getResources().getString(R.string.check_win));

                                        check_win_pari.setText(win); xp_check.setText(expiriens + " XP");
                                        reward_card.setVisibility(View.VISIBLE);

                                        loadJSONFromURL("https://unesell.com/api/lipari/files.pari.php?id=" + ID);
                                    }

                                    if(stasusPari.equals("executor_send_check")){
                                        check_panel_data.setVisibility(View.VISIBLE);
                                        Titles.setText(getResources().getString(R.string.exe_send_check));
                                        check_name_pari.setText(Name + "\n" + Info);
                                        check_edit_pari.setText(editing);
                                        TextView title_reward_sub = (TextView) findViewById(R.id.title_reward_sub);
                                        title_reward_sub.setText(getResources().getString(R.string.reward_lose_exe));

                                        Button create = (Button) findViewById(R.id.create);
                                        create.setText(getResources().getString(R.string.check_win));

                                        check_win_pari.setText(getResources().getString(R.string.exe_lose_10_procent_win));
                                        int exp = Integer.parseInt(expiriens);
                                        int result_xp = (int) Math.ceil(exp / 10.0) * 10;
                                        expiriense = result_xp + "";
                                        xp_check.setText(result_xp + " XP");
                                        reward_card.setVisibility(View.VISIBLE);

                                        // Delete all card
                                        MaterialCardView carddetail = (MaterialCardView) findViewById(R.id.carddetail);
                                        MaterialCardView controlcard = (MaterialCardView) findViewById(R.id.controlcard);
                                        MaterialCardView filecard = (MaterialCardView) findViewById(R.id.filecard);
                                        carddetail.setVisibility(View.GONE); controlcard.setVisibility(View.GONE); filecard.setVisibility(View.GONE);
                                        pari_no_content = "yes";

                                        loadJSONFromURL("https://unesell.com/api/lipari/files.pari.php?id=" + ID);
                                    }

                                    loadingBar.setVisibility(View.GONE);
                                    server_connect_text.setVisibility(View.GONE);
                                    S_SEND.setVisibility(View.VISIBLE);
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

    public void Send(View view) {
        if(pari_no_content == "no"){
            TextInputLayout content_result = (TextInputLayout) findViewById(R.id.content_result);
            if (content_result.getEditText().getText().length() != 0 && fileopen){
                uploadResults();
            }else {
                Toast.makeText(context, getResources().getString(R.string.error_text_length), Toast.LENGTH_SHORT).show();
            }
        }else{
            CloaseChellenge();
        }
    }

    public void CloaseChellenge() {
        AsyncHttpClient myClient = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        TextInputLayout content_result = (TextInputLayout) findViewById(R.id.content_result);
        loadingBar.setVisibility(View.VISIBLE);
        server_connect_text.setVisibility(View.VISIBLE);
        server_connect_text.setText(getResources().getString(R.string.send_upload));
        S_SEND.setVisibility(View.GONE);

        params.put("id", ID);
        params.put("content", "Оппонент проиграл и понёс наказание");
        params.put("expiriense", expiriense);

        myClient.post(url_site + "app/lipari/check/end.pari.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (jsonObject.getString("status").equals("success")) {
                        Intent intent = new Intent(context, DetailsPari.class);
                        intent.putExtra("pari_id", ID);
                        context.startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Ошибка ответа.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Intent intent = new Intent(context, DetailsPari.class);
                intent.putExtra("pari_id", ID);
                context.startActivity(intent);
                finish();
            }

        });
    }

    public void uploadResults() {
        AsyncHttpClient myClient = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        TextInputLayout content_result = (TextInputLayout) findViewById(R.id.content_result);
        loadingBar.setVisibility(View.VISIBLE);
        server_connect_text.setVisibility(View.VISIBLE);
        server_connect_text.setText(getResources().getString(R.string.send_upload));
        S_SEND.setVisibility(View.GONE);

        try {
            params.put("id", ID);
            params.put("content", content_result.getEditText().getText().toString().replace(" ", "%20"));
            params.put("file", file);
        } catch (java.io.FileNotFoundException e) {

        }
        myClient.post(url_site + "data/files/lipari/lipari.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    if (jsonObject.getString("status").equals("success")) {
                        Intent intent = new Intent(context, DetailsPari.class);
                        intent.putExtra("pari_id", ID);
                        context.startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Ошибка ответа.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(), new String(responseBody), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == KeyGallery) {
                if (data == null) {
                    return;
                } else {
                    Uri selectedImageUri = data.getData();
                    img.setImageURI(selectedImageUri);
                    file = new File(getPathFromUri(selectedImageUri));
                    fileopen = true;
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    private static final int REQUEST_PICK_IMAGE = 102;

    private void galleryIntent() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Используем старый метод для версий Android от 6 до 12 (исключительно)
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), KeyGallery);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Используем новый метод для Android 13 и выше
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, KeyGallery);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ReadMediaImagesRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galleryIntent();
            } else {
                Toast.makeText(getBaseContext(), "Permission problem!", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean checkPermission(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Используем старый метод для версий Android до 12 (исключительно)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionRationaleDialog(context, Manifest.permission.READ_EXTERNAL_STORAGE, ReadMediaImagesRequestCode);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ReadMediaImagesRequestCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            // Используем новый метод для Android 13 и выше
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_MEDIA_IMAGES)) {
                    showPermissionRationaleDialog(context, Manifest.permission.READ_MEDIA_IMAGES, ReadMediaImagesRequestCode);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, ReadMediaImagesRequestCode);
                }
                return false;
            } else {
                return true;
            }
        }
    }

    private void showPermissionRationaleDialog(Context context, String permission, int requestCode) {
        MaterialAlertDialogBuilder mDialogBuilder = new MaterialAlertDialogBuilder(context);
        mDialogBuilder.setTitle(context.getResources().getString(R.string.manifestPerm));
        if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mDialogBuilder.setMessage(context.getResources().getString(R.string.manifest_perm_storage));
        } else if (permission.equals(Manifest.permission.READ_MEDIA_IMAGES)) {
            mDialogBuilder.setMessage(context.getResources().getString(R.string.manifest_perm_storage));
        }
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.OkLoseExecute),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.Cansel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                                name.add(userData.getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FileAdapter fileAdapter = new FileAdapter(name, CheckPari.this, ID);
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

    // END LOAD FILE SEE
}