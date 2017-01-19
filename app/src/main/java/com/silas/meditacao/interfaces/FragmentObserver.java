package com.silas.meditacao.interfaces;

import java.util.Calendar;
import java.util.Observable;

/**
 * Created by silas on 18/01/17.
 */

public class FragmentObserver extends Observable {
    @Override
    public void notifyObservers() {
        setChanged(); // Set the changed flag to true, otherwise observers won't be notified.
        super.notifyObservers();
    }
}
