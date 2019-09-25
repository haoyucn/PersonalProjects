package gamePresenters;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.obfuscation.ttr_phase1b.activity.IPresenter;

public interface IPTicketsPresenter extends IPresenter {

    void onClose(Fragment fragment);

    /**
     * Storing the previous IGamePresenter allows the activity to restore the
     * presenter when the fragment is removed
     * @param pres
     */
    void setPrevPresenter(IGamePresenter pres);

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    interface OnCloseListener {
        void onClose(Fragment fragment, IGamePresenter pres);
    }

}
