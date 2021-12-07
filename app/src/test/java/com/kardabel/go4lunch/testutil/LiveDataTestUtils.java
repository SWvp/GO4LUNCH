package com.kardabel.go4lunch.testutil;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LiveDataTestUtils {

    public static<T> void observeForTesting(LiveData<T> liveData, OnObservedListener<T> block) {
        liveData.observeForever(ignored -> {});

        block.onObserved(liveData.getValue());
    }

    public interface OnObservedListener<T> {

        void onObserved(T liveData);

    }
}
