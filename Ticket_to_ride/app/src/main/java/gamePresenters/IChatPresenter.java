package gamePresenters;

import com.obfuscation.ttr_phase1b.activity.IPresenter;

import communication.Message;

public interface IChatPresenter extends IPresenter {

    void goBack();
    void onSend(Message message);

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnBackListener {
        void onBack();
    }

}
