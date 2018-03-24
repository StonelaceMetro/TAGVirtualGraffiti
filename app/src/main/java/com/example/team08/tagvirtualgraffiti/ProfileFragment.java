package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private Button mLogoutButton;
    private ImageButton mChangeTagButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        //NOTE: Button is derived from TextView whereas ImageButton is derived from ImageView
        mLogoutButton = (Button) v.findViewById(R.id.logout_button);
        mChangeTagButton = (ImageButton) v.findViewById(R.id.change_tag_button);

        if(mLogoutButton != null){
            mLogoutButton.setOnClickListener(this);
        }

        if(mChangeTagButton != null){
            mChangeTagButton.setOnClickListener(this);
        }

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void logout(){

        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authListener);
        FirebaseAuth.getInstance().signOut();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout_button:
                Toast.makeText(getContext(), "Logging Out...", Toast.LENGTH_SHORT).show();
                logout();
                break;
            case R.id.change_tag_button:
                //TODO: open gallery, upload and scale photo
                Toast.makeText(getActivity(), "Opening Gallery...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
