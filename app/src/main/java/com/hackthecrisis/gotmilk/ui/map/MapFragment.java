package com.hackthecrisis.gotmilk.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hackthecrisis.gotmilk.LocationSingleton;
import com.hackthecrisis.gotmilk.R;
import com.hackthecrisis.gotmilk.adapter.FilterListAdapter;
import com.hackthecrisis.gotmilk.model.Filter;
import com.hackthecrisis.gotmilk.model.ItemGroup;
import com.hackthecrisis.gotmilk.model.Shop;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapViewModel mapViewModel;

    private MapView mapView;
    private FloatingActionButton locationButton;
    private FloatingActionButton showFilterbutton;

    private GoogleMap googleMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private Location lastKnownLocation;

    private ArrayList<Shop> shopArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        locationButton = root.findViewById(R.id.location_button);
        showFilterbutton = root.findViewById(R.id.filter_button);
        handleButtonClick();

        mapView = root.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locate();

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                googleMap.setMyLocationEnabled(true);
              //  googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        } else {
            googleMap.setMyLocationEnabled(true);
           // googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
        LocationSingleton.location = location;

        zoom(location, false);

        mapViewModel.getShopList();
        observeShopList();

        handleMarkerClicks();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLocationPermission();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    private void getLocationPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0)
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                googleMap.setMyLocationEnabled(true);
    }

    private void locate() {
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                }
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
                LocationSingleton.location = location;
                if(location != null)
                    zoom(location, true);
            }
        });
    }

    private void zoom(Location location, boolean isAnimate) {
        if(isAnimate) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(13)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(13)
                    .build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void observeShopList() {
        mapViewModel.getShopListLiveData().observeForever(new Observer<ArrayList<Shop>>() {
            @Override
            public void onChanged(ArrayList<Shop> shops) {
                googleMap.clear();
                setMarkers(shops);
                shopArrayList = new ArrayList<>(shops);
                Log.i("shops got", String.valueOf(shops.size()));
            }
        });

        mapViewModel.getShopListLoading().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String error) {
//                if(!error.equals(""))
//                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setMarkers(ArrayList<Shop> shops) {
        if(shops.size() > 0) {
            if(googleMap != null) {
                for(Shop shop : shops) {
                    Log.i("Coordinates", shop.getLocation().toString());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .title(shop.getName())
                            .position(new LatLng(shop.getLocation().getLat(), shop.getLocation().getLon()))
                            .snippet(shop.getId());
                    googleMap.addMarker(markerOptions);
                }
            }
        }
    }

    private void handleMarkerClicks() {
        if(googleMap != null) {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for(Shop shop: shopArrayList) {
                        if(marker.getSnippet().equals(shop.getId()))
                            showShopInfoDialog(shop);
                    }
                    return false;
                }
            });
        }
    }

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

        ImageView photo = view.findViewById(R.id.inf_shop_photo);
//        Glide.with(view.getContext())
//                .load(shop.getPhoto())
//                .centerCrop()
//                .into(photo);

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

//        TextView address = view.findViewById(R.id.info_shop_address);
//        address.setText(shop.getAddress());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    ///FILTER

    private RadioButton busy;
    private RadioButton average;
    private RadioButton empty;

    private ImageView filterDialogCloseButton;
    private Button applyFilterButton;

    private RecyclerView itemGroupRecyclerView;
    private FilterListAdapter filterListAdapter;

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

        mapViewModel.getFilteredShopList(filters);
    }

    private void getItemGroupList() {
        mapViewModel.getItemGroupList();
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

    private void observeGroupItemList() {
        mapViewModel.getItemGroupLiveData().observeForever(new Observer<ArrayList<ItemGroup>>() {
            @Override
            public void onChanged(ArrayList<ItemGroup> itemGroups) {
                initGroupListRecyclerView(itemGroups);
            }
        });
    }
}