package com.example.team08.tagvirtualgraffiti;


import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 */

public class PlaceDummy {

    private UUID mId;
    private String mName;
    private User mOwner;
    private double mDistance;


    public PlaceDummy(String name, User owner, double distance) {
        mId = UUID.randomUUID();

        mName = name;

        mOwner = owner;//TODO: modify how owner is assigned
        mDistance = distance;//TODO: modify how distance is assigned
    }

    public String getName() {
        return mName;
    }
    public  User getOwner() { return mOwner;}
    public  String getOwnerName() {return mOwner != null ?  mOwner.getEmail() : "UNTAGGED!";}
    public  double getDistance() {return mDistance;}
    public  UUID getId() {return  mId;}
}
