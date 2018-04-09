package com.example.team08.tagvirtualgraffiti;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
                                    //WITHOUT GLIDE
                                    //addPlacePhotos(sPlaceSearchResult, imageLoadedListener);
                                    //WITH GLIDE
                                    addPhotoReferences(sPlaceSearchResult, imageLoadedListener);
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


    /**
     * Fetches and adds (first) photoreference to placeItem object (for caching with Glide)
     *
     * @param placeItem - The placeItem to find and add photos to.
     */
    public static void addPhotoReferences(@NonNull final PlaceItem placeItem, @Nullable final ImageLoadedListener referenceLoadedListener) {

        try{

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);


            HttpURLConnection urlConnection = null;
            URL url = new URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeItem.getId() +
                    "&key=AIzaSyDdBfOi09N5GUxnvcY8345lRNaZQ-nexDU");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */ );
            urlConnection.setConnectTimeout(15000 /* milliseconds */ );
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            //System.out.println("JSON: " + jsonString);


            JSONObject jsonObject = new JSONObject(jsonString);
            //
            // Parse your json here
            //
            JSONObject result = jsonObject.getJSONObject("result");

            JSONArray photos = result.getJSONArray("photos");
            if (photos.length() > 0){
                //Get photo references
                for (int i = 0; i < photos.length(); i++) {
                    placeItem.addPhotoReference(photos.getJSONObject(i).getString("photo_reference"));
                }

                if (referenceLoadedListener != null) {
                    referenceLoadedListener.onImageLoaded();
                }
            }else{
                Log.d(TAG, "No PhotoReferences available for Place: " + placeItem.getId() + " : " + placeItem.getName());

                //Invoke listener even if no reference found
                if (referenceLoadedListener != null) {
                    referenceLoadedListener.onImageLoaded();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
