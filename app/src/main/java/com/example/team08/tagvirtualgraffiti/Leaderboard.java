package com.example.team08.tagvirtualgraffiti;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 */

public class Leaderboard {
    private static Leaderboard sLeaderboard;

    private List<User> mPlayers;

    public static Leaderboard get(Context context) {
        if (sLeaderboard == null) {
            sLeaderboard = new Leaderboard(context);
        }
        return sLeaderboard;
    }


    private Leaderboard(Context context) {
        mPlayers = new ArrayList<User>();


        makeTestData();
    }



    public List<User> getPlayers() {
        return mPlayers;
    }





    public User getPlayer(UUID id) {
        for (User player : mPlayers) {
            if (player.getId().equals(id)) {
                return player;
            }
        }
        return null;
    }

    public User getPlayer(String username) {
        for (User player : mPlayers) {
            if (player.getEmail().equals(username)) {
                return player;
            }
        }
        return null;
    }


    //TODO: get rid of this once we have real data
    private void makeTestData(){
        for (int i = 0; i < 30; i++) {
            User user = new User(("User" + i), (i % 17)*i);
            mPlayers.add(user);
        }

        Collections.sort(mPlayers , new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return u2.getScore() - u1.getScore();
            }
        });
    }



}
