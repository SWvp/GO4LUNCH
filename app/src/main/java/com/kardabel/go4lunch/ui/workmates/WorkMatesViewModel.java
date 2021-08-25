package com.kardabel.go4lunch.ui.workmates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkMatesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WorkMatesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Workmates fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}