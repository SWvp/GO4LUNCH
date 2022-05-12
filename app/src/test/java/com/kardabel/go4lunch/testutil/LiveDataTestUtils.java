package com.kardabel.go4lunch.testutil;

import androidx.lifecycle.LiveData;

public class LiveDataTestUtils {

    public static<T> void observeForTesting(LiveData<T> liveData, OnObservedListener<T> block) {
        liveData.observeForever(ignored -> {});

        block.onObserved(liveData.getValue());
    }

    public interface OnObservedListener<T> {

        void onObserved(T liveData);

    }
}
