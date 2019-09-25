package task;

import android.os.AsyncTask;

import com.obfuscation.ttr_phase1b.activity.PresenterFacade;

/**
 * Created by hao on 11/19/18.
 */

public class PresenterUpdateTask extends AsyncTask<Object, Void, Object> {
    @Override
    protected Object doInBackground(Object... objects) {
        return objects[0];
    }


    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        PresenterFacade.getInstance().updatePresenter(o);
    }
}
