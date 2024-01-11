package com.unesell.lipari;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context context;
    private List<ImageModel> images;

    public ImageAdapter(Context context, List<ImageModel> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel image = images.get(position);
        Glide.with(holder.imageView.getContext()).load(image.getImageUrl()).into(holder.imageView);
        holder.ImageInfo.setText(image.getAltDescription());

        try {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomSheetDialog(image.getImageUrl());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    SharedPreferences sPref;
    private JSONArray jsonArray;
    String selectedIdentify = "";
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;
    String Money;
    String ID;
    String IMGUrl;
    private void showBottomSheetDialog(String imageUrl) {
        bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_image, null);
        ImageView bottomSheetImageView = bottomSheetView.findViewById(R.id.bottomSheetImageView);
        Button bottomSheetBuyButton = bottomSheetView.findViewById(R.id.bottomSheetBuyButton);
        Spinner spinner = bottomSheetView.findViewById(R.id.spinner);
        IMGUrl = imageUrl;

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
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

                                double progress = (double) y / imageHeight;
                                int alpha = (int) (255 * Math.pow(1 - progress, 3));

                                int newPixelColor = Color.argb(alpha, Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
                                resultBitmap.setPixel(x, y, newPixelColor);
                            }
                        }

                        bottomSheetImageView.setImageBitmap(resultBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle the case where the resource is cleared.
                    }
                });

        sPref = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        ID = sPref.getString("ID", "");

        loadDataForSpinner(spinner, ID);

        // Обработка нажатия на кнопку "Купить"
        bottomSheetBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData();
                int coins = Integer.parseInt(Money);

                if (coins < 10) {
                    showAlertDialog();
                } else {
                    BuyBackground();
                    showPurchaseConfirmationDialog();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (jsonArray != null && jsonArray.length() >= position + 1) {
                    JSONObject selectedObject = jsonArray.optJSONObject(position);
                    if (selectedObject != null) {
                        selectedIdentify = selectedObject.optString("identify");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Действия при отсутствии выбора
            }
        });

        bottomSheetDialog = new BottomSheetDialog(context);

        LinearLayout Content = bottomSheetView.findViewById(R.id.Content);
        LinearLayout Loader = bottomSheetView.findViewById(R.id.Loader);
        Content.setVisibility(View.GONE);
        Loader.setVisibility(View.VISIBLE);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void BuyBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    URL moneyUrl = new URL("https://unesell.com/api/lipari/account.lipari.php?id=" + sPref.getString("ID", ""));
                    HttpsURLConnection moneyConnection = (HttpsURLConnection) moneyUrl.openConnection();
                    moneyConnection.getResponseCode();
                    if (moneyConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        String moneyResponse = new BufferedReader(new InputStreamReader(moneyConnection.getInputStream())).readLine();
                        try {
                            JSONObject moneyJsonObject = new JSONObject(moneyResponse);
                            String money = moneyJsonObject.getString("money");
                            Money = money;

                            int coins = Integer.parseInt(Money);

                            if (coins < 10) {
                                showAlertDialog();
                            } else {
                                URL url = new URL("https://unesell.com/api/lipari/editBackground.php" +
                                        "?id=" + selectedIdentify +
                                        "&link=" + IMGUrl +
                                        "&money=" + (coins - 10) +
                                        "&user_id=" + ID);

                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");

                                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                StringBuilder response = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                reader.close();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void showPurchaseConfirmationDialog() {
        View confirmationView = LayoutInflater.from(context).inflate(R.layout.purchase_confirmation_layout, null);

        new MaterialAlertDialogBuilder(context)
                .setCustomTitle(confirmationView)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.open_challenge, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, DetailsPari.class);
                        intent.putExtra("pari_id", selectedIdentify);
                        context.startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void showAlertDialog() {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.not_enough_coins_title)
                .setMessage(R.string.not_enough_coins_message)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void UpdateData(){
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
                            Money = money;

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

    @SuppressLint("StaticFieldLeak")
    private void loadDataForSpinner(Spinner spinner, String ID) {
        new AsyncTask<Void, Void, Pair<List<String>, JSONArray>>() {
            @Override
            protected Pair<List<String>, JSONArray> doInBackground(Void... voids) {
                List<String> spinnerDataList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray();
                URL url;
                HttpsURLConnection connection;

                try {
                    url = new URL("https://unesell.com/api/lipari/my_challenge_list.php?id=" + ID);
                    connection = (HttpsURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemName = jsonObject.optString("Name");
                        spinnerDataList.add(itemName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UpdateData();
                return new Pair<>(spinnerDataList, jsonArray);
            }

            protected void onPostExecute(Pair<List<String>, JSONArray> result) {
                super.onPostExecute(result);
                List<String> spinnerDataList = result.first;
                jsonArray = result.second;

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerDataList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);

                LinearLayout YesChellenge = bottomSheetView.findViewById(R.id.YesChellenge);
                LinearLayout NoChellenge = bottomSheetView.findViewById(R.id.NoChellenge);

                if (spinnerDataList.isEmpty()) {
                    YesChellenge.setVisibility(View.GONE);
                    NoChellenge.setVisibility(View.VISIBLE);
                } else {
                    YesChellenge.setVisibility(View.VISIBLE);
                    NoChellenge.setVisibility(View.GONE);
                }

                bottomSheetDialog.cancel();

                LinearLayout Content = bottomSheetView.findViewById(R.id.Content);
                LinearLayout Loader = bottomSheetView.findViewById(R.id.Loader);
                Content.setVisibility(View.VISIBLE);
                Loader.setVisibility(View.GONE);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }


        }.execute();
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView ImageInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            ImageInfo = itemView.findViewById(R.id.ImageInfo);
        }
    }
}
