package com.example.team08.tagvirtualgraffiti;

import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 *
 * Model class for User
 */

public class User {

    private String mId;
    private String mEmail;
    private int mScore;
    private String mName;

    public User() {

    }

    public User(String name, int score) {
        mId = UUID.randomUUID().toString();

        mEmail = name;

        mScore = score;//TODO: modify how score is assigned
    }

    public User(String name, String email, String id) {
        mId = id;
        mEmail = email;
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }
    public  int getScore() {return  mScore;}
    public  String getId() {return  mId;}

    public String getName() {
        return mName;
    }
}
