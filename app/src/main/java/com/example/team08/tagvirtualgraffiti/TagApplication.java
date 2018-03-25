package com.example.team08.tagvirtualgraffiti;

import android.app.Application;

/**
 * Created by amanpreetsingh on 3/24/18.
 */

public class TagApplication extends Application {

    public static User mCurrentUser = new User();

    public static void setCurrentUser(User user) {
        mCurrentUser = user;
    }

}
