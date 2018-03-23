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

public class PlacesList {
    private static PlacesList sPlacesList;

    private List<PlaceDummy> mPlaceDummies;

    public static PlacesList get(Context context) {
        if (sPlacesList == null) {
            sPlacesList = new PlacesList(context);
        }
        return sPlacesList;
    }


    private PlacesList(Context context) {
        mPlaceDummies = new ArrayList<PlaceDummy>();


        makeTestData();
    }



    public List<PlaceDummy> getPlaceDummies() {
        return mPlaceDummies;
    }





    public PlaceDummy getPlace(UUID id) {
        for (PlaceDummy placeDummy : mPlaceDummies) {
            if (placeDummy.getId().equals(id)) {
                return placeDummy;
            }
        }
        return null;
    }




    //TODO: get rid of this once we have real data
    private void makeTestData(){
        for (int i = 0; i < 18; i++) {
            PlaceDummy placeDummy = new PlaceDummy(("PlaceDummy" + i), null, (i % 7) * .1357);
            mPlaceDummies.add(placeDummy);
        }

        Collections.sort(mPlaceDummies, new Comparator<PlaceDummy>() {
            @Override
            public int compare(PlaceDummy p1, PlaceDummy p2) {
                return p1.getDistance() < p2.getDistance() ? -1 : 1;
            }
        });
    }



}
