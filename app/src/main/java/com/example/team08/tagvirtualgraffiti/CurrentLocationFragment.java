package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CurrentLocationFragment extends Fragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private TextView mPlaceNameView;
    private ImageView mPlaceImageView;
    private Button mTagButton;

    private String currentPlaceId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_current_location, container, false);


        mPlaceNameView = (TextView) v.findViewById(R.id.place_name);
        mPlaceImageView = (ImageView) v.findViewById(R.id.place_image);
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



    private void updateCurrentPlace() {

        if (getMainActivity().checkLocationPermission()) {

            Task<PlaceLikelihoodBufferResponse> placeResultTask = getMainActivity().mPlaceDetectionClient.getCurrentPlace(null);

            placeResultTask.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                Place currentPlace = likelyPlaces.get(0).getPlace();

                                //Retain PlaceId, since we have to release places after updating
                                currentPlaceId = currentPlace.getId();

                                if (mPlaceNameView != null){
                                    mPlaceNameView.setText(currentPlace.getName());
                                }


                                //Release places buffer
                                likelyPlaces.release();



                                Log.d(TAG, "New Place ID: " + currentPlaceId);

                                updatePlacePhoto();


                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Invalid Location Permissions", Toast.LENGTH_SHORT).show();
        }
    }




    private void updatePlacePhoto() {
        //TODO:Check mGeoDataClient for null; May move this method to MainActivity
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = getMainActivity().mGeoDataClient.getPlacePhotos(currentPlaceId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                Log.d(TAG, "PhotoMetadataBufferSize: " + photoMetadataBuffer.getCount());

                if(photoMetadataBuffer.getCount() > 0) {

                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = getMainActivity().mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();

                            mPlaceImageView.setImageBitmap(bitmap);
                        }
                    });
                }else
                    Toast.makeText(getContext(), "No Photos Found for this Place", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tag_button:
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(TagApplication.mCurrentUser.getId()).child("taggedPlaceId").setValue(currentPlaceId);
                break;
        }
    }
}


