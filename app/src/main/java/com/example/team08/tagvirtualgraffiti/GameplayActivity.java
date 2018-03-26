package com.example.team08.tagvirtualgraffiti;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GameplayActivity extends AppCompatActivity {

    final int ROCK = 1;
    final int PAPER = 2;
    final int SCISSOR = 3;

    private ImageView mPlayerImage;
    private ImageView mOpponentImage;
    private TextView mWinLoseTv;
    private TextView mPlayerNameTv;
    private TextView mOpponentNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra("NOTIFICATION_ID", -1));

        int mySelection = getIntent().getIntExtra("TYPE", -1);
        int opponentSelection = getIntent().getIntExtra("CHALLENGE_TYPE", -1);
        String userName = getIntent().getStringExtra("USER_NAME");
        String userId = getIntent().getStringExtra("USER_ID");
        String placeId = getIntent().getStringExtra("PLACE_ID");

        mPlayerImage = (ImageView) findViewById(R.id.player_image);
        mOpponentImage = (ImageView) findViewById(R.id.opponent_image);
        mWinLoseTv = (TextView) findViewById(R.id.win_tv);
        mPlayerNameTv = (TextView) findViewById(R.id.player_name);
        mOpponentNameTv = (TextView) findViewById(R.id.opponent_name);
        mOpponentNameTv.setText(userName);
        mPlayerNameTv.setText(TagApplication.mCurrentUser.getName());

        switch (mySelection) {
            case ROCK:
                mPlayerImage.setBackgroundResource(R.drawable.rock);
                break;
            case PAPER:
                mPlayerImage.setBackgroundResource(R.drawable.paper);
                break;
            case SCISSOR:
                mPlayerImage.setBackgroundResource(R.drawable.scissors);
                break;
        }

        switch (opponentSelection) {
            case ROCK:
                mOpponentImage.setBackgroundResource(R.drawable.rock);
                break;
            case PAPER:
                mOpponentImage.setBackgroundResource(R.drawable.paper);
                break;
            case SCISSOR:
                mOpponentImage.setBackgroundResource(R.drawable.scissors);
                break;
        }

        String winLoseText = "";

        if (doILose(mySelection, opponentSelection)) {
            winLoseText = "You've lost the tag!";
            updateTags(TagApplication.mCurrentUser.getId(), userId, placeId);
        } else {
            winLoseText = "You've retained your tag!";
        }

        mWinLoseTv.setText(winLoseText);
    }

    public boolean doILose(int mySelection, int opponentSelection) {
        switch (mySelection) {
            case ROCK:
                if (opponentSelection == PAPER)
                    return true;
                return false;
            case PAPER:
                if (opponentSelection == SCISSOR)
                    return true;
                return false;
            case SCISSOR:
                if (opponentSelection == ROCK)
                    return true;
                return false;
        }
        return false;
    }

    public void updateTags(final String currentUserId, final String opponentId, final String placeId) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User opponent = snapshot.getValue(User.class);
                    if (opponent.getId().equals(opponentId)) {
                        ArrayList<String> opponentTaggedPlaces = opponent.getTaggedPlaceId();
                        opponentTaggedPlaces.add(placeId);
                        database.child("users").child(opponentId)
                                .child("taggedPlaceId").setValue(opponentTaggedPlaces);
                        database.child("tags").child(placeId).setValue(opponentId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User opponent = snapshot.getValue(User.class);
                    if (opponent.getId().equals(currentUserId)) {
                        ArrayList<String> taggedPlaces = opponent.getTaggedPlaceId();
                        taggedPlaces.remove(placeId);
                        TagApplication.mCurrentUser.setTaggedPlaceId(taggedPlaces);
                        database.child("users").child(currentUserId)
                                .child("taggedPlaceId").setValue(taggedPlaces);
                        database.child("tagrequests").child(currentUserId).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
