package com.hackthecrisis.gotmilk.ui.grocery;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.R;
import com.hackthecrisis.gotmilk.adapter.FilterListAdapter;
import com.hackthecrisis.gotmilk.adapter.ShopListAdapter;
import com.hackthecrisis.gotmilk.model.Filter;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroceryFragment extends Fragment {

    private GroceryViewModel groceryViewModel;

    private FloatingActionButton showFilterbutton;
    private ImageView filterDialogCloseButton;
    private Button applyFilterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private RecyclerView shopRecyclerView;
    private RecyclerView itemGroupRecyclerView;
    private ShopListAdapter shopListAdapter;
    private FilterListAdapter filterListAdapter;

    private RadioButton busy;
    private RadioButton average;
    private RadioButton empty;

    private EditText searchBar;

    private ArrayList<Shop> shopArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groceryViewModel =
                ViewModelProviders.of(this).get(GroceryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_grocery, container, false);

        showFilterbutton = root.findViewById(R.id.filter_button);
        swipeRefreshLayout = root.findViewById(R.id.shops_refresh);
        shopRecyclerView = root.findViewById(R.id.shops_recyclerview);
        progressBar = root.findViewById(R.id.shops_list_progressbar);
        searchBar = root.findViewById(R.id.grocery_search_input);
        filter();

        handleButtonClick();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                groceryViewModel.getShopList();
            }
        });

        groceryViewModel.getShopList();
        observeShopList();

        return root;
    }

    private ArrayList<Filter> filters;

    private void showFilterDialog() {
        filters = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.shop_filter_dialog, null);
        filterDialogCloseButton = view.findViewById(R.id.close_filter_dialog);
        applyFilterButton = view.findViewById(R.id.apply_filter_button);

        busy = view.findViewById(R.id.busy);
        average = view.findViewById(R.id.average);
        empty = view.findViewById(R.id.empty);
        handleRadioButtons();

        builder.setView(view);

        itemGroupRecyclerView = view.findViewById(R.id.filter_product_recyclerview);
        getItemGroupList();

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        filterDialogCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterShops();
                shopArrayList.clear();
                shopListAdapter.update(shopArrayList);
                progressBar.setVisibility(View.VISIBLE);
                alertDialog.dismiss();
            }
        });
    }

    private void handleRadioButtons() {
        busy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    average.setChecked(false);
                    empty.setChecked(false);
                }
            }
        });
        average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    busy.setChecked(false);
                    empty.setChecked(false);
                }
            }
        });
        empty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                busy.setChecked(false);
                average.setChecked(false);
            }
        });
    }

    private void filterShops() {
        if(busy.isChecked())
            filters.add(new Filter("busyness", "busy"));
        else if(average.isChecked())
            filters.add(new Filter("busyness", "average"));
        else if(empty.isChecked())
            filters.add(new Filter("busyness", "empty"));

        groceryViewModel.getFilteredShopList(filters);
    }

    private void getItemGroupList() {
        groceryViewModel.getItemGroupList();
        observeGroupItemList();
    }

    private void initGroupListRecyclerView(ArrayList<ItemGroup> itemGroups) {
        itemGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemGroupRecyclerView.setHasFixedSize(true);
        filterListAdapter = new FilterListAdapter(itemGroups, getContext(), new FilterListAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(ItemGroup item) {
                filters.add(new Filter("available", item.getId()));
            }

            @Override
            public void onItemUncheck(ItemGroup item) {
                filters.remove(new Filter("available", item.getId()));
            }
        });
        itemGroupRecyclerView.setAdapter(filterListAdapter);
    }

    private void handleButtonClick() {
        showFilterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    private void observeShopList() {
        groceryViewModel.getShopListLiveData().observeForever(new Observer<ArrayList<Shop>>() {
            @Override
            public void onChanged(ArrayList<Shop> shops) {
                if(shops.size() > 0) {
                    initRecyclerView(shops);
                    shopArrayList = new ArrayList<>();
                    shopArrayList = shops;
                }

                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });

        groceryViewModel.getShopListLoading().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
//                if(!error.equals(""))
////                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeGroupItemList() {
        groceryViewModel.getItemGroupLiveData().observeForever(new Observer<ArrayList<ItemGroup>>() {
            @Override
            public void onChanged(ArrayList<ItemGroup> itemGroups) {
                initGroupListRecyclerView(itemGroups);
            }
        });
    }

    private void initRecyclerView(ArrayList<Shop> shops) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopRecyclerView.addItemDecoration(dividerItemDecoration);
        shopListAdapter = new ShopListAdapter(shops, getContext());
        shopRecyclerView.setHasFixedSize(true);
        shopRecyclerView.setAdapter(shopListAdapter);
    }

    ArrayList<Shop> shops1;
    private void filter() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shops1 = new ArrayList<>();
                for(int i = 0; i < shopArrayList.size(); i++) {
                    if(shopArrayList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        shops1.add(shopArrayList.get(i));
                    }
                }

                shopListAdapter.update(shops1);
//                if(count > 0)
//                    shopListAdapter.update(shops1);
//                else
//                    shopListAdapter.update(shopArrayList);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if(searchBar.getText().toString().equals(""))
//                    shopListAdapter.update(GroceryFragment.this.shops);
            }
        });
    }
}