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
    private final String TAG = "NearbyPlaces";

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

        fetchNearbyPlaces(DEFAULT_SIZE);
    }



    public List<PlaceItem> getPlacesList() {
        return mNearbyPlaces;
    }



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
     * Updates the list of nearby places
     *
     * @param listSize - the size of the list of nearby places to return
     */
    public void fetchNearbyPlaces(final int listSize){

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



                                //Get photos for each nearby place
                                for (PlaceItem place : mNearbyPlaces) {
                                    findPlacePhotos(place.getId());
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



    //TODO: Move this method to MainActivity to avoid code duplication
    private void findPlacePhotos(final String placeId) {

        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mMainActivity.mGeoDataClient.getPlacePhotos(placeId);
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
                    // Get the attribution text. TODO: do something with this?
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mMainActivity.mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();

                            getPlace(placeId).addPhoto(bitmap);

                        }
                    });
                }else
                    Toast.makeText(mMainActivity, "No Photos Found for this Place", Toast.LENGTH_SHORT).show();
            }
        });

    }




}
