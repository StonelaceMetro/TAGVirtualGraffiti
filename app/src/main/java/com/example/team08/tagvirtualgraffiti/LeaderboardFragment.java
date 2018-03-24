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


public class LeaderboardFragment extends Fragment {

    private RecyclerView mLeaderboardRecyclerView;
    private PlayerAdapter mAdapter;


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
        Leaderboard leaderboard = Leaderboard.get(getActivity());
        List<User> players = leaderboard.getPlayers();

        mAdapter = new PlayerAdapter(players);
        mLeaderboardRecyclerView.setAdapter(mAdapter);
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


        public void bind(User user) {
            mUser = user;
            mRankTextView.setText(Integer.toString(Leaderboard.get(getContext()).getPlayers().indexOf(user) + 1));//TODO: figure out how to determine rank
            mUsernameTextView.setText(mUser.getEmail());
            mScoreTextView.setText(Integer.toString(mUser.getScore()));
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
            holder.bind(player);
        }

        @Override
        public int getItemCount() {
            return mPlayers.size();
        }
    }


}
