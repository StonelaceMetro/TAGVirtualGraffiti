package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

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
    private FirebaseAuth auth;

    private final static String OPT_NAME = "name";
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container,savedInstanceState);//super call for Logger
        Log.d(TAG, "onCreateView() called");

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

        View v;
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        //if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
        //    v = inflater.inflate(R.layout.fragment_login_land, container, false);
        // } else {
        v = inflater.inflate(R.layout.fragment_login, container, false);
        //}


        mUsernameEditText = (EditText) v.findViewById(R.id.username);
        mPasswordEditText = (EditText) v.findViewById(R.id.password_text);

        Button loginButton = (Button) v.findViewById(R.id.login_button);
        if (loginButton != null) {
            loginButton.setOnClickListener(this);
        }
        Button newUserButton = (Button) v.findViewById(R.id.new_user_button);
        if (newUserButton != null) {
            newUserButton.setOnClickListener(this);
        }
        Button cancelButton = (Button) v.findViewById(R.id.cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(this);
        }

        return v;
    }


    private void checkLogin() {
        //Do login stuff here

        // Bring up the Settings screen
//        startActivity(new Intent(getActivity(), MainActivity.class));
//        getActivity().finish();

        String email = mUsernameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

        if (email.equals("")) {
            Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
//                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(getContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("Aman", "user id : " + auth.getCurrentUser().getUid());
                            final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean found = false;
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        if (user.getId().equals(auth.getCurrentUser().getUid())) {
                                            found = true;
                                            SharedPreferences sharedPref = getContext().getSharedPreferences(
                                                    getString(R.string.user_prefs), Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(user);
                                            editor.putString("USER", json);
                                            editor.commit();
                                            
                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    }
                                    if (!found) {
                                        Toast.makeText(getContext(), "User not found!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                checkLogin();
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
            case R.id.cancel_button:
                getActivity().finish();
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
