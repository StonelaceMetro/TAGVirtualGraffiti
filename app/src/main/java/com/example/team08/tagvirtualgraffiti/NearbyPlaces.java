package com.example.team08.tagvirtualgraffiti;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Adam on 3/22/2018.
 *
 * Singleton for accessing lists of nearby places
 */

public class NearbyPlaces {

    static final int DEFAULT_SIZE = 10;
    private final String TAG = getClass().getSimpleName();

    private static NearbyPlaces sNearbyPlaces;

    private List<PlaceItem> mNearbyPlaces;
    private MainActivity mMainActivity;


    public static NearbyPlaces get(MainActivity activity) {
        if (sNearbyPlaces == null) {
            sNearbyPlaces = new NearbyPlaces(activity);
        }
        return sNearbyPlaces;
    }


    private NearbyPlaces(MainActivity mainActivity) {
        mNearbyPlaces = new ArrayList<PlaceItem>();


        mMainActivity = mainActivity;

    }



    public List<PlaceItem> getPlacesList() {
        return mNearbyPlaces;
    }
    public void clearPlacesList() { mNearbyPlaces.clear(); }



    public PlaceItem getPlace(String id) {
        for (PlaceItem placeItem : mNearbyPlaces) {
            if (placeItem.getId().equals(id)) {
                return placeItem;
            }
        }
        return null;
    }




    //TODO: Move this method to MainActivity to avoid code duplication
    /**
     * Adds nearby places to list
     *
     * @param listSize - the size of the list of nearby places to return
     */
    public void fetchNearbyPlaces(final int listSize, @NonNull final PlacesLoadedListener placesLoadedListener){

        if (mMainActivity.checkLocationPermission()) {

            Task<PlaceLikelihoodBufferResponse> placeResultTask = mMainActivity.mPlaceDetectionClient.getCurrentPlace(null);

            placeResultTask.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();



                                int count;
                                if (likelyPlaces.getCount() < listSize) {
                                    count = likelyPlaces.getCount();
                                    Log.d(TAG, "Not enough places to fill requested list size. Count: " + count);
                                } else {
                                    count = listSize;
                                }



                                for (int i = 0; i < count; i++) {
                                    // Build a list of likely places to show the user.
                                    Place place = likelyPlaces.get(i).getPlace();

                                    mNearbyPlaces.add(new PlaceItem(place.getId(), place.getName().toString(), place.getLatLng()));

                                }

                                //Release places buffer
                                likelyPlaces.release();


                                //TODO: Make this more efficient (Places loaded Listener is called twice)

                                //Places now loaded (but without images)
                                if (placesLoadedListener != null) {
                                    placesLoadedListener.onPlacesLoaded();
                                }
                                //Get photos for each nearby place; Attach places loaded listener to last place
                                for (int i = 0; i < mNearbyPlaces.size(); i++){
                                    ImageLoadedListener il = null;
                                    if (placesLoadedListener != null) {
                                        il = new ImageLoadedListener() {
                                            @Override
                                            public void onImageLoaded() {
                                                placesLoadedListener.onPlacesLoaded();
                                            }
                                        };
                                    }
                                    TagApplication.addPlacePhotos(mNearbyPlaces.get(i), il);
                                }



                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(mMainActivity, "Invalid Location Permissions", Toast.LENGTH_SHORT).show();
        }
    }





}
