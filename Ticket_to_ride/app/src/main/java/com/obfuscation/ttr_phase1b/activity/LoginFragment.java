package com.obfuscation.ttr_phase1b.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.obfuscation.ttr_phase1b.R;

import communication.Result;
import model.ModelFacade;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements IPresenter {

    private static final String TAG = "LoginFrag";

    private String mUser;
    private String mPass;

    private EditText mUsername;
    private EditText mPassword;

    private Button mLogIn;
    private Button mRegister;

    private OnLoginListener mListener;

    public LoginFragment() {
        mUser = "";
        mPass = "";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        mUsername = (EditText) view.findViewById(R.id.username_input);
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUser = s.toString();
                changeAccessibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPassword = (EditText) view.findViewById(R.id.password_input);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPass = s.toString();
                changeAccessibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mLogIn = (Button) view.findViewById(R.id.login_button);
        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Now logging in");
//                Toast.makeText(getActivity(), "Attempting to log in", Toast.LENGTH_SHORT).show();
                ModelFacade.getInstance().login(mUser, mPass);
            }
        });

        mRegister = (Button) view.findViewById(R.id.register_button);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Now registering");
                ModelFacade.getInstance().register(mUser, mPass);
            }
        });

        mLogIn.setEnabled(false);
        mRegister.setEnabled(false);

        return view;
    }

    private void changeAccessibility() {
        if(!mUser.equals("") && !mPass.equals("")) {
            mLogIn.setEnabled(true);
            mRegister.setEnabled(true);
        }else {
            mLogIn.setEnabled(false);
            mRegister.setEnabled(false);
        }
    }

    @Override
    public void updateInfo(Object result) {
        if(result == null || result.getClass() != Result.class) {
            Log.d(TAG, "result is wrong type: " + result);
            Toast.makeText(getActivity(), "login failed: null result", Toast.LENGTH_LONG).show();
        }else {
            Result data = (Result) result;
            if(data.isSuccess()) {
                onLogin();
            }else {
                if(data.getErrorInfo() == null) {
                    Log.d(TAG, "null error msg: " + data);
                    Toast.makeText(getActivity(), "login failed: null error", Toast.LENGTH_LONG).show();
                }else {
                    String toastText = "login failed: " + data.getErrorInfo();
                    Log.d(TAG, toastText);
                    Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
                }
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

    //  sets up the activity as the listener so we can tell it when to change frags
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener) {
            mListener = (OnLoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGameLeaveListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    tells the listener (the activity) to change the fragment to the game list
    public void onLogin() {
        if (mListener != null) {
            mListener.onLogin();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnLoginListener {
        void onLogin();
    }

}
