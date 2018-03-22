package com.example.team08.tagvirtualgraffiti;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_nearby_places:
                    mTextMessage.setText(R.string.title_nearby_places);
                    return true;
                case R.id.nav_current_location:
                    mTextMessage.setText(R.string.title_current_location);
                    swapFragment(new CurrentLocationFragment());

                    return true;
                case R.id.nav_map:
                    mTextMessage.setText(R.string.title_map);
                    return true;
                case R.id.nav_leaderboard:
                    mTextMessage.setText(R.string.title_leaderboard);
                    swapFragment(new LeaderboardFragment());

                    return true;
                case R.id.nav_profile:
                    mTextMessage.setText(R.string.title_profile);

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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }




    private void swapFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();

    }

}
