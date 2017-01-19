package com.silas.meditacao.interfaces;

import java.util.Calendar;

/**
 * Created by silas on 19/01/17.
 */

public interface Updateable {
    void onUpdate(Calendar dia, int tipo);
}