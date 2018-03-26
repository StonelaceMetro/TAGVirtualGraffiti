package com.example.team08.tagvirtualgraffiti;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class MapFragment extends Fragment implements View.OnClickListener{

    private final String TAG = getClass().getSimpleName();
    private Button mLaunchMapButton;

    private TextView mSelectedPlaceNameView;
    private TextView mSelectedPlaceOwnerView;
    private ImageView mSelectedPlaceImageView;

    //private TextView mSelectedPlaceDistanceView;



    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);


        mLaunchMapButton = (Button) v.findViewById(R.id.launch_map_button);

        if(mLaunchMapButton != null){
            mLaunchMapButton.setOnClickListener(this);
        }



        mSelectedPlaceNameView = (TextView) v.findViewById(R.id.place_name);
        mSelectedPlaceOwnerView = (TextView) v.findViewById(R.id.owner_username);

        mSelectedPlaceImageView = (ImageView) v.findViewById(R.id.place_image);

        //mSelectedPlaceDistanceView = (TextView) v.findViewById(R.id.place_distance);

        if (TagApplication.getCurrentPlace() != null){
            updateSelectedPlaceUI();
        }


        mSelectedPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click to refresh place UI
                updateSelectedPlaceUI();
            }
        });


        return v;
    }





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER && data != null) {
            if (resultCode != PlacePicker.RESULT_ERROR) {
                Place place = PlacePicker.getPlace(getContext(), data);
                Toast.makeText(getActivity(), String.format("Selected Place: %s", place.getName()), Toast.LENGTH_LONG).show();

                TagApplication.setCurrentPlace(new PlaceItem(place.getId(), (String) place.getName(), place.getLatLng()));
                if (TagApplication.getCurrentPlace() != null){
                    TagApplication.addPlacePhotos(TagApplication.getCurrentPlace(), new ImageLoadedListener() {
                        @Override
                        public void onImageLoaded() {


                            updateSelectedPlaceUI();

                            //Launch Current Place Fragment
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.main_fragment_container, new CurrentLocationFragment());
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                    });
                }



            }
        }
    }


    private void updateSelectedPlaceUI() {
        PlaceItem mSelectedPlace = TagApplication.getCurrentPlace();
        if (mSelectedPlace != null) {

            mSelectedPlaceNameView.setText(mSelectedPlace.getName());

            //THIS WILL ALWAYS BE NULL IF WE DON'T GET THE PHOTO METADATA
            if (mSelectedPlace.getPhoto() != null) {
                mSelectedPlaceImageView.setImageBitmap(mSelectedPlace.getPhoto());
            } else{
                Log.d(TAG, "Unable to find image for selected place: " + mSelectedPlace.getId() + " - " + mSelectedPlace.getName());
            }

            //TODO: Lookup Information about the place in our Database!
            mSelectedPlaceOwnerView.setText(mSelectedPlace.getOwnerName());
            //mSelectedPlaceDistanceView.setText(String.format("%.2f", mSelectedPlace.getDistance()));
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.launch_map_button:
                /*Create PlacePicker UI; check that Google Play Services are available*/
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(getActivity());
                    // Start the Intent by requesting a result, identified by a request code.
                    startActivityForResult(intent, REQUEST_PLACE_PICKER);


                } catch (GooglePlayServicesRepairableException e) {
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                        }
                    });
                    dialog.show();
                    Toast.makeText(getActivity(), "Google Play Services out of date.",
                            Toast.LENGTH_LONG)
                            .show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    Toast.makeText(getActivity(), "Google Play Services is not available.",
                            Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }



}
