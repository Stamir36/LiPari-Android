package com.unesell.lipari;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> rewardList = new ArrayList<>();

    public void setRewardList(List<Reward> rewardList) {
        this.rewardList = rewardList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_card, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        holder.bind(reward);
    }

    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public class RewardViewHolder extends RecyclerView.ViewHolder {

        private ImageView iconImageView;
        private TextView nameTextView;
        private TextView infoTextView;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.reward_icon);
            nameTextView = itemView.findViewById(R.id.reward_name);
            infoTextView = itemView.findViewById(R.id.reward_info);
        }

        public void bind(Reward reward) {
            Picasso.get().load("https://unesell.com/" + reward.getIcon()).into(iconImageView);
            nameTextView.setText(reward.getName());
            infoTextView.setText(reward.getInfo());
        }
    }
}