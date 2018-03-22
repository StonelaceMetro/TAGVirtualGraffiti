package com.example.team08.tagvirtualgraffiti;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 3/22/2018.
 *
 * Singleton for accessing lists of nearby places
 */

public class Places {
    private static Places sPlaces;

    private List<Place> mPlaces;

    public static Places get(Context context) {
        if (sPlaces == null) {
            sPlaces = new Places(context);
        }
        return sPlaces;
    }


    private Places(Context context) {
        mPlaces = new ArrayList<Place>();


        makeTestData();
    }



    public List<Place> getPlaces() {
        return mPlaces;
    }





    public Place getPlace(UUID id) {
        for (Place place : mPlaces) {
            if (place.getId().equals(id)) {
                return place;
            }
        }
        return null;
    }




    //TODO: get rid of this once we have real data
    private void makeTestData(){
        for (int i = 0; i < 18; i++) {
            Place place = new Place(("Place" + i), null, (i % 7) * .1357);
            mPlaces.add(place);
        }

        Collections.sort(mPlaces , new Comparator<Place>() {
            @Override
            public int compare(Place p1, Place p2) {
                return p1.getDistance() < p2.getDistance() ? -1 : 1;
            }
        });
    }



}
