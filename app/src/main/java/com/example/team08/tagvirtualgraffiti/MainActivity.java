package com.example.team08.tagvirtualgraffiti;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.places.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "Main Activity";
    private boolean mLocationPermissionGranted = false;


    protected PlaceDetectionClient mPlaceDetectionClient;




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_nearby_places:
                    setTitle(R.string.title_nearby_places);
                    //TODO: Remake this fragment to work for Google Places stuff
                    swapFragment(new NearbyPlacesFragment());

                    return true;
                case R.id.nav_current_location:
                    setTitle(R.string.title_current_location);
                    swapFragment(new CurrentLocationFragment());

                    return true;
                case R.id.nav_map:
                    setTitle(R.string.title_map);
                    swapFragment(new MapFragment());

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

        setCurrentUser();

        checkLocationPermission();

        // NearbyPlaces GeoDataClient: provides access to Google's database of local place and business information.
        TagApplication.sGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .add(R.id.main_fragment_container, new NearbyPlacesFragment())
                .commit();

        setTitle(R.string.title_nearby_places);

//        TagApplication.mFirstTimeLoadingAcitivty = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //locationManager.removeUpdates(this);
        }
    }


    private void swapFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();

    }




    //TODO: See if this actually works?

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.permission_notice)
                        .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //TODO: make this work with our stuff? Source https://stackoverflow.com/questions/40142331/how-to-request-location-permission-at-runtime-on-android-6
                        // locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }






    public void setCurrentUser() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getId().equals(auth.getCurrentUser().getUid())) {
                        found = true;
                        SharedPreferences sharedPref = getSharedPreferences(
                                getString(R.string.user_prefs), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        editor.putString("USER", json);
                        editor.commit();
                        TagApplication.setCurrentUser(user);
                        FirebaseDatabase.getInstance().getReference().child("tagrequests")
                                .child(TagApplication.mCurrentUser.getId())
                                .addValueEventListener(new TagRequestListener());
                    }
                }
                if (!found) {
                    Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class TagRequestListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            String requestString = (String) dataSnapshot.getValue();
            handleTagRequest(requestString);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public void handleTagRequest(final String requestString) {
        if (requestString == null || requestString.equals(""))
            return;
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(TagApplication.mCurrentUser.getId())
                            && user.getId().equals(requestString.split("!!!!!")[0])) {
                        sendRequestNotification(user, requestString.split("!!!!!")[1],
                                requestString.split("!!!!!")[2]);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendRequestNotification(User user, String placeId, String selection) {
        if (TagApplication.mFirstTimeLoadingAcitivty) {
            TagApplication.mFirstTimeLoadingAcitivty = false;
            return;
        }
        int challenge = Integer.parseInt(selection);
        String CHANNEL_ID = "channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, GameplayActivity.class);
        intent.putExtra("NOTIFICATION", true);
        intent.putExtra("CHALLENGE_TYPE", challenge);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 4, intent, 0);

        Intent rockIntent = new Intent(this, GameplayActivity.class);
        rockIntent.putExtra("NOTIFICATION", true);
        rockIntent.putExtra("TYPE", 1);
        rockIntent.putExtra("CHALLENGE_TYPE", challenge);
        rockIntent.putExtra("NOTIFICATION_ID", 99);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        rockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent rockPendingIntent = PendingIntent.getActivity(this, 1, rockIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent paperIntent = new Intent(this, GameplayActivity.class);
        paperIntent.putExtra("NOTIFICATION", true);
        paperIntent.putExtra("TYPE", 2);
        paperIntent.putExtra("CHALLENGE_TYPE", challenge);
        paperIntent.putExtra("NOTIFICATION_ID", 99);
        paperIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent paperPendingIntent = PendingIntent.getActivity(this, 2, paperIntent, 0);

        Intent scissorsIntent = new Intent(this, GameplayActivity.class);
        scissorsIntent.putExtra("NOTIFICATION", true);
        scissorsIntent.putExtra("TYPE", 3);
        scissorsIntent.putExtra("CHALLENGE_TYPE", challenge);
        scissorsIntent.putExtra("NOTIFICATION_ID", 99);
        scissorsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent scissorsPendingIntent = PendingIntent.getActivity(this, 3, scissorsIntent, 0);

        String notificationText = user.getName() + " wants to tag " + placeId + "!\nPick rock, paper or scissors!";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("TAG")
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.rock), rockPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.paper), paperPendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, getString(R.string.scissors), scissorsPendingIntent);

        notificationManager.notify(99, mBuilder.build());

    }
}
