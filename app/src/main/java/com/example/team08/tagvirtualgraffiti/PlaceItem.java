package com.example.team08.tagvirtualgraffiti;


import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 *
 * Object to hold information return from Google Places
 */

public class PlaceItem {

    private String mId;
    private String mName;
    private List<Bitmap> mPhotos;
    private LatLng mLatLng;

    //TODO:Persist 'Tagged Places'
    private User mOwner = null;

    //TODO:Calculate this using GeoDataClient
    private double mDistance = -1;

    //TODO: Add attributions?




    //For Glide/Web client Places API
    private List<String> mPhotoReferences;

    public PlaceItem(String id, String name, LatLng latLng) {
        mId = id;
        mName = name;
        mPhotos = new ArrayList<Bitmap>();
        mPhotoReferences = new ArrayList<String>();
        mLatLng = latLng;

    }



    public String getId(){return mId;}
    public String getName() {return mName;}
    public LatLng getLatLng() {return mLatLng;}
    public Bitmap getPhoto(){return mPhotos.size() > 0? mPhotos.get(0) : null;}
    public List<Bitmap> getPhotos(){return mPhotos;}
    public void addPhoto(Bitmap bitmap){mPhotos.add(bitmap);}


    public String getPhotoReference(){return mPhotoReferences.size() > 0? mPhotoReferences.get(0) : null;}
    public List<String> getPhotoReferences(){return mPhotoReferences;}
    public void addPhotoReference(String photoReference){mPhotoReferences.add(photoReference);}



    public  User getOwner() { return mOwner;}
    public  String getOwnerName() {return mOwner != null ?  mOwner.getEmail() : "UNTAGGED!";}
    public  double getDistance() {return mDistance;}
}
