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
    private ArrayList<String> taggedPlaceId = new ArrayList<>();
    private int rank;


    public User() {

    }

    public User(String name, int score) {
        id = UUID.randomUUID().toString();

        email = name;

        rank = -1;

        this.score = score;//TODO: modify how score is assigned
    }

    public User(String name, String email, String id) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.taggedPlaceId = new ArrayList<>();
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

    public ArrayList<String> getTaggedPlaceId() {
        return taggedPlaceId;
    }

    public void setTaggedPlaceId(ArrayList<String> taggedPlaceId) {
        this.taggedPlaceId = taggedPlaceId;
    }

    //Returns -1 if rank has not been set
    public  int getRank() {return rank;}
    public void setRank(int newRank) {rank = newRank;}
}
