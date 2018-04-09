package com.example.team08.tagvirtualgraffiti;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NearbyPlacesFragment extends Fragment {

    private RecyclerView mPlacesRecyclerView;
    private PlacesAdapter mAdapter;


    private TextView mCurrentPlaceNameView;
    private TextView mCurrentPlaceOwnerView;
    private ImageView mCurrentPlaceImageView;

    protected NearbyPlaces nearbyPlaces;



    private static final int REQUEST_PLACE_PICKER = 54;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearby_places, container, false);

        setHasOptionsMenu(true);

        mPlacesRecyclerView = (RecyclerView) view
                .findViewById(R.id.places_recycler_view);
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        //TODO:Make this a ListItem?
        mCurrentPlaceNameView = (TextView) view.findViewById(R.id.current_place_name);
        mCurrentPlaceImageView = (ImageView) view.findViewById(R.id.current_place_img);
        mCurrentPlaceOwnerView = (TextView) view.findViewById(R.id.current_owner_username);



        if(nearbyPlaces == null) {

            loadList();

        } else {
            updateUI();
        }




        return  view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh_nearby_places:
                Toast.makeText(getActivity(), "Refreshing Nearby Places", Toast.LENGTH_SHORT).show();
                loadList();
                return true;
            case R.id.launch_map_menu:
                launchMap();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }





    private void loadList(){
        nearbyPlaces = NearbyPlaces.get((MainActivity) getActivity());
        nearbyPlaces.clearPlacesList();
        nearbyPlaces.fetchNearbyPlaces(NearbyPlaces.DEFAULT_SIZE, new PlacesLoadedListener() {
            @Override
            public void onPlacesLoaded() {
                updateUI();
            }
        });

    }



    /**
     * Launches the Place Picker (Map) Activity
     */
    private void launchMap() {
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
            Toast.makeText(getContext(), "Google Play Services out of date.",
                    Toast.LENGTH_LONG)
                    .show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(getContext(), "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Result of PlacePicker (Map) Activity
        if (requestCode == REQUEST_PLACE_PICKER && data != null) {
            if (resultCode != PlacePicker.RESULT_ERROR) {
                Place place = PlacePicker.getPlace(getActivity(), data);
                Toast.makeText(getContext(), String.format("Selected Place: %s", place.getName()), Toast.LENGTH_LONG).show();

                TagApplication.setCurrentPlace(new PlaceItem(place.getId(), (String) place.getName(), place.getLatLng()));
                if (TagApplication.getCurrentPlace() != null){
                    TagApplication.addPlacePhotos(TagApplication.getCurrentPlace(), new ImageLoadedListener() {
                        @Override
                        public void onImageLoaded() {
                            //Launch Current Place Fragment
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










    //Recycler View Stuff
    private void updateUI() {
        if (TagApplication.getCurrentPlace() != null){
            mCurrentPlaceNameView.setText(TagApplication.getCurrentPlace().getName());

            if (TagApplication.getCurrentPlace().getPhoto() != null) {
                mCurrentPlaceImageView.setImageBitmap(TagApplication.getCurrentPlace().getPhoto());
            } else {
                mCurrentPlaceImageView.setImageResource(R.drawable.ic_location_city_black_24dp);
            }
            //TODO:Make these work
            mCurrentPlaceOwnerView.setText(TagApplication.getCurrentPlace().getOwnerName());
        }


        List<PlaceItem> nearbyPlacesList = nearbyPlaces.getPlacesList();

        mAdapter = new PlacesAdapter(nearbyPlacesList);
        mPlacesRecyclerView.setAdapter(mAdapter);

    }



    private class PlacesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPlaceNameTextView;
        private ImageView mPlacePhotoImageView;
        private TextView mOwnerTextView;
        // TextView mDistanceTextView;

        private PlaceItem mPlaceItem;

        public PlacesHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_place, parent, false));
            itemView.setOnClickListener(this);

            mPlaceNameTextView= (TextView) itemView.findViewById(R.id.place_name);
            mPlacePhotoImageView = (ImageView) itemView.findViewById(R.id.place_image);


            //TODO: bind tag images at some point...
            mOwnerTextView= (TextView) itemView.findViewById(R.id.owner_username);
            //mDistanceTextView= (TextView) itemView.findViewById(R.id.place_distance);

        }


        public void bind(PlaceItem placeItem) {
            mPlaceItem = placeItem;
            mPlaceNameTextView.setText(mPlaceItem.getName());


            String url = "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=400" +
                    "&photoreference=" + placeItem.getPhotoReference() +
                    "&key=AIzaSyDdBfOi09N5GUxnvcY8345lRNaZQ-nexDU";


            Glide.with(getContext())
                    .load(url)
                    .signature(new StringSignature(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())))
                    .placeholder(R.drawable.ic_location_city_black_24dp)
                    .into(mPlacePhotoImageView)
                    .onLoadFailed(new Exception("Could not find photo for Place: " + placeItem.getId()), getResources().getDrawable(R.drawable.ic_location_city_black_24dp));



/*
            if (placeItem.getPhoto() != null) {
                mPlacePhotoImageView.setImageBitmap(placeItem.getPhoto());
            } else{
                mPlacePhotoImageView.setImageResource(R.drawable.ic_location_city_black_24dp);
            }
*/








            //TODO:Make these work
            mOwnerTextView.setText(mPlaceItem.getOwnerName());
           // mDistanceTextView.setText(String.format("%.2f", mPlaceItem.getDistance()));
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mPlaceItem.getUsername() + " clicked!", Toast.LENGTH_SHORT).show();

            updateUI();

            TagApplication.setCurrentPlace(mPlaceItem);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_container, new CurrentLocationFragment());
            ft.addToBackStack(null);
            ft.commit();


        }
    }


    private class PlacesAdapter extends RecyclerView.Adapter<PlacesHolder> {

        private List<PlaceItem> mPlacesList;

        public PlacesAdapter(List<PlaceItem> placesList) {
            mPlacesList = placesList;
        }

        @Override
        public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PlacesHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PlacesHolder holder, int position) {
            PlaceItem placeItem = mPlacesList.get(position);
            holder.bind(placeItem);
        }

        @Override
        public int getItemCount() {
            return mPlacesList.size();
        }
    }


}
