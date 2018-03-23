package com.example.team08.tagvirtualgraffiti;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.location.places.*;

public class MainActivity extends AppCompatActivity {
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_nearby_places:
                    setTitle(R.string.title_nearby_places);
                    swapFragment(new NearbyPlacesFragment());

                    return true;
                case R.id.nav_current_location:
                    setTitle(R.string.title_current_location);
                    swapFragment(new CurrentLocationFragment());

                    return true;
                case R.id.nav_map:
                    setTitle(R.string.title_map);
                    //TODO: make the map fragment

                    return true;
                case R.id.nav_leaderboard:
                    setTitle(R.string.title_leaderboard);
                    swapFragment(new LeaderboardFragment());

                    return true;
                case R.id.nav_profile:
                    setTitle(R.string.title_profile);

                    swapFragment(new ProfileFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // PlacesList GeoDataClient: provides access to Google's database of local place and business information.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .add(R.id.main_fragment_container, new NearbyPlacesFragment())
                .commit();

        setTitle(R.string.title_nearby_places);


    }




    private void swapFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();

    }

}
