package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
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

import java.util.List;


public class NearbyPlacesFragment extends Fragment {

    private RecyclerView mPlacesRecyclerView;
    private PlacesAdapter mAdapter;


    private TextView mCurrentPlaceNameView;
    private TextView mCurrentPlaceOwnerView;
    private ImageView mCurrentPlaceImageView;

    protected NearbyPlaces nearbyPlaces;




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
                Toast.makeText(getActivity(),"Refreshing Nearby Places", Toast.LENGTH_SHORT).show();
                loadList();
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

            if (placeItem.getPhoto() != null) {
                mPlacePhotoImageView.setImageBitmap(placeItem.getPhoto());
            } else{
                mPlacePhotoImageView.setImageResource(R.drawable.ic_location_city_black_24dp);
            }
            //TODO:Make these work
            mOwnerTextView.setText(mPlaceItem.getOwnerName());
           // mDistanceTextView.setText(String.format("%.2f", mPlaceItem.getDistance()));
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(),mPlaceItem.getName() + " clicked!", Toast.LENGTH_SHORT).show();

            TagApplication.setCurrentPlace(mPlaceItem);
            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_container, new CurrentLocationFragment());
            ft.addToBackStack(null);
            ft.commit();

            updateUI();
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
