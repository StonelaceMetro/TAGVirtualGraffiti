package com.example.team08.tagvirtualgraffiti;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 *
 * Model class for User
 */

public class User {

    private String id;
    private String email;
    private int score;
    private String name;
    private String taggedPlaceId = "";

    public User() {

    }

    public User(String name, int score) {
        id = UUID.randomUUID().toString();

        email = name;

        this.score = score;//TODO: modify how score is assigned
    }

    public User(String name, String email, String id) {
        this.id = id;
        email = email;
        this.name = name;
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.score = user.getScore();
        this.name = user.getName();
        this.taggedPlaceId = user.getTaggedPlaceId();
    }

    public String getEmail() {
        return email;
    }
    public  int getScore() {return score;}
    public  String getId() {return id;}

    public String getName() {
        return name;
    }

    public String getTaggedPlaceId() {
        return taggedPlaceId;
    }
}
