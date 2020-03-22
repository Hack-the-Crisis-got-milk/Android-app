package com.hackthecrisis.gotmilk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hackthecrisis.gotmilk.R;
import com.hackthecrisis.gotmilk.model.Feedback;
import com.hackthecrisis.gotmilk.model.ItemGroup;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.MyViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(ItemGroup item);
        void onItemUncheck(ItemGroup item);
    }

    private ArrayList<Feedback> feedbacks;
    private ArrayList<ItemGroup> itemGroups;
    private Context context;

    @NonNull
    private FeedbackAdapter.OnItemCheckListener onItemCheckListener;

    public FeedbackAdapter(ArrayList<Feedback> feedbacks, ArrayList<ItemGroup> itemGroups, Context context, @NonNull FeedbackAdapter.OnItemCheckListener onItemCheckListener) {
        this.feedbacks = feedbacks;
        this.itemGroups = itemGroups;
        this.context = context;
        this.onItemCheckListener = onItemCheckListener;
    }

    public void update(ArrayList<Feedback> feedbackArrayList) {
        feedbacks.clear();
        feedbacks.addAll(feedbackArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback_item, null);
        return new FeedbackAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.MyViewHolder holder, int position) {
        if(feedbacks != null) {
            for(Feedback feedback: feedbacks) {
                if(feedback.getItem_group_id().equals(itemGroups.get(position).getId())) {
                    if(feedback.getType().equals("availability")) {
                        if(feedback.getValue().equals("available"))
                            holder.available.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
                        else if(feedback.getValue().equals("unavailable"))
                            holder.available.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_clear_red));
                    }
                }
            }
        }

        Glide.with(context)
                .load(itemGroups.get(position).getIcon())
                .centerCrop()
                .into(holder.imageView);

        holder.name.setText(itemGroups.get(position).getName());

        holder.thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView thumbsUp = v.findViewById(R.id.thumbs_up);
                ImageView thumbsDown = v.findViewById(R.id.thumbs_down);
                onItemCheckListener.onItemCheck(itemGroups.get(position));
                holder.thumbsUp.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                holder.thumbsDown.setColorFilter(ContextCompat.getColor(context, R.color.colorGrey));
            }
        });

        holder.thumbsDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView thumbsDown = v.findViewById(R.id.thumbs_down);
                ImageView thumbsUp = v.findViewById(R.id.thumbs_up);
                onItemCheckListener.onItemUncheck(itemGroups.get(position));
                holder.thumbsDown.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent));
                holder.thumbsUp.setColorFilter(ContextCompat.getColor(context, R.color.colorGrey));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return itemGroups.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        ImageView available;
        ImageView thumbsUp;
        ImageView thumbsDown;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.item_image);
            name = view.findViewById(R.id.item_feedback_name);
            available = view.findViewById(R.id.item_available);
            thumbsUp = view.findViewById(R.id.thumbs_up);
            thumbsDown = view.findViewById(R.id.thumbs_down);
        }
    }
}
