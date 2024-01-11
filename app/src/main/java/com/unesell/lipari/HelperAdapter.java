package com.unesell.lipari;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Objects;

public class HelperAdapter extends RecyclerView.Adapter< HelperAdapter.MyViewClass > {
    ArrayList<String> name;
    ArrayList<String> info;
    ArrayList<String> xp;
    ArrayList<String> ID;
    ArrayList<String> status;
    ArrayList<String> paribackground;
    Context context;

    public HelperAdapter(ArrayList<String> name, ArrayList<String> info, ArrayList<String> xp, ArrayList<String> ID, ArrayList<String> status, ArrayList<String> paribackground, Context context) {
        this.name = name;
        this.info = info;
        this.xp = xp;
        this.ID = ID;
        this.status = status;
        this.paribackground = paribackground;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_resurce ,parent,false);
        MyViewClass myViewClass=new MyViewClass(view);
        return myViewClass;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewClass holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(name.get(position));
        holder.info.setText(info.get(position));
        holder.xp.setText(xp.get(position));

        if(status.get(position).equals("search")){ holder.status.setText(context.getResources().getString(R.string.status_1)); }
        if(status.get(position).equals("go")){ holder.status.setText(context.getResources().getString(R.string.status_2)); }
        if(status.get(position).equals("executor_lose")){ holder.status.setText(context.getResources().getString(R.string.status_3)); }
        if(status.get(position).equals("executor_send_check")){ holder.status.setText(context.getResources().getString(R.string.status_4)); }
        if(status.get(position).equals("autor_win_check")){ holder.status.setText(context.getResources().getString(R.string.status_5)); }
        if(status.get(position).equals("end")){ holder.status.setText(context.getResources().getString(R.string.status_6)); }

        if (!Objects.equals(paribackground.get(position), "default")){
            try {
                Glide.with(context)
                        .asBitmap()
                        .load(paribackground.get(position))
                        .apply(new RequestOptions()
                                .override(200, 100) // Размер изображения
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
                                        int alpha = (int) (255 * Math.pow(1 - progress, 5)); // Увеличиваем эффект прозрачности

                                        int newPixelColor = Color.argb(alpha, Color.red(pixelColor), Color.green(pixelColor), Color.blue(pixelColor));
                                        resultBitmap.setPixel(x, y, newPixelColor);
                                    }
                                }

                                holder.paribackground.setImageBitmap(resultBitmap);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Handle the case where the resource is cleared.
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsPari.class);
                intent.putExtra("pari_id", ID.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewClass extends RecyclerView.ViewHolder{
        TextView name;
        TextView info;
        TextView xp;
        TextView status;
        ImageView paribackground;
        public MyViewClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
            xp = itemView.findViewById(R.id.xp);
            status = itemView.findViewById(R.id.statusText);
            paribackground = itemView.findViewById(R.id.paribackground);
        }
    }
}