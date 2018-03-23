package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class CurrentLocationFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private TextView mPlaceNameView;
    private ImageView mPlaceImageView;



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

            Task<PlaceLikelihoodBufferResponse> placeResult = getMainActivity().mPlaceDetectionClient.getCurrentPlace(null);

            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                Place currentPlace = likelyPlaces.get(0).getPlace();


                                if (mPlaceImageView != null) {
                                    //TODO: use GeoDataClient to get image?
                                }
                                if (mPlaceNameView != null){
                                    mPlaceNameView.setText(currentPlace.getName());
                                }

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Invalid Location Permissions", Toast.LENGTH_SHORT);
        }
    }
}
