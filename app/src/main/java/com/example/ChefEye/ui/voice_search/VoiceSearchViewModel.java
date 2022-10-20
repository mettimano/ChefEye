package com.example.ChefEye.ui.voice_search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VoiceSearchViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VoiceSearchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Questo è il fragmento per registrare");
    }

    public LiveData<String> getText() {
        return mText;
    }
}