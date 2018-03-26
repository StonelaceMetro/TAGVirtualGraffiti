package com.example.team08.tagvirtualgraffiti;

import android.app.Application;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.firebase.FirebaseApp;

/**
 * Created by amanpreetsingh on 3/24/18.
 */

public class TagApplication extends Application {

    public static User mCurrentUser = new User();
    public static boolean mFirstTimeLoadingAcitivty = true;

    public static void setCurrentUser(User user) {
        mCurrentUser = user;
    }

    protected static GeoDataClient sGeoDataClient;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
