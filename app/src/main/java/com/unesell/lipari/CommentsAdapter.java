package com.unesell.lipari;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewClass> {
    ArrayList<String> name;
    ArrayList<String> comments;
    ArrayList<String> avatar;
    ArrayList<String> date;
    Context context;

    public CommentsAdapter(ArrayList<String> name, ArrayList<String> comments, ArrayList<String> avatar, ArrayList<String> date,  Context context) {
        this.name = name;
        this.comments = comments;
        this.avatar = avatar;
        this.date = date;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_resurce ,parent,false);
        MyViewClass myViewClass=new MyViewClass(view);
        return myViewClass;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewClass holder, int position) {
        holder.name.setText(name.get(position));
        holder.comments.setText(comments.get(position));
        holder.date.setText(date.get(position));
        Picasso.get().load("https://unesell.com/data/users/avatar/" + avatar.get(position) + ".png").into(holder.avatars);
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewClass extends RecyclerView.ViewHolder{
        TextView name;
        TextView comments;
        TextView avatar;
        TextView date;
        ImageView avatars;

        public MyViewClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            comments = itemView.findViewById(R.id.info);
            date = itemView.findViewById(R.id.statusText);
            avatars = itemView.findViewById(R.id.avatar);
        }
    }
}
