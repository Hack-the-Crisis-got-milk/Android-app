package com.hackthecrisis.gotmilk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.MyViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(Shop shop);
    }

    private ArrayList<Shop> shops;
    private Context context;

    @NonNull
    private ShopListAdapter.OnItemCheckListener onItemCheckListener;

    public ShopListAdapter(ArrayList<Shop> shops, Context context, @NonNull ShopListAdapter.OnItemCheckListener onItemCheckListener) {
        this.shops = shops;
        this.context = context;
        this.onItemCheckListener = onItemCheckListener;
    }

    public void update(ArrayList<Shop> shopsArrayList) {
        shops.clear();
        shops.addAll(shopsArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShopListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopListAdapter.MyViewHolder holder, int position) {
        holder.shopName.setText(shops.get(position).getName());
        if(shops.get(position).isOpen_now()) {
            holder.status.setText(context.getString(R.string.shop_opened));
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorShopOpened));
        } else {
            holder.status.setText(context.getString(R.string.shop_closed));
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorShopClosed));
        }

        holder.type.setText(shops.get(position).getShop_type());

        Glide.with(context)
                .load(shops.get(position).getPhoto())
                .centerCrop()
                .into(holder.photo);

        holder.shop_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCheckListener.onItemCheck(shops.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView shopName;
        TextView status;
        TextView type;
        ImageView photo;
        Button shop_feedback;

        public MyViewHolder(View view) {
            super(view);
            shopName = view.findViewById(R.id.shop_name);
            status = view.findViewById(R.id.shop_open_status);
            type = view.findViewById(R.id.shop_type);
            photo = view.findViewById(R.id.shop_photo);
            shop_feedback = view.findViewById(R.id.shop_feedback);
        }
    }
}
