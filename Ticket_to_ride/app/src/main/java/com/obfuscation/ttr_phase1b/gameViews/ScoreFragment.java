package com.obfuscation.ttr_phase1b.gameViews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.BlankFragment;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.ArrayList;
import java.util.List;

import communication.PlayerStats;
import gamePresenters.IScorePresenter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScoreFragment} interface
 * to handle interaction events.
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment implements IScoreView {

    private static final String TAG = "ScoreFrag";

    private IScorePresenter mPresenter;

    private ArrayList<PlayerStats> mStats;

    private ScoreAdapter mScoreAdapter;
    private RecyclerView mScoreRecycler;

    public ScoreFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ScoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreFragment newInstance() {
        ScoreFragment fragment = new ScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_score, container, false);

        mScoreRecycler = (RecyclerView) view.findViewById(R.id.score_recycler_view);
        mScoreRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        Log.d(TAG, "onCreateView: " + mScoreRecycler);

        if(mPresenter != null) {
            mPresenter.updateInfo(null);
        }

        updateUI();

        return view;
    }

    @Override
    public void setInfo(ArrayList<PlayerStats> stats) {
        mStats = stats;
    }

    @Override
    public void updateUI() {
        Log.d(TAG+"_updateUI", "stats: " + mStats);
        Log.d(TAG, "updateUI: " + mScoreRecycler);
        if(mScoreRecycler != null && mStats != null) {
            Log.d(TAG, "updateUI: (in function) " + mStats);
            mScoreAdapter = new ScoreAdapter(mStats);
            mScoreRecycler.setAdapter(mScoreAdapter);
        }
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IScorePresenter) presenter;
    }

    @Override
    public void sendToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT);
    }

    private class ScoreHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mRank;
        private TextView mTotScore;
        private TextView mTickScore;
        private TextView mTickLoss;
        private TextView mLongest;

        public ScoreHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.score_name);
            mRank = view.findViewById(R.id.score_rank);
            mTotScore = view.findViewById(R.id.score_totalscore);
            mTickScore = view.findViewById(R.id.score_ticketscore);
            mTickLoss = view.findViewById(R.id.score_ticketloss);
            mLongest = view.findViewById(R.id.score_longest);
        }

        public void bind(PlayerStats stats, int index) {
            mName.setText(stats.getName());
            if(index == 1) {
                mRank.setText("Winner " + index);
            }else {
                mRank.setText("Loser " + index);
            }
            mTotScore.setText("" + stats.getTotalPoint());
            mTickScore.setText("" + stats.getWinnedPoint());
            mTickLoss.setText("" + stats.getLostPoint());
            if(stats.isHasLongestPath()) {
                mLongest.setText("ye boi");
            }else {
                mLongest.setText("loser");
            }
        }

    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreHolder> {

        private List<PlayerStats> mStats;

        public ScoreAdapter(List<PlayerStats> stats) {
            mStats = stats;
        }

        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.score_view, parent, false);
            return new ScoreHolder(view);
        }

        public void onBindViewHolder(ScoreHolder holder, int position) {
            Log.d(TAG+"_adapter", "stats[" + position + "]: " + mStats.get(position));
            holder.bind(mStats.get(position), position+1);
        }

        public int getItemCount() {
            return mStats.size();
        }

    }

}
