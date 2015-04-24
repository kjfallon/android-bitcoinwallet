package edu.syr.cis.cis444.bitcoinwallet;


import android.util.Log;

import com.squareup.otto.Bus;

public final class OttoEventBus {

    private static final String TAG = OttoEventBus.class.getSimpleName();
    private static final Bus BUS = new Bus();

    private OttoEventBus() {

    }

    public static Bus getInstance() {
        Log.d(TAG, "providing Otto event bus instance");
        return BUS;
    }

}
