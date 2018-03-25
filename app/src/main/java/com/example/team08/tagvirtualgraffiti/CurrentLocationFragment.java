package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


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

        updateCurrentPlace();

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


    //TODO: maybe store and update Current Place in Main Activity
    private void updateCurrentPlace() {

        if (getMainActivity().checkLocationPermission()) {

            Task<PlaceLikelihoodBufferResponse> placeResultTask = getMainActivity().mPlaceDetectionClient.getCurrentPlace(null);

            placeResultTask.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                Place place = likelyPlaces.get(0).getPlace();

                                //Retain Place, since we have to release places after updating
                                mCurrentPlace = new PlaceItem(place.getId(), place.getName().toString(), place.getLatLng());


                                //Release places buffer
                                likelyPlaces.release();

                                updatePlaceUI();

                                if(mCurrentPlace != null) {
                                    getMainActivity().addPlacePhotos(mCurrentPlace, new ImageLoadedListener() {
                                        @Override
                                        public void onImageLoaded() {
                                            updatePlaceUI();
                                        }
                                    });
                                }






                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Invalid Location Permissions", Toast.LENGTH_SHORT).show();
        }





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
                tag();
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
                ArrayList<String> taggedPlaces = TagApplication.mCurrentUser.getTaggedPlaceId();
                taggedPlaces.add(mCurrentPlace.getId());
                database.child("users").child(TagApplication.mCurrentUser.getId())
                        .child("taggedPlaceId").setValue(taggedPlaces);
                database.child("tags").child(mCurrentPlace.getId()).setValue(TagApplication.mCurrentUser.getId());
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


