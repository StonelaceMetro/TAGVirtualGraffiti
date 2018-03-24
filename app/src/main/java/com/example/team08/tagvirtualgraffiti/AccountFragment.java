package com.example.team08.tagvirtualgraffiti;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragment for user account creation.
 *
 * Created by adamcchampion on 2017/08/05.
 */

public class AccountFragment extends Fragment implements View.OnClickListener {
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirm;
    //private AccountDbHelper mDbHelper;
    private FirebaseAuth auth;

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container,savedInstanceState);//super call for Logger
        Log.d(TAG, "onCreateView() called");
        auth = FirebaseAuth.getInstance();

        View v = inflater.inflate(R.layout.fragment_account, container, false);
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        mEtUsername = (EditText) v.findViewById(R.id.username);
        mEtPassword = (EditText) v.findViewById(R.id.password);
        mEtConfirm = (EditText) v.findViewById(R.id.password_confirm);
        Button btnAdd = (Button) v.findViewById(R.id.done_button);
        btnAdd.setOnClickListener(this);
        Button btnCancel = (Button) v.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(this);
        Button btnExit = (Button) v.findViewById(R.id.exit_button);
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            btnExit.setOnClickListener(this);
        }
        else {
            btnExit.setVisibility(View.GONE);
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        try {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(getResources().getString(R.string.account));
            }
        }
        catch (NullPointerException npe) {
            Log.e(TAG, "Could not set subtitle");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() called");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done_button:
                createAccount();
                break;
            case R.id.cancel_button:
                mEtUsername.setText("");
                mEtPassword.setText("");
                mEtConfirm.setText("");
                break;
            case R.id.exit_button:
                getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void createAccount() {

        String email = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String confirm = mEtConfirm.getText().toString();

        if (email.equals("")) {
            Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (confirm.equals("")) {
            Toast.makeText(getContext(), "Confirm password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirm.equals("") && !confirm.equals(password)) {
            Toast.makeText(getContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getContext(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
//                                progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
//                            startActivity(new Intent(getContext(), MainActivity.class));
//                            getActivity().finish();
                            getFragmentManager().popBackStack();
                        }
                    }
                });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = mDatabase.push().getKey();
        User user = new User("Alex", email, userId);
        mDatabase.child("users").child(user.getId()).setValue(user);
        /*
        //this.output = (TextView) this.findViewById(R.id.out_text);
        String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        String confirm = mEtConfirm.getText().toString();
        if ((password.equals(confirm)) && (!username.equals("")) && (!password.equals("")) && (!confirm.equals(""))) {
            AccountSingleton singleton = AccountSingleton.get(getActivity().getApplicationContext());
            Account account = new Account(username, password);
            singleton.addAccount(account);
            Toast.makeText(getActivity().getApplicationContext(), "New record inserted", Toast.LENGTH_SHORT).show();
        } else if ((username.equals("")) || (password.equals("")) || (confirm.equals(""))) {
            Toast.makeText(getActivity().getApplicationContext(), "Missing entry", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirm)) {
            FragmentManager manager = getFragmentManager();
            AccountErrorDialogFragment fragment = new AccountErrorDialogFragment();
            fragment.show(manager, "account_error");
        } else {
            Log.e(TAG, "An unknown account creation error occurred.");
        }
    */
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



}
