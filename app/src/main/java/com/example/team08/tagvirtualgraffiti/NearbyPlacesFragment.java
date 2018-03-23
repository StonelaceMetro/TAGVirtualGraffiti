package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class NearbyPlacesFragment extends Fragment {

    private RecyclerView mPlacesRecyclerView;
    private PlacesAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_nearby_places, container, false);


        mPlacesRecyclerView = (RecyclerView) view
                .findViewById(R.id.places_recycler_view);
        mPlacesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        updateUI();



        return  view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }










    //Recycler View Stuff
    private void updateUI() {
        PlacesList placesList = PlacesList.get(getActivity());
        List<Place> nearbyPlaces = placesList.getPlaces();

        mAdapter = new PlacesAdapter(nearbyPlaces);
        mPlacesRecyclerView.setAdapter(mAdapter);
    }



    private class PlacesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPlaceNameTextView;
        private ImageView mOwnerImageView;
        private TextView mOwnerTextView;
        private TextView mDistanceTextView;

        private Place mPlace;

        public PlacesHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_place, parent, false));
            itemView.setOnClickListener(this);

            mPlaceNameTextView= (TextView) itemView.findViewById(R.id.place_name);
            mOwnerTextView= (TextView) itemView.findViewById(R.id.owner_username);
            mOwnerImageView= (ImageView) itemView.findViewById(R.id.owner_tag);//TODO: bind tag images at some point...
            mDistanceTextView= (TextView) itemView.findViewById(R.id.place_distance);

        }


        public void bind(Place place) {
            mPlace = place;
            mPlaceNameTextView.setText(mPlace.getName());
            mOwnerTextView.setText(mPlace.getOwnerName());
            mDistanceTextView.setText(String.format("%.2f", mPlace.getDistance()));
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mPlace.getName() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private class PlacesAdapter extends RecyclerView.Adapter<PlacesHolder> {

        private List<Place> mPlaces;

        public PlacesAdapter(List<Place> places) {
            mPlaces = places;
        }

        @Override
        public PlacesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PlacesHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PlacesHolder holder, int position) {
            Place place = mPlaces.get(position);
            holder.bind(place);
        }

        @Override
        public int getItemCount() {
            return mPlaces.size();
        }
    }


}
