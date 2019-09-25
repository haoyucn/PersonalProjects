package com.obfuscation.ttr_phase1b.gameViews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;

import java.util.List;

import communication.Message;
import gamePresenters.IChatPresenter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class ChatFragment extends Fragment implements IChatView {

    private static final String TAG = "ChatFrag";

    private IChatPresenter mPresenter;

    private String mUsername;

    private List<Message> mMessages;

    private Message mPlayerMessage;

    private EditText mPlayerMessageText;
    private Button mBackButton;
    private Button mSendButton;

    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPresenter.update();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mPlayerMessage = new Message(mUsername);
        mPlayerMessageText = (EditText) view.findViewById(R.id.player_message);
        mPlayerMessageText.setText("");
        mPlayerMessageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlayerMessage.setText(s.toString());
                changeAccessibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBackButton = view.findViewById(R.id.chat_back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now sending message");
                mPresenter.goBack();
            }
        });

        mSendButton = (Button) view.findViewById(R.id.send_message_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now sending message");
                mPresenter.onSend(mPlayerMessage);
                mPlayerMessage = new Message(mUsername);
                mPlayerMessageText.setText("");
            }
        });

        mMessageRecycler = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        changeAccessibility();

        updateUI();

        return view;
    }

    private void changeAccessibility() {
        if(mPlayerMessage.getText().equals("")) {
            mSendButton.setEnabled(false);
        }else {
            mSendButton.setEnabled(true);
        }
    }

    @Override
    public void setMessages(List<Message> messages) {
        mMessages = messages;
    }

    @Override
    public void setUsername(String username) {
        mUsername = username;
    }

    @Override
    public void updateUI() {
        Log.d(TAG, "getting updated");
        if(mMessageRecycler != null && mMessages != null) {
            Log.d(TAG+"_updateUI", "gamelist: " + mMessages);
            mMessageAdapter = new MessageAdapter(mMessages);
            mMessageRecycler.setAdapter(mMessageAdapter);
        }
    }

    @Override
    public void sendToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(IPresenter presenter) {
        mPresenter = (IChatPresenter) presenter;
    }

    private class MessageHolder extends RecyclerView.ViewHolder {

//        private Message mMessage;
        private TextView mPlayerNameView;
        private TextView mMessageView;

        public MessageHolder(View view) {
            super(view);

            mPlayerNameView = view.findViewById(R.id.player_name);
            mMessageView = view.findViewById(R.id.message);
        }

        public void bind(Message message) {
//            mMessage = message;
            mPlayerNameView.setText(message.getPlayerID());
            mMessageView.setText(message.getText());
        }

    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        private List<Message> mMessages;

        public MessageAdapter(List<Message> messages) {
            mMessages = messages;
        }

        public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.fragment_chat_list, parent, false);
            return new MessageHolder(view);
        }

        public void onBindViewHolder(MessageHolder holder, int position) {
            Log.d(TAG+"_adapter", "message[" + position + "]: " + mMessages.get(position));
            holder.bind(mMessages.get(position));
        }

        public int getItemCount() {
            return mMessages.size();
        }

    }

}
