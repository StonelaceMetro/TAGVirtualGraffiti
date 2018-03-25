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
        NearbyPlaces nearbyPlaces = NearbyPlaces.get((MainActivity) getActivity());
        List<PlaceItem> nearbyPlacesList = nearbyPlaces.getPlacesList();

        mAdapter = new PlacesAdapter(nearbyPlacesList);
        mPlacesRecyclerView.setAdapter(mAdapter);
    }



    private class PlacesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mPlaceNameTextView;
        private ImageView mPlacePhotoImageView;
        private TextView mOwnerTextView;
        private TextView mDistanceTextView;

        private PlaceItem mPlaceItem;

        public PlacesHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_place, parent, false));
            itemView.setOnClickListener(this);

            mPlaceNameTextView= (TextView) itemView.findViewById(R.id.place_name);
            mOwnerTextView= (TextView) itemView.findViewById(R.id.owner_username);
            mPlacePhotoImageView = (ImageView) itemView.findViewById(R.id.place_image);//TODO: bind tag images at some point...
            mDistanceTextView= (TextView) itemView.findViewById(R.id.place_distance);

        }


        public void bind(PlaceItem placeItem) {
            mPlaceItem = placeItem;
            mPlaceNameTextView.setText(mPlaceItem.getName());

            if (placeItem.getPhoto() != null) {
                mPlacePhotoImageView.setImageBitmap(placeItem.getPhoto());
            }

            //TODO:Make these work
            mOwnerTextView.setText(mPlaceItem.getOwnerName());
            mDistanceTextView.setText(String.format("%.2f", mPlaceItem.getDistance()));
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mPlaceItem.getName() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
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
