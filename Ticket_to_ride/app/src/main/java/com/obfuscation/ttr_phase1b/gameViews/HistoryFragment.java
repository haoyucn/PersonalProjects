package com.obfuscation.ttr_phase1b.gameViews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.ArrayList;
import java.util.List;

import communication.GameHistory;
import gamePresenters.IHistoryPresenter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements IHistoryView {

    private static final String TAG = "HistFrag";

    private IHistoryPresenter mPresenter;

    private List<GameHistory> mHistory;

    private Button mBackButton;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mHistoryRecycler;

    public HistoryFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HistoryFragment.
     */
    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mBackButton = (Button) view.findViewById(R.id.history_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now choosing tickets");
                mPresenter.onBack();
            }
        });

        mHistoryRecycler = (RecyclerView) view.findViewById(R.id.history_recycler_view);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPresenter.updateInfo(null);

        return view;
    }

    @Override
    public void setHistory(List<GameHistory> history) {
        mHistory = history;
    }

    @Override
    public void updateUI() {
        Log.d(TAG, "getting updated");
        if(mHistoryRecycler != null && mHistory != null) {
            Log.d(TAG+"_updateUI", "history: " + mHistory);
            mHistoryAdapter = new HistoryAdapter(mHistory);
            mHistoryRecycler.setAdapter(mHistoryAdapter);
        }
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IHistoryPresenter) presenter;
    }

    @Override
    public void sendToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mDescription;

        public HistoryHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.playername);
            mDescription = view.findViewById(R.id.history_text);
        }

        public void bind(GameHistory history) {
            mName.setText("" + history.getPlayerName());
            mDescription.setText("" + history.getAction());
        }

    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<GameHistory> mHistory;

        public HistoryAdapter(List<GameHistory> history) {
            mHistory = history;
        }

        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.history_view, parent, false);
            return new HistoryHolder(view);
        }

        public void onBindViewHolder(HistoryHolder holder, int position) {
            Log.d(TAG+"_adapter", "history[" + position + "]: " + mHistory.get(position));
            holder.bind(mHistory.get(position));
        }

        public int getItemCount() {
            return mHistory.size();
        }

    }

}
