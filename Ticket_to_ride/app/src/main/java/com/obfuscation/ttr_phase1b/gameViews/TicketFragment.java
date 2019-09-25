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

import java.util.ArrayList;
import java.util.List;

import communication.Message;
import communication.Ticket;
import gamePresenters.ITicketPresenter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketFragment extends Fragment implements ITicketView {

    private static final String TAG = "TicketFrag";
    private static final String ARG_PARAM1 = "param1";

    private ITicketPresenter mPresenter;

    private boolean mIsGameSetup;
    private List<Ticket> mTickets;
    private boolean[] mChosenTickets;

    private TextView mHeader;
    private Button mDoneButton;
    private TicketAdapter mTicketAdapter;
    private RecyclerView mTicketRecycler;

    public TicketFragment() {
        mChosenTickets = new boolean[3];
    }


    public static TicketFragment newInstance(String isGameSetup) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, isGameSetup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsGameSetup = false;
        if (getArguments() != null) {
            String arg1 = getArguments().getString(ARG_PARAM1);
            if(arg1.equals("t")) {
                mIsGameSetup = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPresenter.update();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        mHeader = (TextView) view.findViewById(R.id.ticket_header);
        if(mIsGameSetup) {
            mHeader.setText("Choose at Least 2");
        }

        mDoneButton = (Button) view.findViewById(R.id.ticket_done_button);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now choosing tickets");
                onDone();
            }
        });

        mTicketRecycler = (RecyclerView) view.findViewById(R.id.ticket_recycler_view);
        mTicketRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        changeAccessibility();

        updateUI();

        return view;
    }

    private void onDone() {
        List<Ticket> tickets = new ArrayList<>();
        for(int i = 0; i < mChosenTickets.length; i++) {
            if(mChosenTickets[i]) {
                tickets.add(mTickets.get(i));
            }
        }
        mPresenter.onFinish(tickets);
    }

    private void changeAccessibility() {
//      Check to see if it is our turn; if it is then we need to select 2 tickets
//      else we select 1
        int chosen = 0;
        for(int i = 0; i < mChosenTickets.length; i++) {
            if(mChosenTickets[i]) {
                ++chosen;
            }
        }
        if(mIsGameSetup && chosen >= 2) {
            mDoneButton.setEnabled(true);
        }else if(!mIsGameSetup && chosen >= 1) {
            mDoneButton.setEnabled(true);
        }else {
            mDoneButton.setEnabled(false);
        }
    }

    @Override
    public void setTickets(List<Ticket> tickets) {
        mTickets = tickets;
    }

    @Override
    public boolean isGameSetup() {
        return mIsGameSetup;
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
    public void sendToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT);
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (ITicketPresenter) presenter;
    }


    private class TicketHolder extends RecyclerView.ViewHolder {

        private int mTicket;
        private CheckBox mCheckbox;
        private TextView mDescription;
        private TextView mPoints;

        public TicketHolder(View view) {
            super(view);

            mCheckbox = view.findViewById(R.id.ticket_checkbox);
            mDescription = view.findViewById(R.id.ticket_description);
            mPoints = view.findViewById(R.id.ticket_points);
        }

        public void bind(Ticket ticket, int index) {
            mTicket = index;
            mDescription.setText(ticket.getCity1() + " to " + ticket.getCity2());
            mPoints.setText("" + ticket.getValue());
            if (mChosenTickets[mTicket]) {
                mCheckbox.setChecked(true);
            }
            mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {
                        mChosenTickets[mTicket] = true;
                        changeAccessibility();
                    } else {
                        mChosenTickets[mTicket] = false;
                        changeAccessibility();
                    }
                }

            });
        }

    }

    private class TicketAdapter extends RecyclerView.Adapter<TicketHolder> {

        private List<Ticket> mTickets;

        public TicketAdapter(List<Ticket> tickets) {
            mTickets = tickets;
        }

        public TicketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.ticket_view, parent, false);
            return new TicketHolder(view);
        }

        public void onBindViewHolder(TicketHolder holder, int position) {
            Log.d(TAG+"_adapter", "tickets[" + position + "]: " + mTickets.get(position));
            holder.bind(mTickets.get(position), position);
        }

        public int getItemCount() {
            return mTickets.size();
        }

    }

}
