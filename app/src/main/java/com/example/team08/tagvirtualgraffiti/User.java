package com.example.team08.tagvirtualgraffiti;

import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 *
 * Model class for User
 */

public class User {


    private UUID mId;
    private String mUsername;
    private int mScore;

    public User(String name, int score) {
        mId = UUID.randomUUID();

        mUsername = name;

        mScore = score;//TODO: modify how score is assigned
    }

    public String getUsername() {
        return mUsername;
    }
    public  int getScore() {return  mScore;}
    public  UUID getId() {return  mId;}

}
