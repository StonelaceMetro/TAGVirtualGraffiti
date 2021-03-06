package com.example.team08.tagvirtualgraffiti;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

/**
 * Created by amanpreetsingh on 3/24/18.
 */

public class TagApplication extends Application {

    public static String TAG = "TagApplication";

    public static User mCurrentUser = new User();
    public static boolean mFirstTimeLoadingAcitivty = true;

    public static void setCurrentUser(User user) {
        mCurrentUser = user;
    }

    protected static GeoDataClient sGeoDataClient;
    private static PlaceItem sPlaceSearchResult;
    private static PlaceItem sCurrentPlace;

    public static void setCurrentPlace(PlaceItem place){sCurrentPlace = place;}
    public static PlaceItem getCurrentPlace(){return sCurrentPlace;}



    static final int PHOTO_SIZE_PX = 1080;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }



    protected static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();



        return isConnected;
    }



    protected static Dialog makeDialog(Activity activity, int titleID, int messageID){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(messageID)
                .setTitle(titleID);


        builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
            }
        });

        return builder.create();
    }

    /**
     * Checks if location (Network + GPS) settings are enabled and provides user a dialog to modify them.
     *
     * @param context
     * @return if location is currently available
     */
    protected static boolean checkLocationEnabled(final Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(R.string.msg_location_disabled);
            dialog.setTitle(R.string.title_location_disable);
            dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }


        return gps_enabled && network_enabled;
    }


    /**
     * Static Method to look up a place based on its placeID
     *
     * @param imageLoadedListener - Code to be run after the place is found
     * @return - A PlaceItem PCorresponding to PlaceID; returns null if place cannot be found.
     */
    public static PlaceItem fetchPlace(@NonNull final String placeID, @Nullable final ImageLoadedListener imageLoadedListener){
        sPlaceSearchResult = null;


        if (TagApplication.sGeoDataClient != null){

            Task<PlaceBufferResponse> placeResultTask = TagApplication.sGeoDataClient.getPlaceById(placeID);

            placeResultTask.addOnCompleteListener
                    (new OnCompleteListener<PlaceBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceBufferResponse likelyPlaces = task.getResult();

                                Place place = likelyPlaces.get(0);

                                //Retain Place, since we have to release places after updating
                                sPlaceSearchResult = new PlaceItem(place.getId(), place.getName().toString(), place.getLatLng());


                                //Release places buffer
                                likelyPlaces.release();


                                if(sPlaceSearchResult!= null) {
                                    addPlacePhotos(sPlaceSearchResult, imageLoadedListener);
                                }






                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });

        }else{
            Log.d(TAG, "Place lookup failed! Insufficient permissions or GeoDataClient not Initialized!");

        }


        return sPlaceSearchResult;
    }



    /**
     * Fetches and adds photos to PlaceItem object
     *
     * @param placeItem - The placeItem to find and add photos to.
     */
    public static void addPlacePhotos(@NonNull final PlaceItem placeItem, @Nullable final ImageLoadedListener imageLoadedListener) {
        if (TagApplication.sGeoDataClient != null) {
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = TagApplication.sGeoDataClient.getPlacePhotos(placeItem.getId());
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                    Log.d(TAG, "PhotoMetadataBufferSize: " + photoMetadataBuffer.getCount());

                    if (photoMetadataBuffer.getCount() > 0) {

                        // Get the first photo in the list. TODO: Get All Photos?
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                        // Get the attribution text. TODO: do something with this?
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = TagApplication.sGeoDataClient.getScaledPhoto(photoMetadata, PHOTO_SIZE_PX, PHOTO_SIZE_PX);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();

                                placeItem.addPhoto(bitmap);

                                if (imageLoadedListener != null) {
                                    imageLoadedListener.onImageLoaded();
                                }

                            }
                        });
                    } else {
                        Log.d(TAG, "No Images available for Place: " + placeItem.getId() + " : " + placeItem.getName());

                        //invoke the listener even if the place has no images
                        if (imageLoadedListener != null) {
                            imageLoadedListener.onImageLoaded();
                        }

                    }
                }
            });

        }else{
            Log.d(TAG, "GeoDataClient Not Loaded!");
        }
    }




}
