package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by adamcchampion on 2017/08/13.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    /*
   @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load preferences from XML resource.
        addPreferencesFromResource(R.xml.settings);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.settings));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }
   */




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container,savedInstanceState);//super call for Logger
        Log.d(TAG, "onCreateView() called");
        View v;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        //if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
        //    v = inflater.inflate(R.layout.fragment_login_land, container, false);
        // } else {
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        //}


        Button logoutButton = (Button) v.findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(this);
        }

        return v;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
        }
    }
}
