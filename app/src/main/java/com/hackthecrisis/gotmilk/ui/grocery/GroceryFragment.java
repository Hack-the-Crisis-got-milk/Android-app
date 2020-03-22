package com.hackthecrisis.gotmilk.ui.grocery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hackthecrisis.gotmilk.R;
import com.hackthecrisis.gotmilk.adapter.FeedbackAdapter;
import com.hackthecrisis.gotmilk.adapter.FilterListAdapter;
import com.hackthecrisis.gotmilk.adapter.ShopListAdapter;
import com.hackthecrisis.gotmilk.model.Feedback;
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
    private ArrayList<ItemGroup> itemGroupArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groceryViewModel =
                ViewModelProviders.of(this).get(GroceryViewModel.class);
        getItemGroupList();
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
        observeGroupItemList();

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
       // if(itemGroupArrayList.size() > 0)
            initGroupListRecyclerView(itemGroupArrayList);
  //      else
    //        getItemGroupList();

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
    }

    private void initGroupListRecyclerView(ArrayList<ItemGroup> itemGroups) {
        if(itemGroupRecyclerView != null) {
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
                if(shops != null) {
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
               // initGroupListRecyclerView(itemGroups);
                if(itemGroups != null)
                    itemGroupArrayList = new ArrayList<>(itemGroups);
            }
        });
    }

    private void initRecyclerView(ArrayList<Shop> shops) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shopRecyclerView.addItemDecoration(dividerItemDecoration);
        shopListAdapter = new ShopListAdapter(shops, getContext(), new ShopListAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Shop shop) {
                showShopInfoDialog(shop);
            }
        });
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


    ///FEEDBACK

    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;

    private ArrayList<Feedback> feedbackArrayList;

    TextView feedback_busy;
    TextView feedback_average;
    TextView feedback_empty;

    private void showShopInfoDialog(Shop shop) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.shop_feedback_dialog, null);
        builder.setView(view);

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                groceryViewModel.getFeedbackLiveData().removeObservers(GroceryFragment.this);
                groceryViewModel.getFeedbackSent().removeObservers(GroceryFragment.this);
            }
        });

        feedback_busy = view.findViewById(R.id.feedback_busy);
        feedback_average = view.findViewById(R.id.feedback_average);
        feedback_empty = view.findViewById(R.id.feedback_empty);
        handleBusynessClicks(shop);

        ImageView photo = view.findViewById(R.id.inf_shop_photo);
        Glide.with(view.getContext())
                .load(shop.getPhoto())
                .centerCrop()
                .into(photo);

        feedbackRecyclerView = view.findViewById(R.id.feedback_recyclerview);
        initFeedbackRecyclerView(feedbackArrayList, shop);

        TextView name = view.findViewById(R.id.info_shop_name);
        name.setText(shop.getName());

        TextView status = view.findViewById(R.id.info_shop_open_status);
        if(shop.isOpen_now()) {
            status.setText(getActivity().getString(R.string.shop_opened));
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopOpened));
        } else {
            status.setText(getActivity().getString(R.string.shop_closed));
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopClosed));
        }

        TextView busy = view.findViewById(R.id.info_busy);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        groceryViewModel.getFeedbackForShops(shop.getId(), shopArrayList);
        observeFeedback(busy, shop);
        observeSentFeedback();
    }

    private void observeFeedback(TextView busy, Shop shop) {
        groceryViewModel.getFeedbackLiveData().observeForever(new Observer<ArrayList<Feedback>>() {
            @Override
            public void onChanged(ArrayList<Feedback> feedbacks) {
                if(feedbacks != null) {
                    feedbackArrayList = new ArrayList<>(feedbacks);
                    if(feedbacks.size() > 0) {
                        initFeedbackRecyclerView(feedbacks, shop);
                        if(feedbacks.get(0).getType().equals("busyness"))
                            if(feedbacks.get(0).getValue().equals("busy")) {
                                busy.setText("Busy");
                                busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopClosed));
                            } else if(feedbacks.get(0).getValue().equals("average")) {
                                busy.setText("Average");
                                busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                            } else if(feedbacks.get(0).getValue().equals("empty")) {
                                busy.setText("Empty");
                                busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopOpened));
                            }
                    } else {
                        busy.setText("No feedback");
                        busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorNlack));
                    }
                }
            }
        });
    }

    private void handleBusynessClicks(Shop shop) {
        feedback_busy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback_busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopClosed));
                feedback_average.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                feedback_empty.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                groceryViewModel.sendFeedback("", "busyness", "busy", shop.getId());
            }
        });

        feedback_average.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback_average.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                feedback_busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                feedback_empty.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                groceryViewModel.sendFeedback("", "busyness", "average", shop.getId());
            }
        });

        feedback_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback_empty.setTextColor(ContextCompat.getColor(getContext(), R.color.colorShopOpened));
                feedback_average.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                feedback_busy.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
                groceryViewModel.sendFeedback("", "busyness", "empty", shop.getId());
            }
        });
    }

  //  private ArrayList<ItemGroup> itemGroupArrayList;

    private void initFeedbackRecyclerView(ArrayList<Feedback> feedbacks, Shop shop) {
        if(feedbackRecyclerView != null) {
            feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            feedbackRecyclerView.setHasFixedSize(true);
            ArrayList<ItemGroup> itemGroups5 = new ArrayList<>();
            for(ItemGroup itemGroup: itemGroupArrayList) {
                if(shop.getShop_type().equals(itemGroup.getShop_type()))
                    itemGroups5.add(itemGroup);
            }
            feedbackAdapter = new FeedbackAdapter(feedbacks, itemGroups5, getContext(), new FeedbackAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(ItemGroup item) {
                    groceryViewModel.sendFeedback(item.getId(), "availability", "available", shop.getId());
                }

                @Override
                public void onItemUncheck(ItemGroup item) {
                    groceryViewModel.sendFeedback(item.getId(), "availability", "unavailable", shop.getId());
                }
            });
            feedbackRecyclerView.setAdapter(feedbackAdapter);
        }
    }

    private void observeSentFeedback() {
        groceryViewModel.getFeedbackSent().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                    Toast.makeText(getContext(), "Feedback sent!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Feedback wasn't sent", Toast.LENGTH_SHORT).show();
            }
        });
    }
}