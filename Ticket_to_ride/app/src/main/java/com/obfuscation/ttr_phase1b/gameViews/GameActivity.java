package com.obfuscation.ttr_phase1b.gameViews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.obfuscation.ttr_phase1b.R;
import com.obfuscation.ttr_phase1b.activity.IPresenter;
import com.obfuscation.ttr_phase1b.activity.PresenterFacade;
import com.obfuscation.ttr_phase1b.gameViews.dummy.CardDialog;

import gamePresenters.CardSelectPresenter;
import gamePresenters.ChatPresenter;
import gamePresenters.GamePresenter;
import gamePresenters.HistoryPresenter;
import gamePresenters.ICardSelectPresenter;
import gamePresenters.IChatPresenter;
import gamePresenters.IGamePresenter;
import gamePresenters.IHistoryPresenter;
import gamePresenters.IMenuPresenter;
import gamePresenters.IPInfoPresenter;
import gamePresenters.IPTicketsPresenter;
import gamePresenters.IScorePresenter;
import gamePresenters.ITicketPresenter;
import gamePresenters.PInfoPresenter;
import gamePresenters.PTicketsPresenter;
import gamePresenters.ScorePresenter;
import gamePresenters.Shows;
import gamePresenters.TicketPresenter;

public class GameActivity extends AppCompatActivity implements IGamePresenter.OnShowListener,
        IChatPresenter.OnBackListener, ITicketPresenter.OnBackListener,
        ICardSelectPresenter.OnBackListener, CardDialog.CardDialogListener,
        IMenuPresenter.MenuListener, IScorePresenter.OnReturnListener,
        IPTicketsPresenter.OnCloseListener, IHistoryPresenter.OnBackListener {

    private static final String TAG = "GameActivity";

    public static Intent newIntent(Context context) {
        return new Intent(context, GameActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null) {
            fragment = TicketFragment.newInstance("t");
            PresenterFacade.getInstance().setPresenter(new TicketPresenter((ITicketView) fragment, this));

            fm.beginTransaction().add(R.id.container, fragment).commit();
            Log.d(TAG, "Loaded the ticket fragment");
        }
    }

    @Override
    public void onShow(Shows show, Bundle args) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment;
        switch (show) {
            case chat:
                fragment = ChatFragment.newInstance();
                PresenterFacade.getInstance().setPresenter( new ChatPresenter((IChatView) fragment, this) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case tickets:
                fragment = TicketFragment.newInstance("f");
                PresenterFacade.getInstance().setPresenter( new TicketPresenter((ITicketView) fragment, this) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case playerInfo:
                fragment = PlayerInfoDialogFragment.newInstance();
                IPresenter prevPres = PresenterFacade.getInstance().getPresenter();
                if (prevPres instanceof IGamePresenter) {
                    PresenterFacade.getInstance().setPresenter( new PInfoPresenter((IPlayerInfoView) fragment, this, (IGamePresenter) prevPres));
                    ((IPInfoPresenter) PresenterFacade.getInstance().getPresenter()).showPlayerInfo((IPlayerInfoView) fragment);
                    fm.beginTransaction().add(R.id.container, fragment).commit();
                }
                break;
            case map:
                fragment = GameFragment.newInstance();
                PresenterFacade.getInstance().setPresenter( new GamePresenter((IGameView) fragment, this) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case cardSelect:
                fragment = CardSelectFragment.newInstance();
                fragment.setArguments(args);
                PresenterFacade.getInstance().setPresenter( new CardSelectPresenter((ICardSelectView) fragment, this, args) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case score:
                fragment = ScoreFragment.newInstance();
                PresenterFacade.getInstance().setPresenter( new ScorePresenter((IScoreView) fragment, this) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
            case owned_tickets:
                fragment = PTicketsFragment.newInstance();
                IPresenter oldPres = PresenterFacade.getInstance().getPresenter();
                if (oldPres instanceof IGamePresenter) {
                    PresenterFacade.getInstance().setPresenter( new PTicketsPresenter((IPTicketsView) fragment, this, (IGamePresenter) oldPres) );
                    fm.beginTransaction().add(R.id.container, fragment).commit();
                }
                break;
            case history:
                fragment = HistoryFragment.newInstance();
                PresenterFacade.getInstance().setPresenter( new HistoryPresenter((IHistoryView) fragment, this) );
                fm.beginTransaction().replace(R.id.container, fragment).commit();
                break;
        }
        Log.d(TAG, "showing a " + show + " fragment");
    }

    @Override
    public void onBack() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = GameFragment.newInstance();
        PresenterFacade.getInstance().setPresenter( new GamePresenter((IGameView) fragment, this) );
        fm.beginTransaction().replace(R.id.container, fragment).commit();
        Log.d(TAG, "Loaded the game fragment back in");
    }

    @Override
    public void onClose(Fragment fragment, IGamePresenter presenter) {
        FragmentManager fm = getSupportFragmentManager();

        if(presenter != null) {
            PresenterFacade.getInstance().setPresenter(presenter);
        }

        fm.beginTransaction().remove(fragment).commit();
        Log.d(TAG, "Removed fragment");
    }

    @Override
    public void onReturn() {
//        FragmentManager fm = getSupportFragmentManager();
//
//        Fragment fragment = GameFragment.newInstance();
//        PresenterFacade.newInstance().setPresenter( new GamePresenter((IGameView) fragment, this) );
//        fm.beginTransaction().replace(R.id.container, fragment).commit();
        Log.d(TAG, "Going back to gamelistview");
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onGameSelect() {

    }

    @Override
    public void onConfirmCards(CardDialog dialog) {
        try{
            ICardSelectView fragment = (ICardSelectView) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.onConfirmCards(dialog);

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRejectCards(DialogFragment dialog) {
        try{
            ICardSelectView fragment = (ICardSelectView) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.onRejectCards(dialog);

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
