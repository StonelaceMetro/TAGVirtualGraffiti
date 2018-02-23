package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for splash screen.
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class SplashScreenFragment extends Fragment implements View.OnTouchListener{
    protected boolean mIsActive = true;
    protected int mSplashTime = 500;
    protected int mTimeIncrement = 100;
    protected int mSleepTime = 100;

    private final String TAG = getClass().getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);//super call for Logger
        Log.d(TAG, "onCreateView() called");

        View v = inflater.inflate(R.layout.fragment_splash, container, false);
        v.setOnTouchListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");

        // Thread for displaying the SplashScreen
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int elapsedTime = 0;
                    while (mIsActive && (elapsedTime < mSplashTime)) {
                        sleep(mSleepTime);
                        if (mIsActive) elapsedTime = elapsedTime + mTimeIncrement;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    getActivity().finish();
                    startActivity(new Intent("com.example.team08.tagvirtualgraffiti.Login"));
                }
            }
        };
        splashThread.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            mIsActive = false;
            return true;
        }
        return false;
    }





    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //Log.d(TAG, "onAttach() called");
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate() called");
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //Log.d(TAG, "onActivityCreated() called");
    }



    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.d(TAG, "onDestroyView() called");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy() called");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //Log.d(TAG, "onDetach() called");
    }


}
