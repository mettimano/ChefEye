package com.example.ChefEye.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hi!\n I'm ChefEye,\n your assistant in the kitchen.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}