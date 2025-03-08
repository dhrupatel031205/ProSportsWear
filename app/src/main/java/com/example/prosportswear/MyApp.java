package com.example.prosportswear;

import android.app.Application;

import coil.ImageLoader;

public class MyApp extends Application {

    private static ImageLoader imageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the ImageLoader globally
        imageLoader = new ImageLoader.Builder(this).build();
    }

    public static ImageLoader getImageLoader() {
        return imageLoader;
    }
}
