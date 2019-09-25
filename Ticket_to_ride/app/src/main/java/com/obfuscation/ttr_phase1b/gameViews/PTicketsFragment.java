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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.List;

import communication.Ticket;
import gamePresenters.IGamePresenter;
import gamePresenters.IPTicketsPresenter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PTicketsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PTicketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PTicketsFragment extends Fragment implements IPTicketsView {

    private static final String TAG = "PTicketsFrag";

    private IPTicketsPresenter mPresenter;

    private List<Ticket> mTickets;

    private Button mBackButton;
    private TicketAdapter mTicketAdapter;
    private RecyclerView mTicketRecycler;

    PTicketsFragment self;

    public PTicketsFragment() {
        self = this;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PTicketsFragment.
     */
    public static PTicketsFragment newInstance() {
        PTicketsFragment fragment = new PTicketsFragment();
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
        View view = inflater.inflate(R.layout.fragment_ptickets, container, false);

        mBackButton = (Button) view.findViewById(R.id.ptickets_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now choosing tickets");
                mPresenter.onClose(self);
            }
        });

        mTicketRecycler = (RecyclerView) view.findViewById(R.id.ptickets_recycler_view);
        mTicketRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPresenter.updateInfo(null);

        return view;
    }

    @Override
    public void setTickets(List<Ticket> tickets) {
        mTickets = tickets;
    }

    @Override
    public void updateUI() {
        Log.d(TAG, "getting updated");
        if(mTicketRecycler != null && mTickets != null) {
            Log.d(TAG+"_updateUI", "ticketlist: " + mTickets);
            mTicketAdapter = new TicketAdapter(mTickets);
            mTicketRecycler.setAdapter(mTicketAdapter);
        }
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IPTicketsPresenter) presenter;
    }

    @Override
    public void sendToast(String toast) {
        Log.d(TAG, "sendToast: " + toast);
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT);
    }

    private class TicketHolder extends RecyclerView.ViewHolder {

        private TextView mDescription;
        private TextView mPoints;

        public TicketHolder(View view) {
            super(view);

            mDescription = view.findViewById(R.id.ticket_description);
            mPoints = view.findViewById(R.id.ticket_points);
        }

        public void bind(Ticket ticket) {
            mDescription.setText(ticket.getCity1() + " to " + ticket.getCity2());
            mPoints.setText("" + ticket.getValue());
        }

    }

    private class TicketAdapter extends RecyclerView.Adapter<TicketHolder> {

        private List<Ticket> mTickets;

        public TicketAdapter(List<Ticket> tickets) {
            mTickets = tickets;
        }

        public TicketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.pticket_view, parent, false);
            return new TicketHolder(view);
        }

        public void onBindViewHolder(TicketHolder holder, int position) {
            Log.d(TAG+"_adapter", "tickets[" + position + "]: " + mTickets.get(position));
            holder.bind(mTickets.get(position));
        }

        public int getItemCount() {
            return mTickets.size();
        }

    }

}
