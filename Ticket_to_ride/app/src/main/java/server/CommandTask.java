package server;

import android.os.AsyncTask;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

/**
 * Created by haoyucn on 10/31/18.
 */

public class CommandTask extends AsyncTask<Object, Void, Void> {

    @Override
    public Void doInBackground(Object... params){

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        PresenterFacade.getInstance().updatePresenter(null);
    }


}
