package com.obfuscation.ttr_phase1b.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.obfuscation.ttr_phase1b.R;

public class LoginActivity extends FragmentActivity implements LoginFragment.OnLoginListener,
        LobbyFragment.OnGameLeaveListener, GameListFragment.OnGameSelectListener,
        GameCreationFragment.OnGameCreationLister {

    private static final String TAG = "loginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null) {
            fragment = LoginFragment.newInstance();
            PresenterFacade.getInstance().setPresenter((LoginFragment) fragment);
            fm.beginTransaction().add(R.id.container, fragment).commit();
            Log.d(TAG, "Loaded the login fragment");
        }
    }

    @Override
    public void onLogin() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = GameListFragment.newInstance();
        PresenterFacade.getInstance().setPresenter((GameListFragment) fragment);
        fm.beginTransaction().replace(R.id.container, fragment).commit();
        Log.d(TAG, "Loaded the game list fragment");
    }

    @Override
    public void onGameSelect(String selection) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if(selection == "join") {
            fragment = LobbyFragment.newInstance();
            Log.d(TAG, "Loaded the lobby fragment");
            PresenterFacade.getInstance().setPresenter((LobbyFragment) fragment);
        }else if(selection == "create") {
            fragment = GameCreationFragment.newInstance();
            Log.d(TAG, "Loaded the create game fragment");
            PresenterFacade.getInstance().setPresenter((GameCreationFragment) fragment);
        }
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onFinishCreating(String selection) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if(selection.equals("create")) {
            fragment = LobbyFragment.newInstance();
            Log.d(TAG, "Loaded the lobby fragment");
            PresenterFacade.getInstance().setPresenter((LobbyFragment) fragment);
        }else if(selection.equals("cancel")) {
            fragment = GameListFragment.newInstance();
            Log.d(TAG, "Loaded the game list fragment");
            PresenterFacade.getInstance().setPresenter((GameListFragment) fragment);
        }
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onGameLeave() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = GameListFragment.newInstance();
        PresenterFacade.getInstance().setPresenter((GameListFragment) fragment);
        fm.beginTransaction().replace(R.id.container, fragment).commit();
        Log.d(TAG, "Loaded the game list fragment");
    }

}
