package com.example.team08.tagvirtualgraffiti;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class GameplayActivity extends AppCompatActivity {

    final int ROCK = 1;
    final int PAPER = 2;
    final int SCISSOR = 3;

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
        Toast.makeText(this, "Lose? : " + doILose(mySelection, opponentSelection), Toast.LENGTH_SHORT).show();

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
}
