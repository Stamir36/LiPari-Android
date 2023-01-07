package com.unesell.lipari;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HelperAdapter extends RecyclerView.Adapter< HelperAdapter.MyViewClass > {
    ArrayList<String> name;
    ArrayList<String> info;
    ArrayList<String> xp;
    ArrayList<String> ID;
    ArrayList<String> status;
    Context context;

    public HelperAdapter(ArrayList<String> name, ArrayList<String> info, ArrayList<String> xp, ArrayList<String> ID, ArrayList<String> status,  Context context) {
        this.name = name;
        this.info = info;
        this.xp = xp;
        this.ID = ID;
        this.status = status;
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
    public void onBindViewHolder(@NonNull MyViewClass holder, int position) {
        holder.name.setText(name.get(position));
        holder.info.setText(info.get(position));
        holder.xp.setText(xp.get(position));

        if(status.get(position).equals("search")){ holder.status.setText(context.getResources().getString(R.string.status_1)); }
        if(status.get(position).equals("go")){ holder.status.setText(context.getResources().getString(R.string.status_2)); }
        if(status.get(position).equals("executor_lose")){ holder.status.setText(context.getResources().getString(R.string.status_3)); }
        if(status.get(position).equals("executor_send_check")){ holder.status.setText(context.getResources().getString(R.string.status_4)); }
        if(status.get(position).equals("autor_win_check")){ holder.status.setText(context.getResources().getString(R.string.status_5)); }
        if(status.get(position).equals("end")){ holder.status.setText(context.getResources().getString(R.string.status_6)); }

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
        public MyViewClass(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
            xp = itemView.findViewById(R.id.xp);
            status = itemView.findViewById(R.id.statusText);

        }
    }
}