package com.hackthecrisis.gotmilk.ui.grocery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroceryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GroceryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}