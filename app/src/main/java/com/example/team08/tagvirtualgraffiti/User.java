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
    private String username;
    private ArrayList<String> taggedPlaceId = new ArrayList<>();
    private int rank;


    public User() {

    }

    public User(String email, int score) {
        id = UUID.randomUUID().toString();

        this.email = email;

        rank = -1;

        this.score = score;//TODO: modify how score is assigned
    }

    public User(String username, String email, String id) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.taggedPlaceId = new ArrayList<>();
    }

    public User(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.score = user.getScore();
        this.username = user.getUsername();
        this.taggedPlaceId = user.getTaggedPlaceId();
    }

    public String getEmail() {return email;}
    public int getScore() {return score;}
    public String getId() {return id;}
    public String getUsername() {return username;}

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
