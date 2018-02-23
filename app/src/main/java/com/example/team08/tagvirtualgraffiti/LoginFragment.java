package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

/**
 * Fragment for login screen.
 * Adapted from Tic-Tac-Toe
 *
 * Created by adamcchampion on 2017/08/03.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
   // private AccountSingleton mAccountSingleton;
   // private AccountDbHelper mDbHelper;

    private final static String OPT_NAME = "name";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container,savedInstanceState);//super call for Logger
        Log.d(TAG, "onCreateView() called");
        View v;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        //if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
        //    v = inflater.inflate(R.layout.fragment_login_land, container, false);
        // } else {
            v = inflater.inflate(R.layout.fragment_login, container, false);
        //}


        mUsernameEditText = (EditText) v.findViewById(R.id.username_text);
        mPasswordEditText = (EditText) v.findViewById(R.id.password_text);

        Button loginButton = (Button) v.findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }
        Button newUserButton = (Button) v.findViewById(R.id.new_user_button);
        if (newUserButton != null) {
            newUserButton.setOnClickListener(this);
        }

        return v;
    }


    private void checkLogin() {
        //Do login stuff here


        // Bring up the Settings screen
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        getActivity().finish();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                checkLogin();
                break;
            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.new_user_button:
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                FragmentManager fm = getFragmentManager();
                Fragment fragment = new AccountFragment();
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    //startActivity(new Intent(getActivity().getApplicationContext(), AccountActivity.class));
                    fm.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack("account_fragment")
                            .commit();
                }
                else {
                    fm.beginTransaction()
                            .add(R.id.account_fragment_container, fragment)
                            .addToBackStack("account_fragment")
                            .commit();
                }
                break;
        }
    }








    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.d(TAG, "onAttach() called");
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated() called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView() called");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() called");
    }


}
