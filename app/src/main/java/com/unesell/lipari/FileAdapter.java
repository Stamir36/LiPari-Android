package com.unesell.lipari;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewClass> {
    ArrayList<String> name;
    Context context;
    String ID;

    public FileAdapter(ArrayList<String> name, Context context, String ID) {
        this.name = name;
        this.context = context;
        this.ID = ID;
    }

    @NonNull
    @Override
    public MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_resurce ,parent,false);
        MyViewClass myViewClass=new MyViewClass(view);
        return myViewClass;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewClass holder, int position) {
        holder.name.setText(name.get(position));
        Picasso.get().load("https://unesell.com/data/files/lipari/" + ID + "/" + name.get(position)).into(holder.img);

        holder.filecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageViewGallary.class);
                intent.putExtra("link", "https://unesell.com/data/files/lipari/" + ID + "/" + name.get(position) );
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
        ImageView img;
        MaterialCardView filecard;

        public MyViewClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.imgFile);
            filecard = itemView.findViewById(R.id.filecard);
        }
    }
}
