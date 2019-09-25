package model;

import java.util.List;

/**
 * Created by urimaj on 11/19/18.
 */

public interface IModelObservable {

    void registerObserver(IModelObserver obs);
    void unregisterObserver(IModelObserver obs);
}
