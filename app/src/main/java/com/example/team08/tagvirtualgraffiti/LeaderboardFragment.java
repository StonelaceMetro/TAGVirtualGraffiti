package com.example.team08.tagvirtualgraffiti;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LeaderboardFragment extends Fragment {

    private RecyclerView mLeaderboardRecyclerView;
    private PlayerAdapter mAdapter;
    private EditText mSearchEditText;
    private Button mSearchButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_leaderboard, container, false);






        mLeaderboardRecyclerView = (RecyclerView) view
                .findViewById(R.id.leaderboard_recycler_view);
        mLeaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        mSearchButton = (Button) view.findViewById(R.id.search_leaderboard_button);
        mSearchEditText = (EditText) view.findViewById(R.id.search_leaderboard_text);







        fetchUsers();




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








    public void fetchUsers() {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    users.add(userSnapshot.getValue(User.class));
                }
                Collections.sort(users, new UserScoreComparator());
                updateUI(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Recycler View Stuff
    private void updateUI(final ArrayList<User> players) {
//        Leaderboard leaderboard = Leaderboard.get(getActivity());
//        List<User> players = leaderboard.getPlayers();

        mAdapter = new PlayerAdapter(players);
        mLeaderboardRecyclerView.setAdapter(mAdapter);


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter = new PlayerAdapter(players);
                mLeaderboardRecyclerView.setAdapter(mAdapter);

                CharSequence searchTerm = mSearchEditText.getText();
                List<User> matches = new ArrayList<User>();
                for (User user : mAdapter.mPlayers){
                    if (user.getEmail().contains(searchTerm) ){
                        matches.add(user);
                    }
                }

                mAdapter.mPlayers = matches;
            }
        });
    }



    private class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mRankTextView;
        private ImageView mTagImageView;
        private TextView mUsernameTextView;
        private TextView mScoreTextView;

        private User mUser;

        public PlayerHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_user, parent, false));
            itemView.setOnClickListener(this);

            mRankTextView= (TextView) itemView.findViewById(R.id.user_rank);
            mUsernameTextView= (TextView) itemView.findViewById(R.id.user_username);
            mScoreTextView= (TextView) itemView.findViewById(R.id.user_score);
            mTagImageView= (ImageView) itemView.findViewById(R.id.user_tag);//TODO: bind tag images at some point...

        }


        public void bind(User user, int position) {
            mUser = user;
            mRankTextView.setText("#" + Integer.toString(user.getRank()));//TODO: figure out how to determine rank
            mUsernameTextView.setText(mUser.getEmail());
            mScoreTextView.setText(Integer.toString(mUser.getScore()));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            StorageReference ref = storageReference.child("images/"+ mUser.getId());

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(ref)
                    .signature(new StringSignature(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mTagImageView)
                    .onLoadFailed(new Exception("Could not find tag for User: " + mUser.getId()), getResources().getDrawable(R.drawable.ic_photo_black_24dp));

        }

        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(),
                    mUser.getEmail() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    private class PlayerAdapter extends RecyclerView.Adapter<PlayerHolder> {

        private List<User> mPlayers;

        public PlayerAdapter(List<User> players) {
            mPlayers = players;
        }

        @Override
        public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PlayerHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PlayerHolder holder, int position) {
            User player = mPlayers.get(position);
            //Player's rank has not been set if it is below 1
            if (player.getRank() < 1) {
                player.setRank(position + 1);
            }
            holder.bind(player, position);
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }
    }

    public void updateScores(Object snapshot) {
        Map<String, Object> dataSnapshot = (Map) snapshot;
        ArrayList<Integer> scores = new ArrayList<>();
        for (Map.Entry<String, Object> entry : dataSnapshot.entrySet()) {
            HashMap<String, String> user = (HashMap<String, String>) entry.getValue();

        }
        Collections.sort(scores);
    }

    public class UserScoreComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
            return o2.getScore() - o1.getScore();
        }
    }
}
