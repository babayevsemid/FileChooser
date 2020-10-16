package com.semid.filechooser;

import android.app.Application;

import com.semid.library.FileChooser;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FileChooser.setup(this);
    }
}
