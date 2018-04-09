package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CurrentLocationFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private TextView mCurrentPlaceNameView;
    private ImageView mCurrentPlaceImageView;
    private TextView mCurrentPlaceOwnerView;
    private Button mTagButton;

    private PlaceItem mCurrentPlace;
    private GameDialog mGameDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_current_location, container, false);


        mCurrentPlaceNameView = (TextView) v.findViewById(R.id.place_name);
        mCurrentPlaceImageView = (ImageView) v.findViewById(R.id.place_image);
        mCurrentPlaceOwnerView = (TextView) v.findViewById(R.id.owner_username);
        mTagButton = (Button) v.findViewById(R.id.tag_button);
        mTagButton.setOnClickListener(this);


        mCurrentPlace = TagApplication.getCurrentPlace();
        updatePlaceUI();

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


    /**
     * Returns the fragment's activity cast as MainActivity for access to MainActivity's members
     *
     * @return fragment's current activity cast as MainActivity
     */
    private MainActivity getMainActivity(){

        return (MainActivity) getActivity();
    }





    private void updatePlaceUI() {

        if (mCurrentPlace != null) {
            mCurrentPlaceNameView.setText(mCurrentPlace.getName());

            //THIS WILL ALWAYS BE NULL IF WE DON'T GET THE PHOTO METADATA
            if (mCurrentPlace.getPhoto() != null) {
                mCurrentPlaceImageView.setImageBitmap(mCurrentPlace.getPhoto());
            } else{
                Log.d(TAG, "Unable to find image for current place: " + mCurrentPlace.getId() + " - " + mCurrentPlace.getName());
            }

            //TODO: Lookup Information about the place in our Database!
            mCurrentPlaceOwnerView.setText(mCurrentPlace.getOwnerName());
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tag_button:

                if(TagApplication.isOnline(getContext())) {

                    if (TagApplication.mCurrentUser.getTaggedPlaceIds().contains(mCurrentPlace.getId())) {
                        Toast.makeText(getContext(), "You already own this place!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    tag();

                } else {
                    TagApplication.makeDialog(getActivity(), R.string.title_offline, R.string.msg_offline).show();

                }
                break;
        }
    }

    public void tag() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String userId = (String) snapshot.getValue();
                    String placeId = snapshot.getKey();
                    if (placeId.equals(mCurrentPlace.getId())) {
                        handleTagRequest(userId, placeId);
                        return;
                    }
                }
                ArrayList<String> taggedPlaces = TagApplication.mCurrentUser.getTaggedPlaceIds();
                taggedPlaces.add(mCurrentPlace.getId());
                database.child("users").child(TagApplication.mCurrentUser.getId())
                        .child("taggedPlaceId").setValue(taggedPlaces);
                database.child("users").child(TagApplication.mCurrentUser.getId())
                        .child("score").setValue(taggedPlaces.size());
                database.child("tags").child(mCurrentPlace.getId()).setValue(TagApplication.mCurrentUser.getId());


                Toast.makeText(getContext(), "It's yours now!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void handleTagRequest(final String taggedBy, final String placeId) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("tagrequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean requestPending = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String requestUserId = (String) snapshot.getValue();
                    String userId = snapshot.getKey();
                    if (userId.equals(taggedBy)) {
                        requestPending = true;
                    }
                }
                if (!requestPending) {
                    mGameDialog = new GameDialog(getActivity(), new GameDialog.ClickListener() {
                        @Override
                        public void onClick(int selection) {
                            database.child("tagrequests").child(taggedBy)
                                    .setValue(TagApplication.mCurrentUser.getId() + "!!!!!" + placeId + "!!!!!" + mGameDialog.getCurrentSelection());
                            mGameDialog.dismiss();
                        }
                    });
                    mGameDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


