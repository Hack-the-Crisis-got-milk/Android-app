package com.hackthecrisis.gotmilk.ui.grocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.hackthecrisis.gotmilk.R;

public class GroceryFragment extends Fragment {

    private GroceryViewModel groceryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        groceryViewModel =
                ViewModelProviders.of(this).get(GroceryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_grocery, container, false);
        return root;
    }
}