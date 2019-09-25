package com.obfuscation.ttr_phase1b.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;

import java.util.ArrayList;

import communication.LobbyGame;
import communication.Result;
import model.ModelFacade;
import communication.Player;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameCreationFragment.OnGameCreationLister} interface
 * to handle interaction events.
 * Use the {@link GameCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameCreationFragment extends Fragment implements IPresenter {

    private static final String TAG = "GameCreationFrag";

    private String mUser;
    //all game need to be swiched to gameClient
    private LobbyGame mGame;

    private Button mCancel;
    private Button mCreate;

    private RadioGroup mMaxPlayersRadio;

    private TextView mGameIdView;

    private OnGameCreationLister mListener;

    public GameCreationFragment() {
        mUser = ModelFacade.getInstance().getUserName();
        ArrayList<Player> l = new ArrayList<>();
        l.add(new Player(mUser));
        mGame = new LobbyGame("", mUser, l, 2);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment GameCreationFragment.
     */
    public static GameCreationFragment newInstance() {
        GameCreationFragment fragment = new GameCreationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_creation_fragment, container, false);

        mCancel = (Button) view.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now cancelling");
                onFinish("cancel");

            }
        });

        mCreate = (Button) view.findViewById(R.id.create_button);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now creating");
                ModelFacade.getInstance().createLobbyGame(mGame);
            }
        });

        mMaxPlayersRadio = (RadioGroup) view.findViewById(R.id.max_players);
        mMaxPlayersRadio.check(R.id.players_2);
        mMaxPlayersRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.players_2:
                        mGame.setMaxPlayers(2);
                        break;
                    case R.id.players_3:
                        mGame.setMaxPlayers(3);
                        break;
                    case R.id.players_4:
                        mGame.setMaxPlayers(4);
                        break;
                    case R.id.players_5:
                        mGame.setMaxPlayers(5);
                        break;
                }
            }
        });

        mGameIdView = (EditText) view.findViewById(R.id.game_id_edit);
        mGameIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGame.setGameID(s.toString());
                changeAccessibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCreate.setEnabled(false);

        return view;
    }

    private void changeAccessibility() {
        if(mGame.getGameID().equals("")) {
            mCreate.setEnabled(false);
        }else {
            mCreate.setEnabled(true);
        }
    }

    @Override
    public void updateInfo(Object result) {
        Log.d(TAG, "getting updated");
        if(result == null || result.getClass() != Result.class) {
            Log.d(TAG, "result is wrong type: " + result);
            Toast.makeText(getActivity(), "create failed: null result", Toast.LENGTH_LONG).show();
        }else {
            Result data = (Result) result;
//          if create game worked, go to the new game lobby
            if(data.isSuccess()) {
                Log.d(TAG, "success; now creating");
                onFinish("create");
            }else {
                Log.d(TAG, "create failed: " + data.getErrorInfo());
                Toast.makeText(getActivity(), "create failed: " + data.getErrorInfo(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();
    }


    //    tells the activity to change the frag to the lobby if selection == "create"
//    and game list is selection == "cancel"
    public void onFinish(String selection) {
        if (mListener != null) {
            mListener.onFinishCreating(selection);
        }
    }

//  sets up the activity as the listener so we can tell it when to change frags
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameCreationLister) {
            mListener = (OnGameCreationLister) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnGameCreationLister {
        void onFinishCreating(String selection);
    }
}
