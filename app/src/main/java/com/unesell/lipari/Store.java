package com.unesell.lipari;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.search.SearchBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Store extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList;
    SharedPreferences sPref;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        sPref = getSharedPreferences("Account", MODE_PRIVATE);
        context = this;
        recyclerView = findViewById(R.id.recycler_view);

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(imageAdapter);

        TextInputEditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString();
                if (!query.isEmpty()){
                    performImageSearch(query);
                }else{
                    imageList.clear();
                    imageAdapter.notifyDataSetChanged();
                    TextView end_txt = findViewById(R.id.end_txt);
                    end_txt.setVisibility(View.GONE);
                    @SuppressLint("CutPasteId") RecyclerView recycler_view = findViewById(R.id.recycler_view);
                    recycler_view.setVisibility(View.GONE);
                    ConstraintLayout main_store_title = findViewById(R.id.main_store_title);
                    main_store_title.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
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
                                    Button openAdMoney = (Button) findViewById(R.id.openAdMoney);
                                    openAdMoney.setText(money + " " + context.getResources().getString(R.string.money));
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

    public void openRewardAd(View view) {
        Intent addMoney = new Intent(this, RewardActivity.class);
        startActivity(addMoney);
    }

    @SuppressLint("SetTextI18n")
    public void searchCats(View view) {
        TextInputEditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.setText("cats");
        performImageSearch("cats");
    }

    @SuppressLint("SetTextI18n")
    public void searchAbstract(View view) {
        TextInputEditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.setText("abstract");
        performImageSearch("abstract");
    }

    @SuppressLint("SetTextI18n")
    public void searchNature(View view) {
        TextInputEditText searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.setText("nature");
        performImageSearch("nature");
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchImagesTask extends AsyncTask<String, Void, List<ImageModel>> {

        @Override
        protected List<ImageModel> doInBackground(String... params) {
            List<ImageModel> images = new ArrayList<>();
            String query = params[0];
            try {
                URL url = new URL("https://api.unsplash.com/search/photos?page=1&per_page=30&query=" + query + "&client_id=NaHFk6fEWYCWjF10KF4L9EiplttQNKmTUu6rcLJ9uLc");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray results = jsonResponse.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject imageObject = results.getJSONObject(i);
                    String imageUrl = imageObject.getJSONObject("urls").getString("regular");
                    String altDescription = imageObject.optString("alt_description", "No description available");
                    images.add(new ImageModel(imageUrl, altDescription));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return images;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(List<ImageModel> images) {
            super.onPostExecute(images);
            imageList.addAll(images);
            imageAdapter.notifyDataSetChanged();
            TextView end_txt = findViewById(R.id.end_txt);
            end_txt.setVisibility(View.VISIBLE);
            RecyclerView recycler_view = findViewById(R.id.recycler_view);
            recycler_view.setVisibility(View.VISIBLE);
            ConstraintLayout main_store_title = findViewById(R.id.main_store_title);
            main_store_title.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void performImageSearch(String query) {
        imageList.clear();
        imageAdapter.notifyDataSetChanged();
        new FetchImagesTask().execute(query);
        TextView end_txt = findViewById(R.id.end_txt);
        end_txt.setVisibility(View.GONE);
    }

    public void openStoreInWeb(View view) {
        String url = "https://unesell.com/app/lipari/store/";
        Intent openPage= new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
        startActivity(openPage);
    }
}