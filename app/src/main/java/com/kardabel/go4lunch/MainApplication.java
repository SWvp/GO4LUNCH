package com.kardabel.go4lunch;

import android.app.Application;

/**
 * Created by stéphane Warin OCR on 26/08/2021.
 */
public class MainApplication extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication=this;
    }

    public static Application getApplication() {
        return sApplication;
    }
}
