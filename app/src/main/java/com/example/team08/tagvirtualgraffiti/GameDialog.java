package com.example.team08.tagvirtualgraffiti;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by amanpreetsingh on 3/25/18.
 */

public class GameDialog extends Dialog implements View.OnClickListener {

    Activity mActivity;
    int currentSelection = 1;
    final int ROCK = 1;
    final int PAPER = 2;
    final int SCISSORS = 3;
    ClickListener mListener;

    public GameDialog(@NonNull Activity activity, ClickListener listener) {
        super(activity);
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_dialog);
        ImageView rock = (ImageView) findViewById(R.id.rock);
        ImageView paper = (ImageView) findViewById(R.id.paper);
        ImageView scissors = (ImageView) findViewById(R.id.scissors);
        rock.setOnClickListener(this);
        paper.setOnClickListener(this);
        scissors.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rock:
                currentSelection = ROCK;
                mListener.onClick(ROCK);
                break;
            case R.id.paper:
                currentSelection = PAPER;
                mListener.onClick(PAPER);
                break;
            case R.id.scissors:
                currentSelection = SCISSORS;
                mListener.onClick(SCISSORS);
                break;
        }
    }

    public int getCurrentSelection() {
        return currentSelection;
    }

    public interface ClickListener {
        public void onClick(int selection);
    }
}
