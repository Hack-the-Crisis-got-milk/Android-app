package com.hackthecrisis.gotmilk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hackthecrisis.gotmilk.R;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;

import java.util.ArrayList;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.MyViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(ItemGroup item);
        void onItemUncheck(ItemGroup item);
    }

    private ArrayList<ItemGroup> itemGroups;
    private Context context;

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    public FilterListAdapter(ArrayList<ItemGroup> itemGroups, Context context, @NonNull OnItemCheckListener onItemCheckListener) {
        this.itemGroups = itemGroups;
        this.context = context;
        this.onItemCheckListener = onItemCheckListener;
    }

    public void update(ArrayList<ItemGroup> itemGroupArrayList) {
        itemGroups.clear();
        itemGroups.addAll(itemGroupArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_filter_item, null);
        return new FilterListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterListAdapter.MyViewHolder holder, int position) {
        Glide.with(context)
                .load(itemGroups.get(position).getIcon())
                .centerCrop()
                .into(holder.imageView);

        holder.checkBox.setText(itemGroups.get(position).getName());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked())
                    onItemCheckListener.onItemCheck(itemGroups.get(position));
                else
                    onItemCheckListener.onItemUncheck(itemGroups.get(position));
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
        CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.item_image);
            checkBox = view.findViewById(R.id.item_filter);
        }
    }
}

